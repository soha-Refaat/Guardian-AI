package com.example.Nudity_Detection_Service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceSession {
    private String deviceId;
    private String authToken;
}
