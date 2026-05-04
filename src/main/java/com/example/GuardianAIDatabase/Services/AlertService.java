package com.example.GuardianAIDatabase.Services;

import com.example.GuardianAIDatabase.Entity.AiDetection;
import com.example.GuardianAIDatabase.Entity.Alert;
import com.example.GuardianAIDatabase.Entity.Parent;
import com.example.GuardianAIDatabase.Repository.AiDetectionRepository;
import com.example.GuardianAIDatabase.Repository.AlertRepository;
import com.example.GuardianAIDatabase.Repository.ParentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlertService {
    private final AlertRepository alertRepository;
    private final ParentRepository parentRepository;
    private final AiDetectionRepository aiDetectionRepository;

    public List<Alert>getByParent(UUID parentId){
        return alertRepository.findByParentParentId(parentId);
    }
    public Alert getById(UUID id){
        return alertRepository.findById(id).orElseThrow(()->new RuntimeException("Alert not found"));
    }
    public Alert create(UUID parentId, UUID detectId, Alert alert){
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(()->new RuntimeException("Parent not found"));
        AiDetection detection = aiDetectionRepository.findById(detectId)
                .orElseThrow(()-> new RuntimeException("Ai Detection not found"));
        alert.setParent(parent);
        alert.setAiDetection(detection);
        alert.setSendAt(LocalDateTime.now());
        return alertRepository.save(alert);
    }
    public void delete(UUID id) {
        alertRepository.deleteById(id);
    }

}
