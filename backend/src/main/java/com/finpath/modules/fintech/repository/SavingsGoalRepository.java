package com.finpath.modules.fintech.repository;

import com.finpath.modules.fintech.entity.SavingsGoalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SavingsGoalRepository extends JpaRepository<SavingsGoalEntity, UUID> {
    List<SavingsGoalEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);
}

