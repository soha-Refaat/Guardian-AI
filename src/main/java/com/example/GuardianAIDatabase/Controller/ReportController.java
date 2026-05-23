package com.example.GuardianAIDatabase.Controller;

import com.example.GuardianAIDatabase.Entity.Report;
import com.example.GuardianAIDatabase.Services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/parents/{parentId}/reports")
    public ResponseEntity<List<Report>> getByParent(@PathVariable String parentId) {
        return ResponseEntity.ok(reportService.getByParent(parentId));
    }

    @GetMapping("/children/{childId}/reports")
    public ResponseEntity<List<Report>> getByChild(@PathVariable String childId) {
        return ResponseEntity.ok(reportService.getByChild(childId));
    }

    @GetMapping("/reports/{id}")
    public ResponseEntity<Report> getById(@PathVariable String id) {
        return ResponseEntity.ok(reportService.getById(id));
    }

    @PostMapping("/parents/{parentId}/children/{childId}/reports")
    public ResponseEntity<Report> create(@PathVariable String parentId,
                                         @PathVariable String childId,
                                         @RequestBody Report report) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reportService.create(parentId, childId, report));
    }
}
