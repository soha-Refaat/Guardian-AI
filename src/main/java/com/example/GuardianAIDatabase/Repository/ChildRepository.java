package com.example.GuardianAIDatabase.Repository;

import com.example.GuardianAIDatabase.Entity.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChildRepository extends JpaRepository<Child, UUID> {
    List<Child> findByParentParentId(UUID parentId);
}
