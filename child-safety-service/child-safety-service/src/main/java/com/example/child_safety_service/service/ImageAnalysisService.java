package com.example.child_safety_service.service;

import com.example.child_safety_service.dto.AnalysisResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class ImageAnalysisService {

    private final RestClient restClient;

    public ImageAnalysisService(RestClient restClient) {
        this.restClient = restClient;
    }

    public AnalysisResponse analyzeImage(String imageBase64) {

        Map<String, String> body = Map.of("image", imageBase64);

        Map response =
                restClient.post()
                        .uri("http://localhost:5005/predict")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body)
                        .retrieve()
                        .body(Map.class);

        List<Map<String, Object>> detections =
                (List<Map<String, Object>>) response.get("detections");

        int totalBoxes =
                Integer.parseInt(response.get("total_boxes").toString());

        return new AnalysisResponse(detections, totalBoxes);
    }
}