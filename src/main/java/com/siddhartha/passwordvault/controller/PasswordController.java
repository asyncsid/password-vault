package com.siddhartha.passwordvault.controller;

import com.siddhartha.passwordvault.dto.PasswordEntryRequest;
import com.siddhartha.passwordvault.dto.PasswordEntryResponse;
import com.siddhartha.passwordvault.service.PasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/passwords")
@RequiredArgsConstructor
public class PasswordController {

    private final PasswordService passwordService;

    // CREATE
    @PostMapping
    public ResponseEntity<PasswordEntryResponse> create(
            @Valid @RequestBody PasswordEntryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(passwordService.createPasswordEntry(request));
    }

    // READ SINGLE
    @GetMapping("/{id}")
    public ResponseEntity<PasswordEntryResponse> get(
            @PathVariable UUID id,
            @RequestParam String userId) {
        return ResponseEntity.ok(passwordService.getEntry(id, userId));
    }

    // READ ALL
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PasswordEntryResponse>> getAll(@PathVariable String userId) {
        return ResponseEntity.ok(passwordService.getUserEntries(userId));
    }

    // READ BY TAG
    @GetMapping("/user/{userId}/tags/{tag}")
    public ResponseEntity<List<PasswordEntryResponse>> getByTag(
            @PathVariable String userId, @PathVariable String tag) {
        return ResponseEntity.ok(passwordService.getEntriesByTag(userId, tag));
    }

    // SEARCH
    /*@GetMapping("/user/{userId}/search")
    public ResponseEntity<List<PasswordEntryResponse>> search(
            @PathVariable String userId,
            @RequestParam String q) {
        return ResponseEntity.ok(passwordService.searchEntries(userId, q));
    }*/

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<PasswordEntryResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody PasswordEntryRequest request) {
        return ResponseEntity.ok(passwordService.updatePasswordEntry(id, request));
    }

    // DELETE SINGLE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @RequestParam String userId) {
        passwordService.deletePasswordEntry(id, userId);
        return ResponseEntity.noContent().build();
    }

    // DELETE MULTIPLE
    /*@DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteMultiple(
            @PathVariable String userId,
            @RequestBody BulkDeleteRequest request) {
        passwordService.deleteMultipleEntries(userId, request.getIds());
        return ResponseEntity.noContent().build();
    }*/
}