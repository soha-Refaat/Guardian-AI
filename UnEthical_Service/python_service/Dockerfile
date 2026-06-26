FROM python:3.11-slim

WORKDIR /app

RUN apt-get update && apt-get install -y --no-install-recommends \
    ffmpeg \
    build-essential \
    libgomp1 \
    && rm -rf /var/lib/apt/lists/*

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY NonEthical_service.py .

EXPOSE 5003

CMD ["gunicorn", "--bind", "0.0.0.0:5003", "--timeout", "300", "--workers", "1", "NonEthical_service:app"]