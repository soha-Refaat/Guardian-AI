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


def predict_image(frame):
    results = model.predict(frame, verbose=False)
    boxes = results[0].boxes

    confidence = 0.0
    is_violence = False

    if boxes is not None and len(boxes) > 0 and hasattr(boxes, "conf"):
        confidence = float(boxes.conf.max().item())
        is_violence = confidence > CONF_THRESHOLD

    return {
        "is_safe": not is_violence,
        "reason": "Violence detected" if is_violence else "No violence detected",
        "confidence": round(confidence, 2)
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
    print("FILES:", request.files)
    print("FORM:", request.form)

    file = request.files.get("file")

    if not file:
        print("❌ NO FILE RECEIVED")
        return jsonify({"error": "no file received"}), 400

    # create temp file safely
    temp = tempfile.NamedTemporaryFile(delete=False, suffix=".mp4")
    path = temp.name
    temp.close()

    file.save(path)
    print(f"✅ FILE SAVED TO: {path}")

    cap = cv2.VideoCapture(path)

    total = 0
    unsafe = 0
    max_conf = 0.0

    try:
        while True:
            ret, frame = cap.read()
            if not ret:
                break

            total += 1
            res = predict_image(frame)

            if not res["is_safe"]:
                unsafe += 1

            max_conf = max(max_conf, res["confidence"])

    except Exception as e:
        logging.error(f"Video processing error: {e}")

    finally:
        cap.release()
        cv2.destroyAllWindows()

        # 🔥 cleanup for Windows stability
        gc.collect()
        time.sleep(1)

        for _ in range(3):
            try:
                os.remove(path)
                print("🧹 TEMP FILE DELETED")
                break
            except Exception as e:
                time.sleep(1)

    return jsonify({
        "total_frames": total,
        "unsafe_frames": unsafe,
        "is_safe": unsafe == 0,
        "confidence": round(max_conf, 2)
    })


# ================= RUN =================
if __name__ == "__main__":
    print("🚀 Flask Server Starting...")
    app.run(host="0.0.0.0", port=5000, debug=True)