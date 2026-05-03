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

        File temp = File.createTempFile("video", ".mp4");
        file.transferTo(temp);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(temp));

        Object response =
                restClient.post()
                        .uri("http://localhost:5005/predict-video")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .body(body)
                        .retrieve()
                        .body(Object.class);

        temp.delete();

        return response;
    }
}