import torch
import os
import subprocess
import sys
from transformers import AutoModelForSpeechSeq2Seq, AutoProcessor, pipeline

# ← هنا قبل أي حاجة تانية
os.environ["HF_HOME"] = r"D:\huggingface_cache"

sys.stdout.reconfigure(encoding='utf-8')

FFMPEG_PATH = r"D:\ffmpeg\bin"
if FFMPEG_PATH not in os.environ["PATH"]:
    os.environ["PATH"] += f";{FFMPEG_PATH}"

def extract_audio(video_path: str, output_path: str = "audio_temp.wav") -> str:
    if not os.path.exists(video_path):
        raise FileNotFoundError(f"Video not found: {video_path}")
    
    command = [
        "ffmpeg", "-y",
        "-i", video_path,
        "-vn",
        "-acodec", "pcm_s16le",
        "-ar", "16000",
        "-ac", "1",
        output_path
    ]
    result = subprocess.run(command, capture_output=True, text=True)
    if result.returncode != 0:
        raise RuntimeError(f"ffmpeg Error:\n{result.stderr}")
    
    print(f"[+] Audio extracted successfully")
    return output_path


def load_model():
    print("[+] Loading model...")
    
    device = "cpu"
    torch_dtype = torch.float32

    model_id = "openai/whisper-medium"
    
    model = AutoModelForSpeechSeq2Seq.from_pretrained(
        model_id,
        dtype=torch_dtype,
        low_cpu_mem_usage=True,
        use_safetensors=True
    )
    model.to(device)
    
    processor = AutoProcessor.from_pretrained(model_id)
    
    pipe = pipeline(
        "automatic-speech-recognition",
        model=model,
        tokenizer=processor.tokenizer,
        feature_extractor=processor.feature_extractor,
        device=device,
    )
    
    print("[+] Model loaded successfully!")
    return pipe


def transcribe(video_path: str, language: str = "arabic"):
    pipe = load_model()
    audio_path = extract_audio(video_path)
    
    print("[+] Transcribing...")
    result = pipe(
        audio_path,
        return_timestamps=True,
        generate_kwargs={
            "language": language,
            "task": "transcribe",
            "num_beams": 1,
        }
    )
    
    os.remove(audio_path)
    
    print("\n" + "="*50)
    print("FULL TEXT:")
    print("="*50)
    print(result["text"])
    
    if "chunks" in result:
        print("\n" + "="*50)
        print("TEXT WITH TIMESTAMPS:")
        print("="*50)
        for chunk in result["chunks"]:
            start, end = chunk["timestamp"]
            print(f"[{start:.1f}s -> {end:.1f}s]  {chunk['text']}")
    
    output_file = video_path.rsplit(".", 1)[0] + "_transcript.txt"
    with open(output_file, "w", encoding="utf-8") as f:
        f.write(result["text"])
    print(f"\n[+] Transcript saved to: {output_file}")
    
    return result


if __name__ == "__main__":
    VIDEO_PATH = r"D:\violence_detection_project\nonEthics\vv.mp4"
    LANGUAGE = "arabic"
    transcribe(VIDEO_PATH, LANGUAGE)