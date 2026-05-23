package com.example.GuardianAIDatabase.Services;

import com.example.GuardianAIDatabase.Entity.Child;
import com.example.GuardianAIDatabase.Entity.Parent;
import com.example.GuardianAIDatabase.Repository.ChildRepository;
import com.example.GuardianAIDatabase.Repository.ParentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChildService {
    private final ChildRepository childRepository;
    private final ParentRepository parentRepository;
    public List<Child> getByParent(String parentId) {
        return childRepository.findByParentParentId(parentId);
    }
    public Child getById(String id){
        return childRepository.findById(id).orElseThrow(()-> new RuntimeException("Child not found"));
    }
    public Child create(String parentId,Child child){
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent not found"));
        child.setParent(parent);
        child.setCreatedAt(LocalDateTime.now());
        return childRepository.save(child);
    }
    public Child update(String id,Child child){
        Child existing = getById(id);
        existing.setName(child.getName());
        existing.setAge(child.getAge());
        existing.setProfileImage(child.getProfileImage());
        return childRepository.save(existing);
    }
    public void delete(String id){
        childRepository.deleteById(id);
    }

}
