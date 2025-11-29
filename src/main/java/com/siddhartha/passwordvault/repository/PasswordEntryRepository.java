package com.siddhartha.passwordvault.repository;

import com.siddhartha.passwordvault.entity.PasswordEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordEntryRepository extends JpaRepository<PasswordEntry, UUID> {

    List<PasswordEntry> findByUserIdOrderByUpdatedAtDesc(String userId);

    Optional<PasswordEntry> findByUserIdAndWebsiteUrl(String userId, String websiteUrl);

    @Query("SELECT pe FROM PasswordEntry pe " +
            "JOIN pe.tags t WHERE pe.userId = :userId AND t.tag = :tag " +
            "ORDER BY pe.updatedAt DESC")
    List<PasswordEntry> findByUserIdAndTag(@Param("userId") String userId,
                                           @Param("tag") String tag);

    /*@Query(value = "SELECT * FROM password_entries pe " +
            "WHERE pe.user_id = :userId " +
            "AND MATCH(pe.name, pe.notes) AGAINST (:search IN NATURAL LANGUAGE MODE) " +
            "ORDER BY pe.updated_at DESC",
            nativeQuery = true)
    List<PasswordEntry> searchByUserId(@Param("userId") String userId,
                                       @Param("search") String search);*/

    @Query("SELECT pe FROM PasswordEntry pe WHERE pe.userId = :userId " +
            "AND (LOWER(pe.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(pe.websiteUrl) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(pe.notes) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "ORDER BY pe.updatedAt DESC")
    List<PasswordEntry> searchByUserIdLike(@Param("userId") String userId,
                                           @Param("search") String search);

    @Modifying
    @Query("DELETE FROM PasswordEntry pe WHERE pe.id IN :ids AND pe.userId = :userId")
    void deleteByIdsAndUserId(@Param("ids") List<UUID> ids, @Param("userId") String userId);
}