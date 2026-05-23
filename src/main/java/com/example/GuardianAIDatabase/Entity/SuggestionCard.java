package com.example.GuardianAIDatabase.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuggestionCard {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String suggestionId;

    @ManyToOne
    @JoinColumn(name = "child_id")
    @JsonIgnore
    private Child child;

    private String triggerCategory;
    private String suggestionText;
    private LocalDateTime createdAt;
}