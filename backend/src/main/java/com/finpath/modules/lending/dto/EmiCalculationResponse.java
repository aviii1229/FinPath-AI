package com.finpath.modules.lending.dto;

import java.math.BigDecimal;
import java.util.List;

public class EmiCalculationResponse {
    private BigDecimal principal;
    private BigDecimal annualInterestRate;
    private int tenureMonths;
    private BigDecimal monthlyEmi;
    private BigDecimal totalInterest;
    private BigDecimal totalRepayment;
    private BigDecimal interestToPrincipalRatio;
    private List<EmiBreakdownItemDto> amortizationSchedule;

    public EmiCalculationResponse() {}

    public EmiCalculationResponse(BigDecimal principal, BigDecimal annualInterestRate, int tenureMonths, BigDecimal monthlyEmi, BigDecimal totalInterest, BigDecimal totalRepayment, BigDecimal interestToPrincipalRatio, List<EmiBreakdownItemDto> amortizationSchedule) {
        this.principal = principal;
        this.annualInterestRate = annualInterestRate;
        this.tenureMonths = tenureMonths;
        this.monthlyEmi = monthlyEmi;
        this.totalInterest = totalInterest;
        this.totalRepayment = totalRepayment;
        this.interestToPrincipalRatio = interestToPrincipalRatio;
        this.amortizationSchedule = amortizationSchedule;
    }

    public BigDecimal getPrincipal() {
        return principal;
    }

    public void setPrincipal(BigDecimal principal) {
        this.principal = principal;
    }

    public BigDecimal getAnnualInterestRate() {
        return annualInterestRate;
    }

    public void setAnnualInterestRate(BigDecimal annualInterestRate) {
        this.annualInterestRate = annualInterestRate;
    }

    public int getTenureMonths() {
        return tenureMonths;
    }

    public void setTenureMonths(int tenureMonths) {
        this.tenureMonths = tenureMonths;
    }

    public BigDecimal getMonthlyEmi() {
        return monthlyEmi;
    }

    public void setMonthlyEmi(BigDecimal monthlyEmi) {
        this.monthlyEmi = monthlyEmi;
    }

    public BigDecimal getTotalInterest() {
        return totalInterest;
    }

    public void setTotalInterest(BigDecimal totalInterest) {
        this.totalInterest = totalInterest;
    }

    public BigDecimal getTotalRepayment() {
        return totalRepayment;
    }

    public void setTotalRepayment(BigDecimal totalRepayment) {
        this.totalRepayment = totalRepayment;
    }

    public BigDecimal getInterestToPrincipalRatio() {
        return interestToPrincipalRatio;
    }

    public void setInterestToPrincipalRatio(BigDecimal interestToPrincipalRatio) {
        this.interestToPrincipalRatio = interestToPrincipalRatio;
    }

    public List<EmiBreakdownItemDto> getAmortizationSchedule() {
        return amortizationSchedule;
    }

    public void setAmortizationSchedule(List<EmiBreakdownItemDto> amortizationSchedule) {
        this.amortizationSchedule = amortizationSchedule;
    }
}

