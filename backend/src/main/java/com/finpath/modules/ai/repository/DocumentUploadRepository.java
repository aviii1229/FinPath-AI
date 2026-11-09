package com.finpath.modules.ai.repository;

import com.finpath.modules.ai.entity.DocumentUploadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentUploadRepository extends JpaRepository<DocumentUploadEntity, UUID> {
    List<DocumentUploadEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);
}

