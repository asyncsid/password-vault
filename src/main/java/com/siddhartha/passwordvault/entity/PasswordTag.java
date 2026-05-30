package com.siddhartha.passwordvault.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "password_tags",
        uniqueConstraints = @UniqueConstraint(columnNames = {"password_entry_id", "tag"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordTag {
    @Id
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    @JdbcTypeCode(SqlTypes.VARCHAR)
//    @GeneratedValue(strategy = GenerationType.UUID)
//    @UuidGenerator
    private UUID id;

    @PrePersist
    protected void onCreate() {
        if (id == null) id = UUID.randomUUID(); // "550e8400-e29b-41d4-a716-446655440000"
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "password_entry_id", nullable = false)
    private PasswordEntry passwordEntry;

    @Column(nullable = false, length = 50)
    private String tag;

    @CreationTimestamp
    private LocalDateTime createdAt;
}