"""
Test client for Violence Detection WebSocket on Railway.

Usage:
    pip install websocket-client
    python test_violence_ws.py image.jpg
"""

import sys
import websocket

WS_URL = "wss://violence-detection-production-9a8f.up.railway.app/ws/violence?deviceId=123&token=abc"

def main():
    if len(sys.argv) < 2:
        print("Usage: python test_violence_ws.py <image-path>")
        sys.exit(1)

    image_path = sys.argv[1]

    with open(image_path, "rb") as f:
        frame_bytes = f.read()

    print(f"Loaded image: {len(frame_bytes)} bytes")
    print(f"Connecting to: {WS_URL}")

    ws = websocket.create_connection(
        WS_URL,
        sslopt={"cert_reqs": 0}
    )

    print("Connected!")

    ws.send_binary(frame_bytes)
    print("Image sent. Waiting for response...")

    response = ws.recv()

    print("\n===== RESPONSE =====")
    print(response)

    ws.close()
    print("====================")

if __name__ == "__main__":
    main()