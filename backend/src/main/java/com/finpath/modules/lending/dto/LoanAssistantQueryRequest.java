package com.finpath.modules.lending.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public class LoanAssistantQueryRequest {

    @NotBlank(message = "Query question is required")
    private String query;

    private String loanType = "PERSONAL";
    private String employmentType = "SALARIED";
    private BigDecimal loanAmount;

    public LoanAssistantQueryRequest() {}

    public LoanAssistantQueryRequest(String query, String loanType, String employmentType, BigDecimal loanAmount) {
        this.query = query;
        this.loanType = loanType != null ? loanType : "PERSONAL";
        this.employmentType = employmentType != null ? employmentType : "SALARIED";
        this.loanAmount = loanAmount;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }
}

