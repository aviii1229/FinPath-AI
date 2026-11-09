package com.finpath.modules.lending.dto;

import java.math.BigDecimal;
import java.util.List;

public class LoanChecklistRequest {
    private String loanType = "PERSONAL";
    private String employmentType = "SALARIED";
    private BigDecimal loanAmount = BigDecimal.valueOf(25000);

    public LoanChecklistRequest() {}

    public LoanChecklistRequest(String loanType, String employmentType, BigDecimal loanAmount) {
        this.loanType = loanType != null ? loanType : "PERSONAL";
        this.employmentType = employmentType != null ? employmentType : "SALARIED";
        this.loanAmount = loanAmount != null ? loanAmount : BigDecimal.valueOf(25000);
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

