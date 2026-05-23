package com.example.GuardianAIDatabase.Entity;

import com.example.GuardianAIDatabase.enums.ContentType;
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
public class ContentLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "VARCHAR(36)")
    private UUID contentId;
    @ManyToOne
    @JoinColumn(name="device_id")
    @JsonIgnore
    private Device device;
    private LocalDateTime timestream;
    @Enumerated(EnumType.STRING)
    private ContentType contentType;
    private String sourceApp;

}
