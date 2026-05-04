package com.example.GuardianAIDatabase.Entity;

import com.example.GuardianAIDatabase.enums.ActionTaken;
import com.example.GuardianAIDatabase.enums.Category;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiDetection {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID detectionId;

    @OneToOne
    @JoinColumn(name = "log_id")
    @JsonIgnore
    private ContentLog contentLog;

    @Enumerated(EnumType.STRING)
    private Category category;

    private Float confidenceScore;

    @Enumerated(EnumType.STRING)
    private ActionTaken actionTaken;
}
