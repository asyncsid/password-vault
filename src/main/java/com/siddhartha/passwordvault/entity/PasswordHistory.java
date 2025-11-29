package com.siddhartha.passwordvault.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "password_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordHistory {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "password_entry_id", nullable = false)
    private PasswordEntry passwordEntry;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String oldPassword;

    @CreationTimestamp
    private LocalDateTime changedAt;
}