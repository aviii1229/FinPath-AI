package com.finpath.modules.ai.repository;

import com.finpath.modules.ai.entity.DocumentUploadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentUploadRepository extends JpaRepository<DocumentUploadEntity, UUID> {

    List<DocumentUploadEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<DocumentUploadEntity> findByUserIdAndDocumentCategoryOrderByCreatedAtDesc(UUID userId, String documentCategory);

    List<DocumentUploadEntity> findByUserIdAndProcessingStatusOrderByCreatedAtDesc(UUID userId, String processingStatus);

    Optional<DocumentUploadEntity> findByIdAndUserId(UUID id, UUID userId);

    long countByUserId(UUID userId);

    long countByUserIdAndDocumentCategory(UUID userId, String documentCategory);

    @Query("SELECT d FROM DocumentUploadEntity d WHERE d.user.id = :userId " +
           "AND (:category IS NULL OR d.documentCategory = :category) " +
           "AND (:status IS NULL OR d.processingStatus = :status) " +
           "AND (:search IS NULL OR LOWER(d.fileName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "     OR LOWER(d.extractedSummary) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY d.createdAt DESC")
    List<DocumentUploadEntity> searchUserDocuments(
            @Param("userId") UUID userId,
            @Param("category") String category,
            @Param("status") String status,
            @Param("search") String search);
}
