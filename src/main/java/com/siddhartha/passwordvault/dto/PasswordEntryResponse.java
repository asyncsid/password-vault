package com.siddhartha.passwordvault.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordEntryResponse {
    private UUID id;
    private String userId;
    private String name;
    private String websiteUrl;
    private String email;
    private String notes;
    private List<String> tags;
    //    private List<EntryNoteResponse> notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}