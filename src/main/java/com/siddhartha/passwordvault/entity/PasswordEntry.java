package com.siddhartha.passwordvault.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "password_entries",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "website_url"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordEntry {
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

    @NotBlank
    @Column(nullable = false)
    private String userId;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(nullable = false, name = "website_url")
    private String websiteUrl;

    @NotBlank
    @Column(nullable = false)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password; // Encrypted in service layer

    @OneToMany(mappedBy = "passwordEntry", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PasswordHistory> passwordHistory;

    @OneToMany(mappedBy = "passwordEntry", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PasswordTag> tags;

    @OneToMany(mappedBy = "passwordEntry", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EntryNote> notes;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}