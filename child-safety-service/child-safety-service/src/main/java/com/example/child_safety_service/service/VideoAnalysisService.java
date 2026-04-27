package com.example.child_safety_service.service;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Service
public class VideoAnalysisService {

    private final RestClient restClient;

    public VideoAnalysisService(RestClient restClient) {
        this.restClient = restClient;
    }

    public Object analyzeVideo(MultipartFile file) throws Exception {

        File tempFile = File.createTempFile("video", ".mp4");
        file.transferTo(tempFile);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(tempFile));

        Object response = restClient.post()
                .uri("http://localhost:5000/predict-video")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(Object.class);

        tempFile.delete();

        return response;
    }
}