from fastapi import FastAPI, Request, UploadFile, File
import cv2
import numpy as np
import mediapipe as mp
import tempfile
import os

app = FastAPI()

mp_pose = mp.solutions.pose
pose = mp_pose.Pose(static_image_mode=True, min_detection_confidence=0.5)

LOWER = np.array([0, 40, 70], dtype=np.uint8)
UPPER = np.array([25, 170, 255], dtype=np.uint8)

SKIN_RATIO_THRESHOLD = 0.30
BLOCK_THRESHOLD = 0.70

SAFE_RESULT = {
    "detected": False,
    "category": "NONE",
    "confidence": 0.0,
    "action": "ALLOWED",
    "contentType": "IMAGE",
}


def analyze_frame(frame):
    h, w, _ = frame.shape
    hsv = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)
    skin_mask = cv2.inRange(hsv, LOWER, UPPER)

    rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
    results = pose.process(rgb)

    detected = False
    confidence = 0.0

    if results.pose_landmarks:
        skin_area = cv2.countNonZero(skin_mask)
        total_area = h * w
        ratio = skin_area / total_area

        if ratio > SKIN_RATIO_THRESHOLD:
            detected = True
            confidence = min(ratio * 2, 1.0)

    action = "ALLOWED"
    if detected:
        action = "BLOCKED" if confidence > BLOCK_THRESHOLD else "FLAGGED"

    return {
        "detected": detected,
        "category": "ADULT" if detected else "NONE",
        "confidence": round(confidence, 2),
        "action": action,
        "contentType": "IMAGE",
    }


@app.get("/health")
def health():
    return {"status": "ok"}


@app.post("/analyze")
async def analyze(request: Request):
    frame_bytes = await request.body()

    if not frame_bytes:
        return SAFE_RESULT

    nparr = np.frombuffer(frame_bytes, np.uint8)
    if nparr.size == 0:
        return SAFE_RESULT

    frame = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
    if frame is None:
        return SAFE_RESULT

    return analyze_frame(frame)


# ================= VIDEO =================
@app.post("/analyze-video")
async def analyze_video(file: UploadFile = File(...)):
    # Save uploaded video to a temp file
    suffix = os.path.splitext(file.filename)[1] or ".mp4"
    with tempfile.NamedTemporaryFile(delete=False, suffix=suffix) as tmp:
        tmp.write(await file.read())
        tmp_path = tmp.name

    cap = cv2.VideoCapture(tmp_path)
    total_frames = 0
    unsafe_frames = 0
    detections_per_frame = []
    max_confidence = 0.0
    overall_detected = False

    try:
        while True:
            ret, frame = cap.read()
            if not ret:
                break

            total_frames += 1
            result = analyze_frame(frame)
            detections_per_frame.append(result)

            if result["detected"]:
                unsafe_frames += 1
                overall_detected = True
                if result["confidence"] > max_confidence:
                    max_confidence = result["confidence"]
    finally:
        cap.release()
        try:
            os.remove(tmp_path)
        except:
            pass

    action = "ALLOWED"
    if overall_detected:
        action = "BLOCKED" if max_confidence > BLOCK_THRESHOLD else "FLAGGED"

    return {
        "detected": overall_detected,
        "category": "ADULT" if overall_detected else "NONE",
        "confidence": round(max_confidence, 2),
        "action": action,
        "contentType": "VIDEO",
        "total_frames": total_frames,
        "unsafe_frames": unsafe_frames,
        "detections_per_frame": detections_per_frame
    }