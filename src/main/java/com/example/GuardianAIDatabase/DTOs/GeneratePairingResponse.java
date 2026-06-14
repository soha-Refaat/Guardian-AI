package com.example.GuardianAIDatabase.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class GeneratePairingResponse {
    private String code;
    private LocalDateTime expiresAt;
}
