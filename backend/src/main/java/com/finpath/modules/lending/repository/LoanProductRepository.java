package com.finpath.modules.lending.repository;

import com.finpath.modules.lending.entity.LoanProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LoanProductRepository extends JpaRepository<LoanProductEntity, UUID> {
    List<LoanProductEntity> findByIsActiveTrue();
}

