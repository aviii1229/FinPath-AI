package com.finpath.modules.lending.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class LoanApplicationDto {
    private UUID id;
    private String applicationNumber;
    private UUID loanProductId;
    private String productName;
    private String loanType;
    private BigDecimal requestedAmount;
    private int requestedTermMonths;
    private String loanPurpose;
    private BigDecimal calculatedDti;
    private BigDecimal monthlyEmi;
    private BigDecimal apr;
    private String status;
    private Instant createdAt;
    private String employmentType;
    private BigDecimal monthlyIncome;
    private BigDecimal existingEmis;
    private String missingDocuments;
    private String decisionNotes;
    private Integer creditScore;

    public LoanApplicationDto() {}

    public LoanApplicationDto(UUID id, String applicationNumber, UUID loanProductId, String productName, String loanType, BigDecimal requestedAmount, int requestedTermMonths, String loanPurpose, BigDecimal calculatedDti, BigDecimal monthlyEmi, BigDecimal apr, String status, Instant createdAt) {
        this(id, applicationNumber, loanProductId, productName, loanType, requestedAmount, requestedTermMonths, loanPurpose, calculatedDti, monthlyEmi, apr, status, createdAt, "SALARIED", null, null, null, null, 720);
    }

    public LoanApplicationDto(UUID id, String applicationNumber, UUID loanProductId, String productName, String loanType, BigDecimal requestedAmount, int requestedTermMonths, String loanPurpose, BigDecimal calculatedDti, BigDecimal monthlyEmi, BigDecimal apr, String status, Instant createdAt, String employmentType, BigDecimal monthlyIncome, BigDecimal existingEmis, String missingDocuments, String decisionNotes, Integer creditScore) {
        this.id = id;
        this.applicationNumber = applicationNumber;
        this.loanProductId = loanProductId;
        this.productName = productName;
        this.loanType = loanType;
        this.requestedAmount = requestedAmount;
        this.requestedTermMonths = requestedTermMonths;
        this.loanPurpose = loanPurpose;
        this.calculatedDti = calculatedDti;
        this.monthlyEmi = monthlyEmi;
        this.apr = apr;
        this.status = status;
        this.createdAt = createdAt;
        this.employmentType = employmentType;
        this.monthlyIncome = monthlyIncome;
        this.existingEmis = existingEmis;
        this.missingDocuments = missingDocuments;
        this.decisionNotes = decisionNotes;
        this.creditScore = creditScore;
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

    public UUID getLoanProductId() {
        return loanProductId;
    }

    public void setLoanProductId(UUID loanProductId) {
        this.loanProductId = loanProductId;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getEmploymentType() {
        return employmentType;
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
        return creditScore;
    }

    public void setCreditScore(Integer creditScore) {
        this.creditScore = creditScore;
    }
}

