package com.finpath.modules.insurance.repository;

import com.finpath.modules.insurance.entity.InsurancePolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InsurancePolicyRepository extends JpaRepository<InsurancePolicyEntity, UUID> {
    List<InsurancePolicyEntity> findByUserId(UUID userId);
    Optional<InsurancePolicyEntity> findByPolicyNumber(String policyNumber);
}

