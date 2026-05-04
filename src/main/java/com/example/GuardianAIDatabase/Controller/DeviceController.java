package com.example.GuardianAIDatabase.Controller;

import com.example.GuardianAIDatabase.Entity.Device;
import com.example.GuardianAIDatabase.Services.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping("/children/{childId}/devices")
    public ResponseEntity<List<Device>> getByChild(@PathVariable UUID childId) {
        return ResponseEntity.ok(deviceService.getByChild(childId));
    }

    @GetMapping("/devices/{id}")
    public ResponseEntity<Device> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(deviceService.getById(id));
    }

    @PostMapping("/children/{childId}/devices")
    public ResponseEntity<Device> create(@PathVariable UUID childId, @RequestBody Device device) {
        return ResponseEntity.status(HttpStatus.CREATED).body(deviceService.create(childId, device));
    }

    @PutMapping("/devices/{id}")
    public ResponseEntity<Device> update(@PathVariable UUID id, @RequestBody Device device) {
        return ResponseEntity.ok(deviceService.update(id, device));
    }

    @DeleteMapping("/devices/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deviceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
