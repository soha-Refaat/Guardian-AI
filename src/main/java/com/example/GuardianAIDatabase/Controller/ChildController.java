package com.example.GuardianAIDatabase.Controller;

import com.example.GuardianAIDatabase.Entity.Child;
import com.example.GuardianAIDatabase.Services.ChildService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChildController {
    private final ChildService childService;

    @GetMapping("/parents/{parentId}/children")
    public ResponseEntity<List<Child>>getByParent(@PathVariable String parentId){
        return ResponseEntity.ok(childService.getByParent(parentId));
    }
    @GetMapping("/children/{id}")
    public ResponseEntity<Child>getById(@PathVariable String id){
        return ResponseEntity.ok(childService.getById(id));
    }
    @PostMapping("/parents/{parentId}/children")
    public ResponseEntity<Child>create(@PathVariable String parentId, @RequestBody Child child){
        return ResponseEntity.status(HttpStatus.CREATED).body(childService.create(parentId,child));
    }
    @PutMapping("/children/{id}")
    public ResponseEntity<Child> update(@PathVariable String id, @RequestBody Child child) {
        return ResponseEntity.ok(childService.update(id, child));
    }
    @DeleteMapping("/children/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        childService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
