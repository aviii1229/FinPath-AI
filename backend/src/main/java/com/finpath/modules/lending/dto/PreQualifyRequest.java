package com.finpath.modules.lending.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class PreQualifyRequest {

    @NotNull(message = "Requested amount is required")
    private BigDecimal requestedAmount;

    @NotNull(message = "Preferred term in months is required")
    private Integer preferredTermMonths;

    private String loanType = "PERSONAL";

    public PreQualifyRequest() {}

    public PreQualifyRequest(BigDecimal requestedAmount, Integer preferredTermMonths, String loanType) {
        this.requestedAmount = requestedAmount;
        this.preferredTermMonths = preferredTermMonths;
        this.loanType = loanType;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(BigDecimal requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public Integer getPreferredTermMonths() {
        return preferredTermMonths;
    }

    public void setPreferredTermMonths(Integer preferredTermMonths) {
        this.preferredTermMonths = preferredTermMonths;
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }
}
