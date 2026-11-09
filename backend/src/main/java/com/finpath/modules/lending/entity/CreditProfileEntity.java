package com.finpath.modules.lending.entity;

import com.finpath.security.entity.UserEntity;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "credit_profiles")
@EntityListeners(AuditingEntityListener.class)
public class CreditProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @Column(name = "credit_score", nullable = false)
    private int creditScore = 720;

    @Column(name = "total_existing_debt", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalExistingDebt = BigDecimal.ZERO;

    @Column(name = "monthly_debt_obligations", nullable = false, precision = 15, scale = 2)
    private BigDecimal monthlyDebtObligations = BigDecimal.ZERO;

    @Column(name = "delinquencies_count", nullable = false)
    private int delinquenciesCount = 0;

    @Column(name = "bureau_report_date", nullable = false)
    private LocalDate bureauReportDate = LocalDate.now();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public CreditProfileEntity() {}

    public CreditProfileEntity(UserEntity user, int creditScore, BigDecimal totalExistingDebt, BigDecimal monthlyDebtObligations) {
        this.user = user;
        this.creditScore = creditScore;
        this.totalExistingDebt = totalExistingDebt;
        this.monthlyDebtObligations = monthlyDebtObligations;
        this.delinquenciesCount = 0;
        this.bureauReportDate = LocalDate.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public int getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(int creditScore) {
        this.creditScore = creditScore;
    }

    public BigDecimal getTotalExistingDebt() {
        return totalExistingDebt;
    }

    public void setTotalExistingDebt(BigDecimal totalExistingDebt) {
        this.totalExistingDebt = totalExistingDebt;
    }

    public BigDecimal getMonthlyDebtObligations() {
        return monthlyDebtObligations;
    }

    public void setMonthlyDebtObligations(BigDecimal monthlyDebtObligations) {
        this.monthlyDebtObligations = monthlyDebtObligations;
    }

    public int getDelinquenciesCount() {
        return delinquenciesCount;
    }

    public void setDelinquenciesCount(int delinquenciesCount) {
        this.delinquenciesCount = delinquenciesCount;
    }

    public LocalDate getBureauReportDate() {
        return bureauReportDate;
    }

    public void setBureauReportDate(LocalDate bureauReportDate) {
        this.bureauReportDate = bureauReportDate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}

