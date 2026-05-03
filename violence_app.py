from flask import Flask, request, jsonify
from ultralytics import YOLO
import numpy as np
import cv2
import logging
import traceback
import tempfile
import os
import time
import base64
import gc

logging.basicConfig(level=logging.INFO)

app = Flask(__name__)

# ✅ CHANGE PORT MODEL HERE IF NEEDED
model = YOLO("yolo_small_weights.pt")

CONF_THRESHOLD = 0.5


# ================= ERROR HANDLER =================
@app.errorhandler(Exception)
def handle_exception(e):
    logging.error(traceback.format_exc())
    return jsonify({
        "error": "server error",
        "details": str(e)
    }), 500


# ================= IMAGE =================
def decode_image(base64_str):
    if not base64_str:
        return None, "Empty image"

    if base64_str.startswith("data:image"):
        base64_str = base64_str.split(",")[1]

    try:
        img_bytes = base64.b64decode(base64_str, validate=True)
        np_arr = np.frombuffer(img_bytes, np.uint8)
        img = cv2.imdecode(np_arr, cv2.IMREAD_COLOR)
        return img, None
    except Exception as e:
        return None, str(e)


# ================= CORE PREDICTION (UPDATED) =================
def predict_image(frame):
    results = model.predict(frame, verbose=False)
    boxes = results[0].boxes

    detections = []

    if boxes is not None and len(boxes) > 0:

        for i in range(len(boxes)):
            xyxy = boxes.xyxy[i].tolist()
            conf = float(boxes.conf[i].item())
            cls = int(boxes.cls[i].item())

            label = "violence" if conf >= CONF_THRESHOLD else "non-violence"

            detections.append({
                "bbox": [
                    int(xyxy[0]),
                    int(xyxy[1]),
                    int(xyxy[2]),
                    int(xyxy[3])
                ],
                "class_id": cls,
                "confidence": conf,
                "label": label
            })

    return {
        "detections": detections,
        "total_boxes": len(detections)
    }


# ================= IMAGE ENDPOINT =================
@app.route("/predict", methods=["POST"])
def predict():
    data = request.get_json()

    if not data or "image" not in data:
        return jsonify({"error": "missing image"}), 400

    frame, error = decode_image(data["image"])

    if error:
        return jsonify({"error": error}), 400

    result = predict_image(frame)
    return jsonify(result)


# ================= VIDEO ENDPOINT =================
@app.route("/predict-video", methods=["POST"])
def predict_video():

    file = request.files.get("file")

    if not file:
        return jsonify({"error": "no file received"}), 400

    temp = tempfile.NamedTemporaryFile(delete=False, suffix=".mp4")
    path = temp.name
    temp.close()

    file.save(path)

    cap = cv2.VideoCapture(path)

    total_frames = 0
    unsafe_frames = 0

    all_detections = []

    try:
        while True:
            ret, frame = cap.read()
            if not ret:
                break

            total_frames += 1

            result = predict_image(frame)

            if result["total_boxes"] > 0:
                unsafe_frames += 1

            all_detections.append(result)

    finally:
        cap.release()
        cv2.destroyAllWindows()

        gc.collect()
        time.sleep(1)

        try:
            os.remove(path)
        except:
            pass

    return jsonify({
        "total_frames": total_frames,
        "unsafe_frames": unsafe_frames,
        "detections_per_frame": all_detections
    })


# ================= RUN =================
if __name__ == "__main__":
    print("🚀 Flask running on port 5005")
    app.run(host="0.0.0.0", port=5005, debug=True)