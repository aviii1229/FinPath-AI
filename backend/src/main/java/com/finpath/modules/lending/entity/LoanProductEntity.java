package com.finpath.modules.lending.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "loan_products")
@EntityListeners(AuditingEntityListener.class)
public class LoanProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "product_name", nullable = false, length = 150)
    private String productName;

    @Column(name = "loan_type", nullable = false, length = 50)
    private String loanType; // PERSONAL, MORTGAGE, AUTO, SME_CREDIT

    @Column(name = "min_principal", nullable = false, precision = 15, scale = 2)
    private BigDecimal minPrincipal;

    @Column(name = "max_principal", nullable = false, precision = 15, scale = 2)
    private BigDecimal maxPrincipal;

    @Column(name = "min_interest_rate", nullable = false, precision = 6, scale = 4)
    private BigDecimal minInterestRate; // e.g. 0.0549 = 5.49%

    @Column(name = "max_interest_rate", nullable = false, precision = 6, scale = 4)
    private BigDecimal maxInterestRate;

    @Column(name = "min_term_months", nullable = false)
    private int minTermMonths;

    @Column(name = "max_term_months", nullable = false)
    private int maxTermMonths;

    @Column(name = "min_credit_score", nullable = false)
    private int minCreditScore = 600;

    @Column(name = "lender_name", length = 100)
    private String lenderName = "FinPath Prime Lending";

    @Column(name = "processing_fee_percentage", precision = 5, scale = 2)
    private BigDecimal processingFeePercentage = BigDecimal.valueOf(1.00);

    @Column(name = "interest_rate_type", length = 20)
    private String interestRateType = "FIXED";

    @Column(name = "prepayment_terms", length = 255)
    private String prepaymentTerms = "Zero penalty on prepayment after 6 months";

    @Column(columnDefinition = "TEXT")
    private String features;

    @Column(name = "is_simulated", nullable = false)
    private boolean isSimulated = true;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public LoanProductEntity() {}

    public LoanProductEntity(String productName, String loanType, BigDecimal minPrincipal, BigDecimal maxPrincipal, BigDecimal minInterestRate, BigDecimal maxInterestRate, int minTermMonths, int maxTermMonths, int minCreditScore) {
        this.productName = productName;
        this.loanType = loanType;
        this.minPrincipal = minPrincipal;
        this.maxPrincipal = maxPrincipal;
        this.minInterestRate = minInterestRate;
        this.maxInterestRate = maxInterestRate;
        this.minTermMonths = minTermMonths;
        this.maxTermMonths = maxTermMonths;
        this.minCreditScore = minCreditScore;
        this.isActive = true;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public BigDecimal getMinPrincipal() {
        return minPrincipal;
    }

    public void setMinPrincipal(BigDecimal minPrincipal) {
        this.minPrincipal = minPrincipal;
    }

    public BigDecimal getMaxPrincipal() {
        return maxPrincipal;
    }

    public void setMaxPrincipal(BigDecimal maxPrincipal) {
        this.maxPrincipal = maxPrincipal;
    }

    public BigDecimal getMinInterestRate() {
        return minInterestRate;
    }

    public void setMinInterestRate(BigDecimal minInterestRate) {
        this.minInterestRate = minInterestRate;
    }

    public BigDecimal getMaxInterestRate() {
        return maxInterestRate;
    }

    public void setMaxInterestRate(BigDecimal maxInterestRate) {
        this.maxInterestRate = maxInterestRate;
    }

    public int getMinTermMonths() {
        return minTermMonths;
    }

    public void setMinTermMonths(int minTermMonths) {
        this.minTermMonths = minTermMonths;
    }

    public int getMaxTermMonths() {
        return maxTermMonths;
    }

    public void setMaxTermMonths(int maxTermMonths) {
        this.maxTermMonths = maxTermMonths;
    }

    public int getMinCreditScore() {
        return minCreditScore;
    }

    public void setMinCreditScore(int minCreditScore) {
        this.minCreditScore = minCreditScore;
    }

    public String getLenderName() {
        return lenderName != null ? lenderName : "FinPath Prime Lending";
    }

    public void setLenderName(String lenderName) {
        this.lenderName = lenderName;
    }

    public BigDecimal getProcessingFeePercentage() {
        return processingFeePercentage != null ? processingFeePercentage : BigDecimal.valueOf(1.00);
    }

    public void setProcessingFeePercentage(BigDecimal processingFeePercentage) {
        this.processingFeePercentage = processingFeePercentage;
    }

    public String getInterestRateType() {
        return interestRateType != null ? interestRateType : "FIXED";
    }

    public void setInterestRateType(String interestRateType) {
        this.interestRateType = interestRateType;
    }

    public String getPrepaymentTerms() {
        return prepaymentTerms != null ? prepaymentTerms : "Zero penalty on prepayment after 6 months";
    }

    public void setPrepaymentTerms(String prepaymentTerms) {
        this.prepaymentTerms = prepaymentTerms;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
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

