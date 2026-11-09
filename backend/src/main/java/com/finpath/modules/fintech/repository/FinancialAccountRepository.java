package com.finpath.modules.fintech.repository;

import com.finpath.modules.fintech.entity.FinancialAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FinancialAccountRepository extends JpaRepository<FinancialAccountEntity, UUID> {
    List<FinancialAccountEntity> findByUserId(UUID userId);
}

