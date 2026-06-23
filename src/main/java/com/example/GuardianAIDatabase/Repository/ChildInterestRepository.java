package com.example.GuardianAIDatabase.Repository;

import com.example.GuardianAIDatabase.Entity.ChildInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChildInterestRepository extends JpaRepository<ChildInterest, String> {
    List<ChildInterest> findByChildChildId(String childId);
}
