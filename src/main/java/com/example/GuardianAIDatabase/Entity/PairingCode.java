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
public class PairingCode {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String pairingId;
    @Column(unique = true, length = 6)
    private String code;
    @ManyToOne
    @JoinColumn(name = "child_id")
    @JsonIgnore
    private Child child;
    private LocalDateTime expiresAt;
    private boolean used;
}
