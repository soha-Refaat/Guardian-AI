package com.example.GuardianAIDatabase.Repository;

import com.example.GuardianAIDatabase.Entity.ContentFilter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContentFilterRepository extends JpaRepository<ContentFilter, String> {
    Optional<ContentFilter> findByChildChildId(String childId);
}
