package com.example.Nudity_Detection_Service.service;

import com.example.Nudity_Detection_Service.dto.DetectionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatabaseClientService {

    private final WebClient dbServiceWebClient;

    /**
     * POST /api/devices/{deviceId}/logs
     * Returns the created log's id (logId) so we can attach a detection to it.
     */
    public Mono<String> createContentLog(String deviceId, String authToken, String contentType) {
        return dbServiceWebClient.post()
                .uri("/api/devices/{deviceId}/logs", deviceId)
                .header("Authorization", "Bearer " + authToken)
                .bodyValue(Map.of(
                        "contentType", contentType,
                        "sourceApp", "LiveCamera"
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (String) response.get("logId"))
                .doOnError(e -> log.error("Failed to create content log: {}", e.getMessage()))
                .onErrorResume(e -> Mono.empty());
    }

    /**
     * POST /api/logs/{logId}/detection
     */
    public Mono<String> createDetection(String logId, String authToken, DetectionResult result) {
        return dbServiceWebClient.post()
                .uri("/api/logs/{logId}/detection", logId)
                .header("Authorization", "Bearer " + authToken)
                .bodyValue(Map.of(
                        "category", result.getCategory(),
                        "confidenceScore", result.getConfidence(),
                        "actionTaken", result.getAction()
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (String) response.get("detectionId"))
                .doOnError(e -> log.error("Failed to create detection: {}", e.getMessage()))
                .onErrorResume(e -> Mono.empty());
    }

    /**
     * POST /api/devices/{deviceId}/auto-alert/{detectionId}
     *
     * Convenience endpoint on the Database microservice: it looks up the
     * device's child and parent internally, so this service only needs to
     * know the deviceId - it never needs to resolve parentId itself.
     */
    public Mono<Void> createAlert(String deviceId, String detectionId, String authToken,
                                  DetectionResult result) {
        return dbServiceWebClient.post()
                .uri("/api/devices/{deviceId}/auto-alert/{detectionId}", deviceId, detectionId)
                .header("Authorization", "Bearer " + authToken)
                .bodyValue(Map.of("alertType", result.getCategory() + "_DETECTED"))
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(e -> log.error("Failed to create alert: {}", e.getMessage()))
                .onErrorResume(e -> Mono.empty());
    }
}
