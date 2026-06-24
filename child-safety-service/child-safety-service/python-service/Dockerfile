FROM python:3.11-slim

WORKDIR /app

# Install system dependencies needed by OpenCV
RUN apt-get update && apt-get install -y --no-install-recommends \
    libgl1 \
    libglib2.0-0 \
    && rm -rf /var/lib/apt/lists/*

# Install Python dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy app files
COPY violence_app.py .
COPY yolo_small_weights.pt .

EXPOSE 5005

CMD ["gunicorn", "--bind", "0.0.0.0:5005", "--timeout", "120", "violence_app:app"]