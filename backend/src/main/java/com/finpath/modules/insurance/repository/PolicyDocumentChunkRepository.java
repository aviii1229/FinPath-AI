package com.finpath.modules.insurance.repository;

import com.finpath.modules.insurance.entity.PolicyDocumentChunkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PolicyDocumentChunkRepository extends JpaRepository<PolicyDocumentChunkEntity, UUID> {

    List<PolicyDocumentChunkEntity> findByDocumentId(UUID documentId);

    List<PolicyDocumentChunkEntity> findByCategory(String category);

    @Query("SELECT c FROM PolicyDocumentChunkEntity c WHERE LOWER(c.chunkText) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.sectionTitle) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<PolicyDocumentChunkEntity> searchByKeyword(@Param("keyword") String keyword);

    List<PolicyDocumentChunkEntity> findTop20ByOrderByCreatedAtDesc();
}

