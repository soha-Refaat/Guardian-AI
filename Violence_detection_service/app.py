from flask import Flask, request, jsonify
from ultralytics import YOLO
import base64
import numpy as np
import cv2
import logging
import traceback
import tempfile
import os
import time

logging.basicConfig(level=logging.INFO)

app = Flask(__name__)

model = YOLO("yolo_small_weights.pt")


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

    img_bytes = base64.b64decode(base64_str, validate=True)

    np_arr = np.frombuffer(img_bytes, np.uint8)

    img = cv2.imdecode(np_arr, cv2.IMREAD_COLOR)

    return img, None


# ================= YOLO PREDICTION =================
def predict_image(frame):

    results = model.predict(frame, verbose=False)

    boxes = results[0].boxes

    detections = []

    if boxes is not None and len(boxes) > 0:

        for i in range(len(boxes)):

            x1, y1, x2, y2 = boxes.xyxy[i].tolist()

            confidence = float(boxes.conf[i])

            class_id = int(boxes.cls[i])

            class_name = model.names[class_id]

            detections.append({
                "class_id": class_id,
                "class_name": class_name,
                "confidence": round(confidence, 2),
                "box": {
                    "x1": round(x1, 2),
                    "y1": round(y1, 2),
                    "x2": round(x2, 2),
                    "y2": round(y2, 2)
                }
            })

    return {
        "count": len(detections),
        "detections": detections
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

    print("===== VIDEO ENDPOINT HIT =====")

    file = request.files.get("file")

    if not file:
        return jsonify({"error": "no file received"}), 400

    temp = tempfile.NamedTemporaryFile(delete=False, suffix=".mp4")

    path = temp.name

    file.save(path)

    print(f"✅ FILE SAVED TO: {path}")

    cap = cv2.VideoCapture(path)

    total_frames = 0

    detected_frames = []

    try:

        while cap.isOpened():

            ret, frame = cap.read()

            if not ret:
                break

            total_frames += 1

            result = predict_image(frame)

            if result["count"] > 0:

                detected_frames.append({
                    "frame_number": total_frames,
                    "detections": result["detections"]
                })

    except Exception as e:

        logging.error(f"Video processing error: {e}")

    finally:

        cap.release()

        cv2.destroyAllWindows()

        time.sleep(0.5)

        try:
            os.remove(path)
            print("🧹 TEMP FILE DELETED")

        except Exception as e:
            logging.warning(f"Could not delete temp file: {e}")

    return jsonify({
        "total_frames": total_frames,
        "detected_frames_count": len(detected_frames),
        "frames": detected_frames
    })


# ================= MAIN =================
if __name__ == "__main__":

    print("🚀 Flask Server Starting...")

    app.run(host="0.0.0.0", port=5000, debug=True)