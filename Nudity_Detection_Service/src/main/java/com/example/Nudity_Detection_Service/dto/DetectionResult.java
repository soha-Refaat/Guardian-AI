package com.example.Nudity_Detection_Service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetectionResult {

    private boolean detected;
    private String category;      // VIOLENCE, ADULT, BULLYING, HATE_SPEECH, NONE
    private float confidence;
    private String action;        // BLOCKED, FLAGGED, ALLOWED
    private String contentType;   // IMAGE, VIDEO

    public static DetectionResult safe() {
        return new DetectionResult(false, "NONE", 0f, "ALLOWED", "IMAGE");
    }
}