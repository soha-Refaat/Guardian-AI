package com.example.GuardianAIDatabase.Repository;

import com.example.GuardianAIDatabase.Entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface AlertRepository extends JpaRepository<Alert, String> {
    List<Alert> findByParentParentId(String parentId);
}
