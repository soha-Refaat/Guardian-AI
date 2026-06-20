package com.example.Nudity_Detection_Service.Component;

import com.example.Nudity_Detection_Service.dto.DetectionResult;
import com.example.Nudity_Detection_Service.dto.DeviceSession;
import com.example.Nudity_Detection_Service.service.DatabaseClientService;
import com.example.Nudity_Detection_Service.service.DetectionClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class FrameDetectionHandler extends BinaryWebSocketHandler {

    private final DetectionClientService detectionClientService;
    private final DatabaseClientService databaseClientService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // sessionId -> device/auth context for that open connection
    private final Map<String, DeviceSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String deviceId = extractParam(session, "deviceId");
        String token = extractParam(session, "token");

        if (deviceId == null || token == null) {
            log.warn("WebSocket rejected: missing deviceId or token");
            closeSilently(session, CloseStatus.POLICY_VIOLATION);
            return;
        }

        sessions.put(session.getId(), new DeviceSession(deviceId, token));
        log.info("WebSocket connected: device={}, session={}", deviceId, session.getId());
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        DeviceSession deviceSession = sessions.get(session.getId());
        if (deviceSession == null) {
            return;
        }

        byte[] frameBytes = message.getPayload().array();

        detectionClientService.analyzeFrame(frameBytes)
                .subscribe(
                        result -> handleDetectionResult(session, deviceSession, result),
                        error -> log.error("Detection pipeline error: {}", error.getMessage())
                );
    }

    private void handleDetectionResult(WebSocketSession session, DeviceSession deviceSession,
                                       DetectionResult result) {
        // 1. Reply to the Android app immediately - this is the whole point of WebSocket
        sendResult(session, result);

        // 2. If something unsafe was found, persist it asynchronously.
        //    This does NOT block the next frame's reply.
        if (result.isDetected()) {
            persistDetection(deviceSession, result);
        }
    }

    private void sendResult(WebSocketSession session, DetectionResult result) {
        try {
            if (!session.isOpen()) return;
            String json = objectMapper.writeValueAsString(result);
            session.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            log.error("Failed to send WebSocket reply: {}", e.getMessage());
        }
    }

    private void persistDetection(DeviceSession deviceSession, DetectionResult result) {
        String deviceId = deviceSession.getDeviceId();
        String token = deviceSession.getAuthToken();

        databaseClientService.createContentLog(deviceId, token, result.getContentType())
                .flatMap(logId -> {
                    if (logId == null) return reactor.core.publisher.Mono.<String>empty();
                    return databaseClientService.createDetection(logId, token, result);
                })
                .flatMap(detectionId -> {
                    if (detectionId == null) return reactor.core.publisher.Mono.<Void>empty();
                    return databaseClientService.createAlert(deviceId, detectionId, token, result);
                })
                .subscribe(
                        v -> log.info("Detection + alert pipeline completed for device {}", deviceId),
                        error -> log.error("Failed to persist detection/alert: {}", error.getMessage())
                );
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        DeviceSession removed = sessions.remove(session.getId());
        if (removed != null) {
            log.info("WebSocket closed: device={}, status={}", removed.getDeviceId(), status);
        }
    }

    private String extractParam(WebSocketSession session, String paramName) {
        String query = session.getUri() != null ? session.getUri().getQuery() : null;
        if (query == null) return null;

        for (String pair : query.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2 && kv[0].equals(paramName)) {
                return kv[1];
            }
        }
        return null;
    }

    private void closeSilently(WebSocketSession session, CloseStatus status) {
        try {
            session.close(status);
        } catch (IOException ignored) {
        }
    }
}
