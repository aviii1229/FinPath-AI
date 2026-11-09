package com.finpath.modules.lending.repository;

import com.finpath.modules.lending.entity.CreditProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CreditProfileRepository extends JpaRepository<CreditProfileEntity, UUID> {
    Optional<CreditProfileEntity> findByUserId(UUID userId);
}

