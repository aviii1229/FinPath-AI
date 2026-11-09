package com.finpath.modules.insurance.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "insurance_products")
@EntityListeners(AuditingEntityListener.class)
public class InsuranceProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "carrier_name", nullable = false, length = 100)
    private String carrierName;

    @Column(name = "product_name", nullable = false, length = 150)
    private String productName;

    @Column(nullable = false, length = 50)
    private String category; // HEALTH, TERM_LIFE, AUTO, HOMEOWNERS

    @Column(name = "base_premium_monthly", nullable = false, precision = 15, scale = 2)
    private BigDecimal basePremiumMonthly;

    @Column(name = "coverage_amount_min", nullable = false, precision = 15, scale = 2)
    private BigDecimal coverageAmountMin;

    @Column(name = "coverage_amount_max", nullable = false, precision = 15, scale = 2)
    private BigDecimal coverageAmountMax;

    @Column(name = "terms_summary", nullable = false, length = 1000)
    private String termsSummary;

    @Column(name = "waiting_period_days")
    private Integer waitingPeriodDays = 0;

    @Column(name = "benefits", columnDefinition = "TEXT")
    private String benefits;

    @Column(name = "exclusions", columnDefinition = "TEXT")
    private String exclusions;

    @Column(name = "deductible_options", length = 100)
    private String deductibleOptions = "$500 / $1,000 / $2,500";

    @Column(name = "copay_percentage", precision = 5, scale = 2)
    private BigDecimal copayPercentage = BigDecimal.valueOf(10.00);

    @Column(name = "is_simulated", nullable = false)
    private boolean isSimulated = true;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public InsuranceProductEntity() {}

    public InsuranceProductEntity(String carrierName, String productName, String category, BigDecimal basePremiumMonthly, BigDecimal coverageAmountMin, BigDecimal coverageAmountMax, String termsSummary) {
        this.carrierName = carrierName;
        this.productName = productName;
        this.category = category;
        this.basePremiumMonthly = basePremiumMonthly;
        this.coverageAmountMin = coverageAmountMin;
        this.coverageAmountMax = coverageAmountMax;
        this.termsSummary = termsSummary;
        this.isActive = true;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getBasePremiumMonthly() {
        return basePremiumMonthly;
    }

    public void setBasePremiumMonthly(BigDecimal basePremiumMonthly) {
        this.basePremiumMonthly = basePremiumMonthly;
    }

    public BigDecimal getCoverageAmountMin() {
        return coverageAmountMin;
    }

    public void setCoverageAmountMin(BigDecimal coverageAmountMin) {
        this.coverageAmountMin = coverageAmountMin;
    }

    public BigDecimal getCoverageAmountMax() {
        return coverageAmountMax;
    }

    public void setCoverageAmountMax(BigDecimal coverageAmountMax) {
        this.coverageAmountMax = coverageAmountMax;
    }

    public String getTermsSummary() {
        return termsSummary;
    }

    public void setTermsSummary(String termsSummary) {
        this.termsSummary = termsSummary;
    }

    public Integer getWaitingPeriodDays() {
        return waitingPeriodDays;
    }

    public void setWaitingPeriodDays(Integer waitingPeriodDays) {
        this.waitingPeriodDays = waitingPeriodDays;
    }

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public String getExclusions() {
        return exclusions;
    }

    public void setExclusions(String exclusions) {
        this.exclusions = exclusions;
    }

    public String getDeductibleOptions() {
        return deductibleOptions;
    }

    public void setDeductibleOptions(String deductibleOptions) {
        this.deductibleOptions = deductibleOptions;
    }

    public BigDecimal getCopayPercentage() {
        return copayPercentage;
    }

    public void setCopayPercentage(BigDecimal copayPercentage) {
        this.copayPercentage = copayPercentage;
    }

    public boolean isSimulated() {
        return isSimulated;
    }

    public void setSimulated(boolean simulated) {
        isSimulated = simulated;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}

