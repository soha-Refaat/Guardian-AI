package com.example.GuardianAIDatabase.Entity;

import com.example.GuardianAIDatabase.enums.FilterAction;
import com.example.GuardianAIDatabase.enums.FilterLevel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentFilter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String filterId;

    @OneToOne
    @JoinColumn(name = "child_id")
    @JsonIgnore
    private Child child;

    // ---- Violence ----
    @Enumerated(EnumType.STRING)
    private FilterLevel violenceLevel;          // LOW, MEDIUM, HIGH

    @Enumerated(EnumType.STRING)
    private FilterAction violenceAction;        // FLAG_ONLY, BLUR, BLOCK

    // ---- Nudity ----
    @Enumerated(EnumType.STRING)
    private FilterLevel nudityLevel;

    @Enumerated(EnumType.STRING)
    private FilterAction nudityAction;

    // ---- Offensive Words ----
    @Enumerated(EnumType.STRING)
    private FilterLevel offensiveWordsLevel;

    @Enumerated(EnumType.STRING)
    private FilterAction offensiveWordsAction;

    private LocalDateTime updatedAt;
}
