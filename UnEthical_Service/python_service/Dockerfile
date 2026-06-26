FROM python:3.11-slim

WORKDIR /app

# Install system dependencies (ffmpeg + build tools)
RUN apt-get update && apt-get install -y --no-install-recommends \
    ffmpeg \
    build-essential \
    && rm -rf /var/lib/apt/lists/*

# Install Python dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy app files
COPY unethical_app.py .

EXPOSE 5003

CMD ["gunicorn", "--bind", "0.0.0.0:5003", "--timeout", "300", "--workers", "1", "NonEthical_service:app"]