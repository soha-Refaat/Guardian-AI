package com.toxicbert.toxic_bert_gateway.service;

import com.toxicbert.toxic_bert_gateway.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ToxicBertService {

    private final RestTemplate restTemplate;

    @Value("${flask.service.url:http://localhost:5003}")
    private String flaskUrl;

    // ───────────────────────────────
    // SINGLE ANALYZE
    // ───────────────────────────────
    public AnalyzeResult analyze(String text, double threshold) {

        String url = flaskUrl + "/analyze";

        Map<String, Object> body = new HashMap<>();
        body.put("text", text);
        body.put("threshold", threshold);

        try {
            ResponseEntity<AnalyzeResult> response =
                    restTemplate.postForEntity(url, buildRequest(body), AnalyzeResult.class);

            return response.getBody();

        } catch (Exception e) {
            log.error("Analyze failed", e);
            throw new RuntimeException("Flask analyze failed: " + e.getMessage());
        }
    }

    // ───────────────────────────────
    // BATCH
    // ───────────────────────────────
    public BatchAnalyzeResult analyzeBatch(List<String> texts, double threshold) {

        String url = flaskUrl + "/analyze/batch";

        Map<String, Object> body = new HashMap<>();
        body.put("texts", texts);
        body.put("threshold", threshold);

        try {
            ResponseEntity<BatchAnalyzeResult> response =
                    restTemplate.postForEntity(url, buildRequest(body), BatchAnalyzeResult.class);

            return response.getBody();

        } catch (Exception e) {
            log.error("Batch analyze failed", e);
            throw new RuntimeException("Flask batch failed: " + e.getMessage());
        }
    }

    // ───────────────────────────────
    // VIDEO
    // ───────────────────────────────
    public VideoAnalyzeResult analyzeVideo(MultipartFile file,
                                           String language,
                                           double threshold) {

        String url = flaskUrl + "/analyze/video";

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        try {
            body.add("file", new MultipartInputStreamFileResource(
                    file.getInputStream(),
                    file.getOriginalFilename()
            ));
        } catch (IOException e) {
            throw new RuntimeException("File read error");
        }

        body.add("language", language);
        body.add("threshold", threshold);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        try {
            ResponseEntity<VideoAnalyzeResult> response =
                    restTemplate.postForEntity(
                            url,
                            new HttpEntity<>(body, headers),
                            VideoAnalyzeResult.class
                    );

            return response.getBody();

        } catch (Exception e) {
            log.error("Video analyze failed", e);
            throw new RuntimeException("Flask video failed: " + e.getMessage());
        }
    }

    // ───────────────────────────────
    // HEALTH
    // ───────────────────────────────
    public Map<?, ?> checkFlaskHealth() {
        try {
            return restTemplate.getForObject(flaskUrl + "/health", Map.class);
        } catch (Exception e) {
            return Map.of("status", "unreachable");
        }
    }

    // ───────────────────────────────
    // REQUEST BUILDER
    // ───────────────────────────────
    private HttpEntity<Map<String, Object>> buildRequest(Map<String, Object> body) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new HttpEntity<>(body, headers);
    }
}

// ───────────────────────────────
// FILE WRAPPER
// ───────────────────────────────
class MultipartInputStreamFileResource extends InputStreamResource {

    private final String filename;

    MultipartInputStreamFileResource(InputStream inputStream, String filename) {
        super(inputStream);
        this.filename = filename;
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public long contentLength() {
        return -1;
    }
}