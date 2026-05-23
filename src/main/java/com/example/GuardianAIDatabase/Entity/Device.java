package com.example.GuardianAIDatabase.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "VARCHAR(36)")
    private UUID deviceId;
    @ManyToOne
    @JoinColumn(name="child_id")
    private Child child;
    private String deviceName;
    private String deviceVersion;
    private boolean isActive;
    private LocalDateTime lastSeen;
}
