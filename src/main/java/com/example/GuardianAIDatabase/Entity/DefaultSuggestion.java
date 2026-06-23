package com.example.GuardianAIDatabase.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DefaultSuggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String suggestionId;

    private String category;      // "Gaming", "Sports", "Music"
    private String description;   // "Games & Video Games"
    private String iconName;      // اسم الـ icon للـ Flutter app
}
