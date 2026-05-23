package com.example.GuardianAIDatabase.Services;

import com.example.GuardianAIDatabase.Entity.Child;
import com.example.GuardianAIDatabase.Entity.Parent;
import com.example.GuardianAIDatabase.Entity.Report;
import com.example.GuardianAIDatabase.Repository.ChildRepository;
import com.example.GuardianAIDatabase.Repository.ParentRepository;
import com.example.GuardianAIDatabase.Repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ParentRepository parentRepository;
    private final ChildRepository childRepository;

    public List<Report> getByParent(String parentId) {
        return reportRepository.findByParentParentId(parentId);
    }

    public List<Report> getByChild(String childId) {
        return reportRepository.findByChildChildId(childId);
    }

    public Report getById(String id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));
    }

    public Report create(String parentId, String childId, Report report) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent not found"));
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Child not found"));
        report.setParent(parent);
        report.setChild(child);
        report.setGeneratedAt(LocalDateTime.now());
        return reportRepository.save(report);
    }
}
