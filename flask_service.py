from flask import Flask, request, jsonify, abort
import torch
import time
import logging
import os
import sys
import subprocess
import tempfile

from transformers import (
    AutoTokenizer,
    AutoModelForSequenceClassification,
    AutoModelForSeq2SeqLM,
    AutoModelForSpeechSeq2Seq,
    AutoProcessor,
    pipeline
)

# ─────────────────────────────
# CONFIG
# ─────────────────────────────
os.environ["HF_HOME"] = r"D:\huggingface_cache"

if hasattr(sys.stdout, "reconfigure"):
    sys.stdout.reconfigure(encoding="utf-8")

FFMPEG_PATH = r"D:\ffmpeg\bin"
os.environ["PATH"] += f";{FFMPEG_PATH}"

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = Flask(__name__)

app.config["MAX_CONTENT_LENGTH"] = 200 * 1024 * 1024

# ─────────────────────────────
# MODELS
# ─────────────────────────────
MODEL_ID = "unitary/toxic-bert"

LABELS = [
    "toxicity",
    "severe_toxicity",
    "obscene",
    "threat",
    "insult",
    "identity_attack"
]

model = None
tokenizer = None
translator_model = None
translator_tokenizer = None
whisper_pipe = None


# ─────────────────────────────
# LOAD MODELS (ONLY TOXIC + TRANSLATOR)
# ─────────────────────────────
def load_model():

    global model, tokenizer, translator_model, translator_tokenizer

    logger.info("Loading toxic model...")

    tokenizer = AutoTokenizer.from_pretrained(MODEL_ID)
    model = AutoModelForSequenceClassification.from_pretrained(MODEL_ID)
    model.eval()

    logger.info("Loading translator...")

    translator_tokenizer = AutoTokenizer.from_pretrained(
        "Helsinki-NLP/opus-mt-ar-en"
    )

    translator_model = AutoModelForSeq2SeqLM.from_pretrained(
        "Helsinki-NLP/opus-mt-ar-en"
    )

    translator_model.eval()

    logger.info("Core models loaded")


# ─────────────────────────────
# HELPERS
# ─────────────────────────────
def is_arabic(text):
    return any("\u0600" <= c <= "\u06FF" for c in text)


def translate(text):
    inputs = translator_tokenizer(
        text,
        return_tensors="pt",
        truncation=True,
        padding=True,
        max_length=512
    )

    with torch.no_grad():
        out = translator_model.generate(**inputs)

    return translator_tokenizer.decode(out[0], skip_special_tokens=True)


def preprocess_text(text):
    if is_arabic(text):
        return translate(text), True
    return text, False


def infer(texts, threshold=0.5):

    inputs = tokenizer(
        texts,
        return_tensors="pt",
        truncation=True,
        padding=True,
        max_length=512
    )

    with torch.no_grad():
        logits = model(**inputs).logits
        probs = torch.sigmoid(logits).cpu().numpy()

    results = []

    for text, row in zip(texts, probs):

        scores = {l: float(v) for l, v in zip(LABELS, row)}

        max_label = max(scores, key=scores.get)

        results.append({
            "text": text,
            "scores": scores,
            "is_toxic": max(scores.values()) >= threshold,
            "max_label": max_label,
            "max_score": scores[max_label]
        })

    return results


def extract_audio(video_path, audio_path):
    cmd = [
        "ffmpeg", "-y",
        "-i", video_path,
        "-vn",
        "-ar", "16000",
        "-ac", "1",
        "-acodec", "pcm_s16le",
        audio_path
    ]

    subprocess.run(cmd, capture_output=True)


# ─────────────────────────────
# ROUTES
# ─────────────────────────────
@app.get("/health")
def health():
    return jsonify({"status": "ok", "model": MODEL_ID})


