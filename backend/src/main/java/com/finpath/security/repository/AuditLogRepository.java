package com.finpath.security.repository;

import com.finpath.security.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogEntity, UUID> {

    List<AuditLogEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<AuditLogEntity> findByActionOrderByCreatedAtDesc(String action);

    long countByUserIdAndAction(UUID userId, String action);
}
