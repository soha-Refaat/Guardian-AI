package com.example.GuardianAIDatabase.Services;


import com.example.GuardianAIDatabase.Entity.Child;
import com.example.GuardianAIDatabase.Entity.SuggestionCard;
import com.example.GuardianAIDatabase.Repository.ChildRepository;
import com.example.GuardianAIDatabase.Repository.SuggestionCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SuggestionCardService {

    private final SuggestionCardRepository suggestionCardRepository;
    private final ChildRepository childRepository;

    public List<SuggestionCard> getByChild(UUID childId) {
        return suggestionCardRepository.findByChildChildId(childId);
    }

    public SuggestionCard getById(UUID id) {
        return suggestionCardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SuggestionCard not found"));
    }

    public SuggestionCard create(UUID childId, SuggestionCard card) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Child not found"));
        card.setChild(child);
        card.setCreatedAt(LocalDateTime.now());
        return suggestionCardRepository.save(card);
    }

    public void delete(UUID id) {
        suggestionCardRepository.deleteById(id);
    }
}
