package com.example.GuardianAIDatabase.Services;

import com.example.GuardianAIDatabase.Entity.Child;
import com.example.GuardianAIDatabase.Entity.Preference;
import com.example.GuardianAIDatabase.Repository.ChildRepository;
import com.example.GuardianAIDatabase.Repository.PreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PreferenceService {

    private final PreferenceRepository preferenceRepository;
    private final ChildRepository childRepository;

    public Preference getByChild(UUID childId) {
        return preferenceRepository.findByChildChildId(childId)
                .orElseThrow(() -> new RuntimeException("Preference not found"));
    }

    public Preference createOrUpdate(UUID childId, Preference preference) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Child not found"));

        Preference existing = preferenceRepository
                .findByChildChildId(childId)
                .orElse(new Preference());

        existing.setChild(child);
        existing.setSensitivityLevel(preference.getSensitivityLevel());
        existing.setBlurEnabled(preference.isBlurEnabled());
        existing.setMuteEnabled(preference.isBlurEnabled());
        existing.setLanguageFilterLevel(preference.getLanguageFilterLevel());
        existing.setUpdatedAt(LocalDateTime.now());
        return preferenceRepository.save(existing);
    }
}
