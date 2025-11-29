package com.siddhartha.passwordvault.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

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
    @GeneratedValue
    @UuidGenerator
    private UUID id;

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

    @OneToMany(mappedBy = "passwordEntry", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PasswordTag> tags;

    @OneToMany(mappedBy = "passwordEntry", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EntryNote> notes;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}