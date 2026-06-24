"""
Test client for GuardianAI Detection WebSocket on Railway.
Usage:
    pip install websocket-client
    python test_websocket.py path/to/image.jpg
"""

import sys
import websocket

WS_URL = "wss://nudity-detection-production.up.railway.app/ws/detection?deviceId=test&token=test"

def main():
    if len(sys.argv) < 2:
        print("Usage: python test_websocket.py <path-to-image.jpg>")
        sys.exit(1)

    image_path = sys.argv[1]

    with open(image_path, "rb") as f:
        frame_bytes = f.read()

    print(f"Image loaded: {len(frame_bytes)} bytes")
    print(f"Connecting to Railway: {WS_URL}")

    ws = websocket.create_connection(WS_URL, sslopt={"cert_reqs": 0})
    print("Connected!")

    ws.send_binary(frame_bytes)
    print("Frame sent, waiting for reply...")

    response = ws.recv()
    print("Reply from Python model:")
    print(response)

    ws.close()

if __name__ == "__main__":
    main()