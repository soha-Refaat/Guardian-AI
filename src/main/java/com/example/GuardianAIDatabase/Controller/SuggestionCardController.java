package com.example.GuardianAIDatabase.Controller;

import com.example.GuardianAIDatabase.Entity.SuggestionCard;
import com.example.GuardianAIDatabase.Services.SuggestionCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SuggestionCardController {

    private final SuggestionCardService suggestionCardService;

    @GetMapping("/children/{childId}/suggestions")
    public ResponseEntity<List<SuggestionCard>> getByChild(@PathVariable String childId) {
        return ResponseEntity.ok(suggestionCardService.getByChild(childId));
    }

    @GetMapping("/suggestions/{id}")
    public ResponseEntity<SuggestionCard> getById(@PathVariable String id) {
        return ResponseEntity.ok(suggestionCardService.getById(id));
    }

    @PostMapping("/children/{childId}/suggestions")
    public ResponseEntity<SuggestionCard> create(@PathVariable String childId,
                                                 @RequestBody SuggestionCard card) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(suggestionCardService.create(childId, card));
    }

    @DeleteMapping("/suggestions/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        suggestionCardService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
