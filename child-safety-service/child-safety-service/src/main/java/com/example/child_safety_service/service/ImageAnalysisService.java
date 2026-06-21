package com.example.child_safety_service.service;

import com.example.child_safety_service.dto.AnalysisResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class ImageAnalysisService {

    private final RestClient restClient;

    public ImageAnalysisService(RestClient restClient) {
        this.restClient = restClient;
    }

    public AnalysisResponse analyzeImage(String imageBase64) {

        Map<String, String> body = Map.of("image", imageBase64);

        Map response;

        try {
            response = restClient.post()
                    .uri("http://localhost:5000/predict")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .onStatus(
                            status -> status.isError(),
                            (req, res) -> {
                                String errorBody = new String(res.getBody().readAllBytes());                                throw new RuntimeException(errorBody);
                            }
                    )
                    .body(Map.class);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        if (response == null) {
            throw new RuntimeException("Empty response from ML service");
        }

        boolean isSafe = Boolean.parseBoolean(response.get("is_safe").toString());
        String reason = response.get("reason").toString();
        double confidence = Double.parseDouble(response.get("confidence").toString());

        return new AnalysisResponse(isSafe, reason, confidence);
    }
}