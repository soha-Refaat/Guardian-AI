package com.example.GuardianAIDatabase.Repository;

import com.example.GuardianAIDatabase.Entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface ReportRepository extends JpaRepository<Report, String> {
    List<Report> findByParentParentId(String parentId);
    List<Report> findByChildChildId(String childId);
}
