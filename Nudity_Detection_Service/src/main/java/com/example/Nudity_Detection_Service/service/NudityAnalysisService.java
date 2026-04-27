package com.example.Nudity_Detection_Service.service;


import com.example.Nudity_Detection_Service.MultipartInputStreamFileResource;
import com.example.Nudity_Detection_Service.dto.AnalysisResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class NudityAnalysisService {

    private final RestTemplate restTemplate = new RestTemplate();

    private final String IMAGE_URL = "http://localhost:6000/predict-nudity";
    private final String VIDEO_URL = "http://localhost:6000/predict-nudity-video";

    // ================= IMAGE =================
    public AnalysisResponse analyzeImage(String base64) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = "{\"image\":\"" + base64 + "\"}";

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<AnalysisResponse> response =
                restTemplate.postForEntity(IMAGE_URL, request, AnalysisResponse.class);

        return response.getBody();
    }

    // ================= VIDEO =================
    public Object analyzeVideo(MultipartFile file) throws Exception {

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        body.add("file", new MultipartInputStreamFileResource(
                file.getInputStream(),
                file.getOriginalFilename()
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> request =
                new HttpEntity<>(body, headers);

        return restTemplate.postForObject(VIDEO_URL, request, Object.class);
    }
}
