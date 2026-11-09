package com.finpath.modules.lending.entity;

import com.finpath.security.entity.UserEntity;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "loan_applications")
@EntityListeners(AuditingEntityListener.class)
public class LoanApplicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "application_number", nullable = false, unique = true, length = 64)
    private String applicationNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_product_id", nullable = false)
    private LoanProductEntity loanProduct;

    @Column(name = "requested_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal requestedAmount;

    @Column(name = "requested_term_months", nullable = false)
    private int requestedTermMonths;

    @Column(name = "loan_purpose", nullable = false, length = 255)
    private String loanPurpose;

    @Column(name = "calculated_dti", precision = 5, scale = 2)
    private BigDecimal calculatedDti;

    @Column(name = "monthly_emi", precision = 15, scale = 2)
    private BigDecimal monthlyEmi;

    @Column(name = "apr", precision = 6, scale = 4)
    private BigDecimal apr;

    @Column(nullable = false, length = 30)
    private String status = "SUBMITTED"; // DRAFT, SUBMITTED, UNDER_REVIEW, DOCUMENTS_REQUIRED, APPROVED, DISBURSED, DECLINED

    @Column(name = "employment_type", length = 50)
    private String employmentType = "SALARIED";

    @Column(name = "monthly_income", precision = 15, scale = 2)
    private BigDecimal monthlyIncome;

    @Column(name = "existing_emis", precision = 15, scale = 2)
    private BigDecimal existingEmis;

    @Column(name = "missing_documents", columnDefinition = "TEXT")
    private String missingDocuments;

    @Column(name = "decision_notes", columnDefinition = "TEXT")
    private String decisionNotes;

    @Column(name = "credit_score")
    private Integer creditScore = 720;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public LoanApplicationEntity() {}

    public LoanApplicationEntity(String applicationNumber, UserEntity user, LoanProductEntity loanProduct, BigDecimal requestedAmount, int requestedTermMonths, String loanPurpose, BigDecimal calculatedDti, BigDecimal monthlyEmi, BigDecimal apr, String status) {
        this.applicationNumber = applicationNumber;
        this.user = user;
        this.loanProduct = loanProduct;
        this.requestedAmount = requestedAmount;
        this.requestedTermMonths = requestedTermMonths;
        this.loanPurpose = loanPurpose;
        this.calculatedDti = calculatedDti;
        this.monthlyEmi = monthlyEmi;
        this.apr = apr;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public LoanProductEntity getLoanProduct() {
        return loanProduct;
    }

    public void setLoanProduct(LoanProductEntity loanProduct) {
        this.loanProduct = loanProduct;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(BigDecimal requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public int getRequestedTermMonths() {
        return requestedTermMonths;
    }

    public void setRequestedTermMonths(int requestedTermMonths) {
        this.requestedTermMonths = requestedTermMonths;
    }

    public String getLoanPurpose() {
        return loanPurpose;
    }

    public void setLoanPurpose(String loanPurpose) {
        this.loanPurpose = loanPurpose;
    }

    public BigDecimal getCalculatedDti() {
        return calculatedDti;
    }

    public void setCalculatedDti(BigDecimal calculatedDti) {
        this.calculatedDti = calculatedDti;
    }

    public BigDecimal getMonthlyEmi() {
        return monthlyEmi;
    }

    public void setMonthlyEmi(BigDecimal monthlyEmi) {
        this.monthlyEmi = monthlyEmi;
    }

    public BigDecimal getApr() {
        return apr;
    }

    public void setApr(BigDecimal apr) {
        this.apr = apr;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmploymentType() {
        return employmentType != null ? employmentType : "SALARIED";
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public BigDecimal getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(BigDecimal monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public BigDecimal getExistingEmis() {
        return existingEmis;
    }

    public void setExistingEmis(BigDecimal existingEmis) {
        this.existingEmis = existingEmis;
    }

    public String getMissingDocuments() {
        return missingDocuments;
    }

    public void setMissingDocuments(String missingDocuments) {
        this.missingDocuments = missingDocuments;
    }

    public String getDecisionNotes() {
        return decisionNotes;
    }

    public void setDecisionNotes(String decisionNotes) {
        this.decisionNotes = decisionNotes;
    }

    public Integer getCreditScore() {
        return creditScore != null ? creditScore : 720;
    }

    public void setCreditScore(Integer creditScore) {
        this.creditScore = creditScore;
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

