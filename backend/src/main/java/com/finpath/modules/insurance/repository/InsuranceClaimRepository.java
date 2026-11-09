package com.finpath.modules.insurance.repository;

import com.finpath.modules.insurance.entity.InsuranceClaimEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InsuranceClaimRepository extends JpaRepository<InsuranceClaimEntity, UUID> {
    List<InsuranceClaimEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);
    Optional<InsuranceClaimEntity> findByClaimNumber(String claimNumber);
}