# ─────────────────────────────
# TEXT
# ─────────────────────────────
@app.post("/analyze")
def analyze():

    body = request.get_json(force=True)

    text = body.get("text", "").strip()
    threshold = float(body.get("threshold", 0.5))

    if not text:
        abort(400, "text required")

    text_en, tr = preprocess_text(text)

    t0 = time.perf_counter()
    res = infer([text_en], threshold)[0]

    res.update({
        "original_text": text,
        "translated_text": text_en,
        "was_translated": tr,
        "latency_ms": round((time.perf_counter() - t0) * 1000, 2)
    })

    return jsonify(res)


@app.post("/analyze/batch")
def analyze_batch():

    body = request.get_json(force=True)
    texts = body.get("texts", [])
    threshold = float(body.get("threshold", 0.5))

    processed = []
    meta = []

    for t in texts:
        t_en, tr = preprocess_text(t)
        processed.append(t_en)
        meta.append({
            "original_text": t,
            "translated_text": t_en,
            "was_translated": tr
        })

    results = infer(processed, threshold)

    for i in range(len(results)):
        results[i].update(meta[i])

    return jsonify({
        "results": results
    })

# ─────────────────────────────
# VIDEO (NO AUTO WHISPER LOAD AT START)
# ─────────────────────────────
@app.post("/analyze/video")
def analyze_video():

    if "file" not in request.files:
        abort(400, "file required")

    file = request.files["file"]

    # ✔ اللغة جاية من الـ body زي ما طلبتي
    language = request.form.get("language", "ar")
    threshold = float(request.form.get("threshold", 0.5))

    with tempfile.NamedTemporaryFile(suffix=".mp4", delete=False) as tmp:
        video_path = tmp.name
        file.save(video_path)

    audio_path = video_path + ".wav"

    try:
        extract_audio(video_path, audio_path)

        # ─────────────────────────────
        # LOAD WHISPER ONLY WHEN NEEDED
        # ─────────────────────────────
        global whisper_pipe

        if whisper_pipe is None:

            logger.info("Loading Whisper on demand...")

            whisper_model = AutoModelForSpeechSeq2Seq.from_pretrained(
                "openai/whisper-small",
                torch_dtype=torch.float32
            ).to("cpu")

            processor = AutoProcessor.from_pretrained(
                "openai/whisper-small"
            )

            whisper_pipe = pipeline(
                "automatic-speech-recognition",
                model=whisper_model,
                tokenizer=processor.tokenizer,
                feature_extractor=processor.feature_extractor,
                device="cpu"
            )

        t0 = time.perf_counter()

        asr = whisper_pipe(
            audio_path,
            return_timestamps=True,
            generate_kwargs={
                "task": "transcribe",
                "language": language,   # ✔ من البودي
                "num_beams": 1
            }
        )

        whisper_ms = round((time.perf_counter() - t0) * 1000, 2)

        transcript = asr["text"]
        chunks = asr.get("chunks", [])

        processed_chunks = []

        for c in chunks:

            text_en, tr = preprocess_text(c["text"])

            r = infer([text_en], threshold)[0]

            processed_chunks.append({
                "start": c["timestamp"][0],
                "end": c["timestamp"][1],
                "text": c["text"],
                "translated_text": text_en,
                "was_translated": tr,
                "scores": r["scores"],
                "is_toxic": r["is_toxic"]
            })

        text_en, tr = preprocess_text(transcript)

        final = infer([text_en], threshold)[0]

    finally:
        os.remove(video_path)
        os.remove(audio_path)

    return jsonify({
        "transcript": transcript,
        "translated_text": text_en,
        "was_translated": tr,
        "chunks": processed_chunks,
        "scores": final["scores"],
        "is_toxic": final["is_toxic"],
        "max_label": final["max_label"],
        "max_score": final["max_score"],
        "whisper_ms": whisper_ms
    })


# ─────────────────────────────
# MAIN
# ─────────────────────────────
if __name__ == "__main__":

    import multiprocessing
    multiprocessing.freeze_support()

    logger.info("Starting service...")

    load_model()   # ✔ بس toxic + translator

    app.run(host="0.0.0.0", port=5003)