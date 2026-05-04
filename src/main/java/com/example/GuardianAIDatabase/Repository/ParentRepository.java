package com.example.GuardianAIDatabase.Repository;

import com.example.GuardianAIDatabase.Entity.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParentRepository extends JpaRepository<Parent, UUID> {
    Optional<Parent> findByEmail(String email);
}
