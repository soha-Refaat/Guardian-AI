package com.example.GuardianAIDatabase.Services;

import com.example.GuardianAIDatabase.DTOs.IncidentResponse;
import com.example.GuardianAIDatabase.Entity.AiDetection;
import com.example.GuardianAIDatabase.Entity.Child;
import com.example.GuardianAIDatabase.Entity.ContentLog;
import com.example.GuardianAIDatabase.Entity.Device;
import com.example.GuardianAIDatabase.Repository.AiDetectionRepository;
import com.example.GuardianAIDatabase.enums.ActionTaken;
import com.example.GuardianAIDatabase.enums.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.GuardianAIDatabase.enums.FilterAction.BLUR;

@Service
@RequiredArgsConstructor
public class IncidentService {

    private final AiDetectionRepository aiDetectionRepository;

    // كل incidents الـ Parent
    public List<IncidentResponse> getByParent(String parentId) {
        return aiDetectionRepository.findAllByParentId(parentId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // incidents طفل معين بس
    public List<IncidentResponse> getByChild(String childId) {
        return aiDetectionRepository.findAllByChildId(childId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private IncidentResponse toResponse(AiDetection detection) {
        ContentLog log = detection.getContentLog();
        Device device = log.getDevice();
        Child child = device.getChild();

        return new IncidentResponse(
                detection.getDetectionId(),
                log.getContentId(),
                child.getChildId(),
                child.getName(),
                device.getDeviceId(),
                log.getSourceApp(),
                log.getContentType().name(),
                log.getTimestream(),
                formatCategory(detection.getCategory()),
                getSeverity(detection.getConfidenceScore()),
                detection.getConfidenceScore(),
                formatAction(detection.getActionTaken())
        );
    }

    private String formatCategory(Category category) {
        if (category == null) return "Unknown";
        return category.name().charAt(0)
                + category.name().substring(1).toLowerCase()
                .replace("_", " ");
    }

    private String getSeverity(Float score) {
        if (score == null) return "LOW";
        if (score >= 0.75) return "HIGH";
        if (score >= 0.50) return "MEDIUM";
        return "LOW";
    }

    private String formatAction(ActionTaken action) {
        if (action == null) return "Unknown";
        return switch (action) {
            case BLOCKED -> "Blocked";
            case FLAGGED -> "Flagged";
            case ALLOWED -> "Allowed";
            case BLUR    -> "Screen Blurred";
        };
    }
}
