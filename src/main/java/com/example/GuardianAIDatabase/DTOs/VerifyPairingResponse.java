package com.example.GuardianAIDatabase.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerifyPairingResponse {
    private String childId;
    private String childName;
    private String deviceId;
    private String authToken;
    private String parentId;
    private String message;
}
