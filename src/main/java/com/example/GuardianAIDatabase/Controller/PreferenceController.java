package com.example.GuardianAIDatabase.Controller;

import com.example.GuardianAIDatabase.Entity.Preference;
import com.example.GuardianAIDatabase.Services.PreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PreferenceController {

    private final PreferenceService preferenceService;

    @GetMapping("/children/{childId}/preference")
    public ResponseEntity<Preference> getByChild(@PathVariable UUID childId) {
        return ResponseEntity.ok(preferenceService.getByChild(childId));
    }

    @PutMapping("/children/{childId}/preference")
    public ResponseEntity<Preference> createOrUpdate(@PathVariable UUID childId,
                                                     @RequestBody Preference preference) {
        return ResponseEntity.ok(preferenceService.createOrUpdate(childId, preference));
    }
}
