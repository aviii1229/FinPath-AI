package com.finpath.modules.fintech.repository;

import com.finpath.modules.fintech.entity.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {

    @Query("SELECT t FROM TransactionEntity t WHERE t.account.user.id = :userId ORDER BY t.transactionDate DESC, t.createdAt DESC")
    List<TransactionEntity> findAllByUserId(@Param("userId") UUID userId);

    @Query("SELECT t FROM TransactionEntity t WHERE t.account.user.id = :userId ORDER BY t.transactionDate DESC, t.createdAt DESC")
    Page<TransactionEntity> findAllByUserIdPaginated(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT t FROM TransactionEntity t WHERE t.account.user.id = :userId AND LOWER(t.category) = LOWER(:category) ORDER BY t.transactionDate DESC")
    List<TransactionEntity> findByUserIdAndCategory(@Param("userId") UUID userId, @Param("category") String category);
}

