"""
GuardianAI - Python Detection Service
Receives a single image frame (raw bytes) and returns whether it
contains unsafe content. Deploy this as its own Railway service.
"""

from fastapi import FastAPI, Request
import cv2
import numpy as np
import mediapipe as mp

app = FastAPI()

mp_pose = mp.solutions.pose
pose = mp_pose.Pose(static_image_mode=True, min_detection_confidence=0.5)

LOWER = np.array([0, 40, 70], dtype=np.uint8)
UPPER = np.array([25, 170, 255], dtype=np.uint8)

SKIN_RATIO_THRESHOLD = 0.30   # tune based on testing
BLOCK_THRESHOLD = 0.70        # confidence above this -> BLOCKED, else FLAGGED

SAFE_RESULT = {
    "detected": False,
    "category": "NONE",
    "confidence": 0.0,
    "action": "ALLOWED",
    "contentType": "IMAGE",
}


@app.get("/health")
def health():
    return {"status": "ok"}


@app.post("/analyze")
async def analyze(request: Request):
    frame_bytes = await request.body()

    # Guard against empty / missing body (e.g. malformed WebSocket frame)
    if not frame_bytes:
        return SAFE_RESULT

    nparr = np.frombuffer(frame_bytes, np.uint8)

    # Guard against an empty array before handing it to OpenCV
    if nparr.size == 0:
        return SAFE_RESULT

    frame = cv2.imdecode(nparr, cv2.IMREAD_COLOR)

    # Guard against bytes that aren't a valid/decodable image
    if frame is None:
        return SAFE_RESULT

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