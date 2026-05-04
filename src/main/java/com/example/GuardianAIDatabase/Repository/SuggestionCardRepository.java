package com.example.GuardianAIDatabase.Repository;

import com.example.GuardianAIDatabase.Entity.SuggestionCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface SuggestionCardRepository extends JpaRepository<SuggestionCard, UUID> {
    List<SuggestionCard> findByChildChildId(UUID childId);
}
