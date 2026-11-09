package com.finpath.modules.lending.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public class CreateLoanApplicationRequest {

    @NotNull(message = "Loan product ID is required")
    private UUID loanProductId;

    @NotNull(message = "Requested amount is required")
    private BigDecimal requestedAmount;

    @NotNull(message = "Requested term in months is required")
    private Integer requestedTermMonths;

    @NotBlank(message = "Loan purpose is required")
    private String loanPurpose;

    private String employmentType = "SALARIED";
    private BigDecimal monthlyIncome;
    private BigDecimal existingEmis;
    private String status = "SUBMITTED"; // DRAFT or SUBMITTED

    public CreateLoanApplicationRequest() {}

    public CreateLoanApplicationRequest(UUID loanProductId, BigDecimal requestedAmount, Integer requestedTermMonths, String loanPurpose) {
        this.loanProductId = loanProductId;
        this.requestedAmount = requestedAmount;
        this.requestedTermMonths = requestedTermMonths;
        this.loanPurpose = loanPurpose;
    }

    public CreateLoanApplicationRequest(String loanProductId, BigDecimal requestedAmount, Integer requestedTermMonths, String loanPurpose) {
        this(UUID.fromString(loanProductId), requestedAmount, requestedTermMonths, loanPurpose);
    }

    public UUID getLoanProductId() {
        return loanProductId;
    }

    public void setLoanProductId(UUID loanProductId) {
        this.loanProductId = loanProductId;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(BigDecimal requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public Integer getRequestedTermMonths() {
        return requestedTermMonths;
    }

    public void setRequestedTermMonths(Integer requestedTermMonths) {
        this.requestedTermMonths = requestedTermMonths;
    }

    public String getLoanPurpose() {
        return loanPurpose;
    }

    public void setLoanPurpose(String loanPurpose) {
        this.loanPurpose = loanPurpose;
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

    public String getStatus() {
        return status != null ? status : "SUBMITTED";
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
