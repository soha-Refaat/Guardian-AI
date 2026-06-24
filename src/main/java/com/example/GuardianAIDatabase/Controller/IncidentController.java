package com.example.GuardianAIDatabase.Controller;

import com.example.GuardianAIDatabase.DTOs.IncidentResponse;
import com.example.GuardianAIDatabase.Services.IncidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService incidentService;

    // كل incidents الـ Parent (كل أطفاله)
    @GetMapping("/parents/{parentId}/logs")
    public ResponseEntity<List<IncidentResponse>> getByParent(
            @PathVariable String parentId) {
        return ResponseEntity.ok(incidentService.getByParent(parentId));
    }

    // incidents طفل معين بس
    @GetMapping("/children/{childId}/logs")
    public ResponseEntity<List<IncidentResponse>> getByChild(
            @PathVariable String childId) {
        return ResponseEntity.ok(incidentService.getByChild(childId));
    }
}
