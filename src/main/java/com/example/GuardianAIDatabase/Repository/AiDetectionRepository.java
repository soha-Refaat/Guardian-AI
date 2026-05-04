package com.example.GuardianAIDatabase.Repository;

import com.example.GuardianAIDatabase.Entity.AiDetection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface AiDetectionRepository extends JpaRepository<AiDetection, UUID> {
    Optional<AiDetection> findByContentLogContentId(UUID contentId);
}
