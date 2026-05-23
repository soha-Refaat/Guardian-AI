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
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "VARCHAR(36)")
    private UUID alertId;
    @ManyToOne
    @JoinColumn(name="parent_id")
    @JsonIgnore
    private Parent parent;
    @OneToOne
    @JoinColumn(name = "detection_id")
    @JsonIgnore
    private AiDetection aiDetection;
    private String alertType;
    private String Message;
    private LocalDateTime sendAt;
}
