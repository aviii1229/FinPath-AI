package com.finpath.modules.fintech.repository;

import com.finpath.modules.fintech.entity.BudgetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BudgetRepository extends JpaRepository<BudgetEntity, UUID> {
    List<BudgetEntity> findByUserId(UUID userId);
    Optional<BudgetEntity> findByUserIdAndCategory(UUID userId, String category);
}

