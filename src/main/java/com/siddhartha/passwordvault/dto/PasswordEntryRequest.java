package com.siddhartha.passwordvault.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordEntryRequest {
    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Website URL is required")
    private String websiteUrl;

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private List<String> tags;
    private String notes;
//    private List<EntryNoteRequest> notes;
}