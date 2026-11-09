package com.finpath.modules.insurance.repository;

import com.finpath.modules.insurance.entity.InsuranceProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InsuranceProductRepository extends JpaRepository<InsuranceProductEntity, UUID> {
    List<InsuranceProductEntity> findByIsActiveTrue();
    List<InsuranceProductEntity> findByCategoryAndIsActiveTrue(String category);
}

