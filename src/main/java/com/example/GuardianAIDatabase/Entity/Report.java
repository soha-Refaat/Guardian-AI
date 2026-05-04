package com.example.GuardianAIDatabase.Entity;

import com.example.GuardianAIDatabase.enums.OverallRiskLevel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID reportId;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    @JsonIgnore
    private Parent parent;

    @ManyToOne
    @JoinColumn(name = "child_id")
    @JsonIgnore
    private Child child;

    private LocalDate periodStart;
    private LocalDate periodEnd;
    private LocalDateTime generatedAt;
    private Integer totalEvents;
    private Integer unsafeEvents;

    @Enumerated(EnumType.STRING)
    private OverallRiskLevel overallRiskLevel;
}
