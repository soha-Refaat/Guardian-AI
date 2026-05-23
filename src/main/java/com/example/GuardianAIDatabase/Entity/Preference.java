package com.example.GuardianAIDatabase.Entity;

import com.example.GuardianAIDatabase.enums.LanguageFilterLevel;
import com.example.GuardianAIDatabase.enums.SensitivityLevel;
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
public class Preference {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "VARCHAR(36)")
    private UUID preferenceId;
    @OneToOne
    @JoinColumn(name="child_id")
    @JsonIgnore
    private Child child;
    @Enumerated(EnumType.STRING)
    private SensitivityLevel sensitivityLevel;
    private boolean blurEnabled;
    private boolean muteEnabled;
    @Enumerated(EnumType.STRING)
    private LanguageFilterLevel languageFilterLevel;
    private LocalDateTime updatedAt;
}
