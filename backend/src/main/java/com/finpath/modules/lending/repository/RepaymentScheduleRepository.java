package com.finpath.modules.lending.repository;

import com.finpath.modules.lending.entity.RepaymentScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RepaymentScheduleRepository extends JpaRepository<RepaymentScheduleEntity, UUID> {
    List<RepaymentScheduleEntity> findByApplicationIdOrderByInstallmentNumberAsc(UUID applicationId);
}

