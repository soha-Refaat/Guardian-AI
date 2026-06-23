package com.example.GuardianAIDatabase.Services;

import com.example.GuardianAIDatabase.Entity.Child;
import com.example.GuardianAIDatabase.Entity.ChildInterest;
import com.example.GuardianAIDatabase.Entity.DefaultSuggestion;
import com.example.GuardianAIDatabase.Repository.ChildInterestRepository;
import com.example.GuardianAIDatabase.Repository.ChildRepository;
import com.example.GuardianAIDatabase.Repository.DefaultSuggestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SuggestionService {

    private final DefaultSuggestionRepository defaultSuggestionRepository;
    private final ChildInterestRepository childInterestRepository;
    private final ChildRepository childRepository;


    public List<DefaultSuggestion> getAllDefaults() {
        return defaultSuggestionRepository.findAll();
    }

    // جلب interests الـ Child
    public List<ChildInterest> getChildInterests(String childId) {
        return childInterestRepository.findByChildChildId(childId);
    }

    // إضافة interest للـ Child
    public ChildInterest addInterest(String childId, ChildInterest incoming) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Child not found"));

        ChildInterest interest = new ChildInterest();
        interest.setChild(child);
        interest.setCategory(incoming.getCategory());
        interest.setIsCustom(incoming.getIsCustom() != null && incoming.getIsCustom());
        interest.setAddedAt(LocalDateTime.now());

        return childInterestRepository.save(interest);
    }

    // حذف interest
    public void removeInterest(String interestId) {
        childInterestRepository.deleteById(interestId);
    }
}