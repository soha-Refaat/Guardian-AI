package com.example.GuardianAIDatabase.Services;

import com.example.GuardianAIDatabase.Entity.AiDetection;
import com.example.GuardianAIDatabase.Entity.ContentLog;
import com.example.GuardianAIDatabase.Repository.AiDetectionRepository;
import com.example.GuardianAIDatabase.Repository.ContentLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AiDetectionService {
    private final AiDetectionRepository aiDetectionRepository;
    private final ContentLogRepository contentLogRepository;
    public AiDetection getByLog(UUID logId){
        return aiDetectionRepository.findByContentLogContentId(logId)
                .orElseThrow(() -> new RuntimeException("Detection not found"));
    }
    public AiDetection getById(UUID id) {
        return aiDetectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Detection not found"));
    }
    public AiDetection create(UUID logId, AiDetection detection) {
        ContentLog log = contentLogRepository.findById(logId)
                .orElseThrow(() -> new RuntimeException("ContentLog not found"));
        detection.setContentLog(log);
        return aiDetectionRepository.save(detection);
    }
}
