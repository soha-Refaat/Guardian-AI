package com.example.GuardianAIDatabase.Controller;

import com.example.GuardianAIDatabase.Entity.Alert;
import com.example.GuardianAIDatabase.Services.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping("/parents/{parentId}/alerts")
    public ResponseEntity<List<Alert>> getByParent(@PathVariable String parentId) {
        return ResponseEntity.ok(alertService.getByParent(parentId));
    }

    @GetMapping("/alerts/{id}")
    public ResponseEntity<Alert> getById(@PathVariable String id) {
        return ResponseEntity.ok(alertService.getById(id));
    }

    @PostMapping("/parents/{parentId}/alerts/{detectionId}")
    public ResponseEntity<Alert> create(@PathVariable String parentId,
                                        @PathVariable String detectionId,
                                        @RequestBody Alert alert) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(alertService.create(parentId, detectionId, alert));
    }

    @DeleteMapping("/alerts/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        alertService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
