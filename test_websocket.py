"""
Simple test client for the GuardianAI Detection WebSocket.
Sends a real image file as a binary WebSocket frame and prints the reply.

Usage:
    pip install websocket-client
    python test_websocket.py path/to/image.jpg
"""

import sys
import websocket

def main():
    if len(sys.argv) < 2:
        print("Usage: python test_websocket.py <path-to-image.jpg>")
        sys.exit(1)

    image_path = sys.argv[1]

    with open(image_path, "rb") as f:
        frame_bytes = f.read()

    print(f"Loaded {len(frame_bytes)} bytes from {image_path}")

    ws_url = "ws://localhost:8080/ws/detection?deviceId=test-device-1&token=test-token"
    print(f"Connecting to {ws_url} ...")

    ws = websocket.create_connection(ws_url)
    print("Connected!")

    ws.send_binary(frame_bytes)
    print("Frame sent, waiting for reply...")

    response = ws.recv()
    print("Reply:", response)

    ws.close()

if __name__ == "__main__":
    main()
