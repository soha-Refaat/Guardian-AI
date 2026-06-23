package com.example.GuardianAIDatabase.Repository;

import com.example.GuardianAIDatabase.Entity.ChildInterest;
import com.example.GuardianAIDatabase.Entity.DefaultSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DefaultSuggestionRepository extends JpaRepository<DefaultSuggestion, String> {}


