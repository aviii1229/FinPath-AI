package com.finpath.modules.lending.repository;

import com.finpath.modules.lending.entity.LoanApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplicationEntity, UUID> {
    List<LoanApplicationEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);
    Optional<LoanApplicationEntity> findByApplicationNumber(String applicationNumber);
}

