package com.example.GuardianAIDatabase.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncidentResponse {
    private String incidentId;
    private String logId;
    private String childId;
    private String childName;
    private String deviceId;
    private String sourceApp;
    private String contentType;
    private LocalDateTime timestamp;
    private String category;
    private String severity;
    private Float confidenceScore;
    private String actionTaken;
}
