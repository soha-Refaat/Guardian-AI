package com.example.GuardianAIDatabase.Repository;

import com.example.GuardianAIDatabase.Entity.Preference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface PreferenceRepository extends JpaRepository<Preference, String> {
    Optional<Preference> findByChildChildId(String childId);
}
