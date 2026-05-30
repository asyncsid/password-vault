-- =====================================================
-- PASSWORD VAULT DATABASE SCHEMA
-- Docker MySQL 8.0 • Production Ready
-- Compatible: /var/lib/mysql volume persistence
-- =====================================================

CREATE DATABASE IF NOT EXISTS `password_vault`
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE `password_vault`;

-- =====================================================
-- 1. USERS TABLE (Optional - Multi-tenant support)
-- =====================================================
CREATE TABLE `users` (
    `id` VARCHAR(36) NOT NULL PRIMARY KEY,
    `username` VARCHAR(100) NOT NULL UNIQUE,
    `email` VARCHAR(255) NOT NULL UNIQUE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_users_email` (`email`),
    INDEX `idx_users_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- =====================================================
-- 2. PASSWORD_ENTRIES (Core Table)
-- =====================================================
CREATE TABLE `password_entries` (
    `id` VARCHAR(36) NOT NULL PRIMARY KEY,
    `user_id` VARCHAR(36) NOT NULL,
    `name` VARCHAR(200) NOT NULL,
    `website_url` VARCHAR(500) NOT NULL,
    `email` VARCHAR(255) NOT NULL,
    `password` VARCHAR(500) NOT NULL,        -- Encrypted in app layer
    `notes` TEXT,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- Foreign Key (Optional)
    `user_ref_id` VARCHAR(36),
    FOREIGN KEY (`user_ref_id`) REFERENCES `users`(`id`) ON DELETE SET NULL,

    -- Composite Unique Constraint
    UNIQUE KEY `uk_user_website` (`user_id`, `website_url`),

    -- Indexes for Performance
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_name` (`name`),
    INDEX `idx_website` (`website_url`(100)),
    INDEX `idx_email` (`email`),
    FULLTEXT KEY `ftx_search` (`name`, `notes`)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- =====================================================
-- 3. PASSWORD_HISTORY (Audit Trail)
-- =====================================================
CREATE TABLE `password_history` (
    `id` VARCHAR(36) NOT NULL PRIMARY KEY,
    `password_entry_id` VARCHAR(36) NOT NULL,
    `old_password` VARCHAR(500) NOT NULL,     -- Encrypted
    `changed_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `changed_by` VARCHAR(36),                 -- user_id

    FOREIGN KEY (`password_entry_id`) REFERENCES `password_entries`(`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,

    INDEX `idx_entry_id` (`password_entry_id`),
    INDEX `idx_changed_at` (`changed_at`)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- =====================================================
-- 4. PASSWORD_TAGS (Many-to-Many Labels)
-- =====================================================
CREATE TABLE `password_tags` (
    `id` VARCHAR(36) NOT NULL PRIMARY KEY,
    `password_entry_id` VARCHAR(36) NOT NULL,
    `tag` VARCHAR(50) NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (`password_entry_id`) REFERENCES `password_entries`(`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,

    UNIQUE KEY `uk_entry_tag` (`password_entry_id`, `tag`),
    INDEX `idx_tag` (`tag`),
    INDEX `idx_entry_tag` (`password_entry_id`, `tag`)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- =====================================================
-- 5. ENTRY_NOTES (Rich Notes Support)
-- =====================================================
CREATE TABLE `entry_notes` (
    `id` VARCHAR(36) NOT NULL PRIMARY KEY,
    `password_entry_id` VARCHAR(36) NOT NULL,
    `title` VARCHAR(200),
    `content` TEXT NOT NULL,
    `is_private` BOOLEAN DEFAULT FALSE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (`password_entry_id`) REFERENCES `password_entries`(`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,

    INDEX `idx_entry_id` (`password_entry_id`),
    FULLTEXT KEY `ftx_notes` (`title`, `content`)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- =====================================================
-- 6. NOTE_TAGS (Notes Categorization)
-- =====================================================
CREATE TABLE `note_tags` (
    `id` VARCHAR(36) NOT NULL PRIMARY KEY,
    `note_id` VARCHAR(36) NOT NULL,
    `tag` VARCHAR(50) NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (`note_id`) REFERENCES `entry_notes`(`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,

    UNIQUE KEY `uk_note_tag` (`note_id`, `tag`),
    INDEX `idx_tag` (`tag`)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- =====================================================
-- 7. SHARING (Future: Share entries with other users)
-- =====================================================
CREATE TABLE `shared_entries` (
    `id` VARCHAR(36) NOT NULL PRIMARY KEY,
    `password_entry_id` VARCHAR(36) NOT NULL,
    `shared_with_user_id` VARCHAR(36) NOT NULL,
    `shared_by` VARCHAR(36) NOT NULL,
    `permission` ENUM('READ', 'EDIT') DEFAULT 'READ',
    `expires_at` TIMESTAMP NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (`password_entry_id`) REFERENCES `password_entries`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`shared_with_user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`shared_by`) REFERENCES `users`(`id`) ON DELETE CASCADE,

    INDEX `idx_shared_with` (`shared_with_user_id`),
    INDEX `idx_expires` (`expires_at`)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- =====================================================
-- 8. AUDIT LOG (Security Compliance)
-- =====================================================
CREATE TABLE `audit_log` (
    `id` VARCHAR(36) NOT NULL PRIMARY KEY,
    `user_id` VARCHAR(36) NOT NULL,
    `action` ENUM('CREATE', 'UPDATE', 'DELETE', 'VIEW', 'SHARE') NOT NULL,
    `resource_type` VARCHAR(50) NOT NULL,
    `resource_id` VARCHAR(36) NOT NULL,
    `ip_address` VARCHAR(45),
    `user_agent` TEXT,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    INDEX `idx_user_action` (`user_id`, `action`),
    INDEX `idx_created_at` (`created_at`),
    INDEX `idx_resource` (`resource_type`, `resource_id`)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- =====================================================
-- 9. Create MySQL User for App
-- =====================================================
CREATE USER IF NOT EXISTS 'vault_app'@'%' IDENTIFIED BY 'VaultSecure2025!';
GRANT SELECT, INSERT, UPDATE, DELETE
    ON password_vault.* TO 'vault_app'@'%';
GRANT USAGE ON password_vault.* TO 'vault_app'@'%';
FLUSH PRIVILEGES;

-- =====================================================
-- 10. Sample Data (For Testing)
-- =====================================================
INSERT INTO `password_entries` (`id`, `user_id`, `name`, `website_url`, `email`, `password`, `notes`) VALUES
('11111111-1111-1111-1111-111111111111', 'siddhartha', 'GitHub', 'github.com', 'siddhartha@example.com', 'encrypted_github_pass', '2FA enabled'),
('22222222-2222-2222-2222-222222222222', 'siddhartha', 'Google', 'accounts.google.com', 'siddhartha@gmail.com', 'encrypted_google_pass', 'Recovery email: backup@example.com'),
('33333333-3333-3333-3333-333333333333', 'siddhartha', 'Bank', 'mybank.com', 'siddhartha@bank.com', 'encrypted_bank_pass', 'High security - change every 90 days');

INSERT INTO `password_tags` (`id`, `password_entry_id`, `tag`) VALUES
('tag1', '11111111-1111-1111-1111-111111111111', 'work'),
('tag2', '11111111-1111-1111-1111-111111111111', 'social'),
('tag3', '33333333-3333-3333-3333-333333333333', 'finance');

-- =====================================================
-- SUCCESS: Database Ready for Docker!
-- =====================================================
SELECT 'Password Vault Database Initialized Successfully!' as status;