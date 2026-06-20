package com.example.GuardianAIDatabase.Entity;

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
public class MonitoredApp {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String appId;

    @ManyToOne
    @JoinColumn(name = "child_id")
    @JsonIgnore
    private Child child;

    private String appName;       // "YouTube", "TikTok", "Chrome", "Instagram"
    private String packageName;   // optional: "com.google.android.youtube" (Android package id)
    private Boolean isActive;     // allows toggling monitoring on/off without deleting

    private LocalDateTime addedAt;
}
