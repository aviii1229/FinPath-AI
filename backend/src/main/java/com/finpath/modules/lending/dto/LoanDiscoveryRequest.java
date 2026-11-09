package com.finpath.modules.lending.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class LoanDiscoveryRequest {

    @NotNull(message = "Requested loan amount is required")
    @DecimalMin(value = "500.0", message = "Minimum loan amount is 500")
    private BigDecimal requestedAmount;

    @NotNull(message = "Monthly income is required")
    @DecimalMin(value = "500.0", message = "Minimum monthly income is 500")
    private BigDecimal monthlyIncome;

    @NotBlank(message = "Employment type is required")
    private String employmentType = "SALARIED"; // SALARIED, SELF_EMPLOYED, BUSINESS_OWNER, GIG_CONTRACTOR

    private BigDecimal existingEmis = BigDecimal.ZERO;

    @NotNull(message = "Preferred tenure is required")
    @Min(value = 6, message = "Minimum tenure is 6 months")
    private Integer preferredTenureMonths = 36;

    @NotBlank(message = "Loan purpose is required")
    private String loanPurpose = "PERSONAL"; // PERSONAL, HOME_PURCHASE, VEHICLE_PURCHASE, DEBT_CONSOLIDATION, EDUCATION, BUSINESS_EXPANSION

    private Integer estimatedCreditScore = 720;

    public LoanDiscoveryRequest() {}

    public LoanDiscoveryRequest(BigDecimal requestedAmount, BigDecimal monthlyIncome, String employmentType, BigDecimal existingEmis, Integer preferredTenureMonths, String loanPurpose, Integer estimatedCreditScore) {
        this.requestedAmount = requestedAmount;
        this.monthlyIncome = monthlyIncome;
        this.employmentType = employmentType;
        this.existingEmis = existingEmis != null ? existingEmis : BigDecimal.ZERO;
        this.preferredTenureMonths = preferredTenureMonths != null ? preferredTenureMonths : 36;
        this.loanPurpose = loanPurpose != null ? loanPurpose : "PERSONAL";
        this.estimatedCreditScore = estimatedCreditScore != null ? estimatedCreditScore : 720;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(BigDecimal requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public BigDecimal getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(BigDecimal monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public BigDecimal getExistingEmis() {
        return existingEmis != null ? existingEmis : BigDecimal.ZERO;
    }

    public void setExistingEmis(BigDecimal existingEmis) {
        this.existingEmis = existingEmis;
    }

    public Integer getPreferredTenureMonths() {
        return preferredTenureMonths;
    }

    public void setPreferredTenureMonths(Integer preferredTenureMonths) {
        this.preferredTenureMonths = preferredTenureMonths;
    }

    public String getLoanPurpose() {
        return loanPurpose;
    }

    public void setLoanPurpose(String loanPurpose) {
        this.loanPurpose = loanPurpose;
    }

    public Integer getEstimatedCreditScore() {
        return estimatedCreditScore != null ? estimatedCreditScore : 720;
    }

    public void setEstimatedCreditScore(Integer estimatedCreditScore) {
        this.estimatedCreditScore = estimatedCreditScore;
    }
}

