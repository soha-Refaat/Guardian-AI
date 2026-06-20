package com.example.Nudity_Detection_Service.service;

import com.example.Nudity_Detection_Service.dto.DetectionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class DetectionClientService {

    private final WebClient pythonServiceWebClient;

    public Mono<DetectionResult> analyzeFrame(byte[] frameBytes) {
        return pythonServiceWebClient.post()
                .uri("/analyze")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .bodyValue(frameBytes)
                .retrieve()
                .bodyToMono(DetectionResult.class)
                .timeout(Duration.ofSeconds(3))
                .onErrorResume(error -> {
                    log.error("Python detection service error: {}", error.getMessage());
                    return Mono.just(DetectionResult.safe());
                });
    }
}
