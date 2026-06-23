package com.example.GuardianAIDatabase.Controller;

import com.example.GuardianAIDatabase.Entity.ChildInterest;
import com.example.GuardianAIDatabase.Entity.DefaultSuggestion;
import com.example.GuardianAIDatabase.Services.SuggestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SuggestionController {

    private final SuggestionService suggestionService;

    // الاقتراحات الجاهزة — الـ Parent يشوفها ويختار منها
    @GetMapping("/api/suggestions/defaults")
    public ResponseEntity<List<DefaultSuggestion>> getDefaults() {
        return ResponseEntity.ok(suggestionService.getAllDefaults());
    }

    // interests الـ Child
    @GetMapping("/api/children/{childId}/interests")
    public ResponseEntity<List<ChildInterest>> getInterests(@PathVariable String childId) {
        return ResponseEntity.ok(suggestionService.getChildInterests(childId));
    }

    // إضافة interest
    @PostMapping("/api/children/{childId}/interests")
    public ResponseEntity<ChildInterest> addInterest(@PathVariable String childId,
                                                     @RequestBody ChildInterest interest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(suggestionService.addInterest(childId, interest));
    }

    // حذف interest
    @DeleteMapping("/api/interests/{interestId}")
    public ResponseEntity<Void> removeInterest(@PathVariable String interestId) {
        suggestionService.removeInterest(interestId);
        return ResponseEntity.noContent().build();
    }
}