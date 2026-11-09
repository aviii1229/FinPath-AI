package com.finpath.modules.ai.repository;

import com.finpath.modules.ai.entity.AiChatSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AiChatSessionRepository extends JpaRepository<AiChatSessionEntity, UUID> {
    List<AiChatSessionEntity> findByUserIdOrderByUpdatedAtDesc(UUID userId);
}

