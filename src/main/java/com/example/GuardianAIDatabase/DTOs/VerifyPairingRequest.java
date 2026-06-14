package com.example.GuardianAIDatabase.DTOs;

import lombok.Data;

@Data
public class VerifyPairingRequest {
    private String code;
    private String deviceName;
    private String androidVersion;
}
