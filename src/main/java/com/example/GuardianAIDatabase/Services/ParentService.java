package com.example.GuardianAIDatabase.Services;

import com.example.GuardianAIDatabase.Entity.Parent;
import com.example.GuardianAIDatabase.Repository.ParentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParentService {
    private final ParentRepository parentRepository;
    public List<Parent>getAll(){
        return parentRepository.findAll();
    }
    public Parent getById(UUID id){
        return parentRepository.findById(id).orElseThrow(() -> new RuntimeException("Parent not found"));
    }
    public Parent create(Parent parent){
        parent.setCreatedAt(LocalDateTime.now());
        return parentRepository.save(parent);
    }
    public Parent update(UUID id,Parent parent){
        Parent existing = getById(id);
        existing.setName(parent.getName());
        existing.setEmail(parent.getEmail());
        existing.setPhoneNumber(parent.getPhoneNumber());
        return parentRepository.save(existing);
    }
    public void delete(UUID id){
        parentRepository.deleteById(id);
    }

}
