package com.toxicbert.toxic_bert_gateway.controller;

import com.toxicbert.toxic_bert_gateway.model.*;
import com.toxicbert.toxic_bert_gateway.service.ToxicBertService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/toxicity")
@RequiredArgsConstructor
public class ToxicBertController {

    private final ToxicBertService service;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<?, ?> flaskHealth = service.checkFlaskHealth();

        return ResponseEntity.ok(Map.of(
                "spring_boot", "ok",
                "flask_service", flaskHealth
        ));
    }

    @PostMapping("/analyze")
    public ResponseEntity<AnalyzeResult> analyze(@Valid @RequestBody AnalyzeRequest request) {

        String preview = request.getText() != null
                ? request.getText().substring(0, Math.min(50, request.getText().length()))
                : "null";

        log.info("Received /analyze request: {}", preview);

        AnalyzeResult result = service.analyze(
                request.getText(),
                request.getThreshold() != null ? request.getThreshold() : 0.5
        );

        return ResponseEntity.ok(result);
    }

    @PostMapping("/analyze/batch")
    public ResponseEntity<BatchAnalyzeResult> analyzeBatch(@Valid @RequestBody BatchAnalyzeRequest request) {

        log.info("Received /analyze/batch with {} texts", request.getTexts().size());

        BatchAnalyzeResult result = service.analyzeBatch(
                request.getTexts(),
                request.getThreshold() != null ? request.getThreshold() : 0.5
        );

        return ResponseEntity.ok(result);
    }

    @PostMapping("/analyze/video")
    public ResponseEntity<VideoAnalyzeResult> analyzeVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "language", defaultValue = "ar") String language,
            @RequestParam(value = "threshold", defaultValue = "0.5") double threshold
    ) {

        log.info("Video request: file={}, lang={}",
                file.getOriginalFilename(), language);

        VideoAnalyzeResult result =
                service.analyzeVideo(file, language, threshold);

        if (result == null) {
            throw new RuntimeException("Empty response from Flask");
        }

        return ResponseEntity.ok(result);
    }
}