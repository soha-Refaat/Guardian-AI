package com.example.GuardianAIDatabase.Controller;

import com.example.GuardianAIDatabase.Entity.AiDetection;
import com.example.GuardianAIDatabase.Services.AiDetectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AiDetectionController {

    private final AiDetectionService aiDetectionService;

    @GetMapping("/logs/{logId}/detection")
    public ResponseEntity<AiDetection> getByLog(@PathVariable UUID logId) {
        return ResponseEntity.ok(aiDetectionService.getByLog(logId));
    }

    @GetMapping("/detections/{id}")
    public ResponseEntity<AiDetection> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(aiDetectionService.getById(id));
    }

    @PostMapping("/logs/{logId}/detection")
    public ResponseEntity<AiDetection> create(@PathVariable UUID logId,
                                              @RequestBody AiDetection detection) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(aiDetectionService.create(logId, detection));
    }
}
