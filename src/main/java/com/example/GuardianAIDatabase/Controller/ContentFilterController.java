package com.example.GuardianAIDatabase.Controller;

import com.example.GuardianAIDatabase.Entity.ContentFilter;
import com.example.GuardianAIDatabase.Services.ContentFilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ContentFilterController {

    private final ContentFilterService contentFilterService;

    @GetMapping("/children/{childId}/content-filters")
    public ResponseEntity<ContentFilter> getByChild(@PathVariable String childId) {
        return ResponseEntity.ok(contentFilterService.getByChild(childId));
    }

    @PutMapping("/children/{childId}/content-filters")
    public ResponseEntity<ContentFilter> createOrUpdate(@PathVariable String childId,
                                                        @RequestBody ContentFilter contentFilter) {
        return ResponseEntity.ok(contentFilterService.createOrUpdate(childId, contentFilter));
    }
}
