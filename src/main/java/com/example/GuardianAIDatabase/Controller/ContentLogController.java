package com.example.GuardianAIDatabase.Controller;

import com.example.GuardianAIDatabase.Entity.ContentLog;
import com.example.GuardianAIDatabase.Services.ContentLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ContentLogController {

    private final ContentLogService contentLogService;

    @GetMapping("/devices/{deviceId}/logs")
    public ResponseEntity<List<ContentLog>> getByDevice(@PathVariable String deviceId) {
        return ResponseEntity.ok(contentLogService.getByDevice(deviceId));
    }

    @GetMapping("/logs/{id}")
    public ResponseEntity<ContentLog> getById(@PathVariable String id) {
        return ResponseEntity.ok(contentLogService.getById(id));
    }

    @PostMapping("/devices/{deviceId}/logs")
    public ResponseEntity<ContentLog> create(@PathVariable String deviceId,
                                             @RequestBody ContentLog log) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contentLogService.create(deviceId, log));
    }

    @DeleteMapping("/logs/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        contentLogService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
