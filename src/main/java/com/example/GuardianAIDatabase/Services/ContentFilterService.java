package com.example.GuardianAIDatabase.Services;

import com.example.GuardianAIDatabase.Entity.Child;
import com.example.GuardianAIDatabase.Entity.ContentFilter;
import com.example.GuardianAIDatabase.Repository.ChildRepository;
import com.example.GuardianAIDatabase.Repository.ContentFilterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ContentFilterService {

    private final ContentFilterRepository contentFilterRepository;
    private final ChildRepository childRepository;

    public ContentFilter getByChild(String childId) {
        return contentFilterRepository.findByChildChildId(childId)
                .orElseThrow(() -> new RuntimeException("Content filter not found"));
    }

    public ContentFilter createOrUpdate(String childId, ContentFilter incoming) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Child not found"));

        ContentFilter existing = contentFilterRepository
                .findByChildChildId(childId)
                .orElse(new ContentFilter());

        existing.setChild(child);

        existing.setViolence(incoming.getViolence());

        existing.setNudity(incoming.getNudity());

        existing.setOffensiveWords(incoming.getOffensiveWords());

        existing.setUpdatedAt(LocalDateTime.now());

        return contentFilterRepository.save(existing);
    }
}
