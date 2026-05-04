package com.example.GuardianAIDatabase.Controller;

import com.example.GuardianAIDatabase.Entity.Parent;
import com.example.GuardianAIDatabase.Services.ParentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/parents")
@RequiredArgsConstructor
public class ParentController {
    private final ParentService parentService;
    @GetMapping
    public ResponseEntity<List<Parent>>getAll(){
        return ResponseEntity.ok(parentService.getAll());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Parent>getById(@PathVariable UUID id){
        return ResponseEntity.ok(parentService.getById(id));
    }
    @PostMapping
    public ResponseEntity<Parent>create(@RequestBody Parent parent){
        return ResponseEntity.status(HttpStatus.CREATED).body(parentService.create(parent));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Parent>update(@PathVariable UUID id, @RequestBody Parent parent){
        return ResponseEntity.ok(parentService.update(id,parent));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void>delete(@PathVariable UUID id){
        parentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
