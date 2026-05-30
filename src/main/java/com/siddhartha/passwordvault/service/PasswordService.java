package com.siddhartha.passwordvault.service;

import com.siddhartha.passwordvault.dto.PasswordEntryRequest;
import com.siddhartha.passwordvault.dto.PasswordEntryResponse;
import com.siddhartha.passwordvault.entity.PasswordEntry;
import com.siddhartha.passwordvault.entity.PasswordHistory;
import com.siddhartha.passwordvault.entity.PasswordTag;
import com.siddhartha.passwordvault.repository.PasswordEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PasswordService {

    private final PasswordEntryRepository passwordEntryRepository;
    /*
    private final PasswordHistoryRepository passwordHistoryRepository;
    private final PasswordTagRepository passwordTagRepository;
    private final EntryNoteRepository entryNoteRepository;
    */

    public PasswordEntryResponse createPasswordEntry(PasswordEntryRequest request) {
        // Check for duplicates
        if (passwordEntryRepository.findByUserIdAndWebsiteUrl(request.getUserId(), request.getWebsiteUrl()).isPresent()) {
            throw new IllegalArgumentException("Password entry already exists for this website");
        }

        PasswordEntry entry = PasswordEntry.builder()
                .userId(request.getUserId())
                .name(request.getName())
                .websiteUrl(request.getWebsiteUrl())
                .email(request.getEmail())
                .password(encryptPassword(request.getPassword())) // Implement encryption
//                .notes(request.getNotes())
                .build();

        // Save tags
        if (request.getTags() != null) {
            request.getTags().forEach(tag -> {
                PasswordTag passwordTag = PasswordTag.builder()
                        .passwordEntry(entry)
                        .tag(tag)
                        .build();
                if (entry.getTags() == null) {
                    entry.setTags(new ArrayList<>());
                } else {
                    entry.getTags().add(passwordTag);
                }

            });
        }

        // Save notes
        /*if (request.getNotes() != null) {
            request.getNotes().forEach(noteRequest -> {
                EntryNote note = EntryNote.builder()
                        .passwordEntry(entry)
                        .title(noteRequest.getTitle())
                        .content(noteRequest.getContent())
                        .isPrivate(noteRequest.getIsPrivate())
                        .build();
                entry.getNotes().add(note);
            });
        }*/

        PasswordEntry saved = passwordEntryRepository.save(entry);
        log.info("Created password entry: {} for user: {}", saved.getId(), request.getUserId());
        return mapToResponse(saved);
    }

    public PasswordEntryResponse updatePasswordEntry(UUID id, PasswordEntryRequest request) {
        PasswordEntry entry = passwordEntryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Password entry not found"));

        // Save old password to history if changed
        if (!entry.getPassword().equals(encryptPassword(request.getPassword()))) {
            PasswordHistory history = PasswordHistory.builder()
                    .passwordEntry(entry)
                    .oldPassword(entry.getPassword())
                    .build();
//            passwordHistoryRepository.save(history);
        }

        // Update main fields
        entry.setName(request.getName());
        entry.setEmail(request.getEmail());
        entry.setPassword(encryptPassword(request.getPassword()));
//        entry.setNotes(request.getNotes());

        // Update tags (delete old, add new)
//        passwordTagRepository.deleteAllByPasswordEntryId(id);
        if (request.getTags() != null) {
            request.getTags().forEach(tag -> {
                PasswordTag passwordTag = PasswordTag.builder()
                        .passwordEntry(entry)
                        .tag(tag)
                        .build();
                entry.getTags().add(passwordTag);
            });
        }

        PasswordEntry updated = passwordEntryRepository.save(entry);
        log.info("Updated password entry: {} for user: {}", id, request.getUserId());
        return mapToResponse(updated);
    }

    @Transactional
    public void deletePasswordEntry(UUID id, String userId) {
        /*if (!passwordEntryRepository.existsByIdAndUserId(id, userId)) {
            throw new IllegalArgumentException("Password entry not found");
        }*/
        passwordEntryRepository.deleteById(id);
        log.info("Deleted password entry: {} for user: {}", id, userId);
    }

    @Transactional
    public void deleteMultipleEntries(String userId, List<UUID> ids) {
        passwordEntryRepository.deleteByIdsAndUserId(ids, userId);
        log.info("Deleted {} password entries for user: {}", ids.size(), userId);
    }

    public List<PasswordEntryResponse> getUserEntries(String userId) {
        return passwordEntryRepository.findByUserIdOrderByUpdatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public PasswordEntryResponse getEntry(UUID id, String userId) {
        return passwordEntryRepository.findById(id)
                .filter(entry -> entry.getUserId().equals(userId))
                .map(this::mapToResponse)
                .orElseThrow(() -> new IllegalArgumentException("Entry not found"));
    }

    public List<PasswordEntryResponse> getEntriesByTag(String userId, String tag) {
        return passwordEntryRepository.findByUserIdAndTag(userId, tag)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /*public List<PasswordEntryResponse> searchEntries(String userId, String query) {
        try {
            return passwordEntryRepository.searchByUserId(userId, query)
                    .stream().map(this::mapToResponse).collect(Collectors.toList());
        } catch (Exception e) {
            // Fallback to LIKE search
            log.warn("FULLTEXT search failed, using LIKE: {}", e.getMessage());
            return passwordEntryRepository.searchByUserIdLike(userId, query)
                    .stream().map(this::mapToResponse).collect(Collectors.toList());
        }
    }*/

    private PasswordEntryResponse mapToResponse(PasswordEntry entry) {
        return PasswordEntryResponse.builder()
                .id(entry.getId())
                .userId(entry.getUserId())
                .name(entry.getName())
                .websiteUrl(entry.getWebsiteUrl())
                .email(entry.getEmail())
//                .notes(entry.getNotes())
                .tags(entry.getTags().stream().map(PasswordTag::getTag).collect(Collectors.toList()))
                .createdAt(entry.getCreatedAt())
                .updatedAt(entry.getUpdatedAt())
                .build();
    }

    private String encryptPassword(String password) {
        // Implement BCrypt/Argon2 encryption
        return password; // Placeholder
    }
}