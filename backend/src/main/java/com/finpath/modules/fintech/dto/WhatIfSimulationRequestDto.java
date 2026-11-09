package com.finpath.modules.fintech.dto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class WhatIfSimulationRequestDto {

    private BigDecimal additionalMonthlySavings = BigDecimal.ZERO;
    private Map<String, BigDecimal> categoryReductionsPercent = new HashMap<>(); // Category -> percentage reduction (0-100)
    private BigDecimal simulatedLoanAmount; // Optional new loan borrowing
    private Integer simulatedLoanTenureMonths = 36;
    private BigDecimal simulatedLoanApr = BigDecimal.valueOf(8.99);
    private BigDecimal simulatedInsuranceMonthlyPremium = BigDecimal.ZERO; // e.g. ₹3,308
    private String changedGoalCategory;
    private BigDecimal changedGoalTargetAmount;

    public WhatIfSimulationRequestDto() {}

    public WhatIfSimulationRequestDto(BigDecimal additionalMonthlySavings,
                                     Map<String, BigDecimal> categoryReductionsPercent,
                                     BigDecimal simulatedLoanAmount,
                                     Integer simulatedLoanTenureMonths,
                                     BigDecimal simulatedLoanApr,
                                     BigDecimal simulatedInsuranceMonthlyPremium) {
        this(additionalMonthlySavings, categoryReductionsPercent, simulatedLoanAmount, simulatedLoanTenureMonths, simulatedLoanApr, simulatedInsuranceMonthlyPremium, null, null);
    }

    public WhatIfSimulationRequestDto(BigDecimal additionalMonthlySavings,
                                     Map<String, BigDecimal> categoryReductionsPercent,
                                     BigDecimal simulatedLoanAmount,
                                     Integer simulatedLoanTenureMonths,
                                     BigDecimal simulatedLoanApr,
                                     BigDecimal simulatedInsuranceMonthlyPremium,
                                     String changedGoalCategory,
                                     BigDecimal changedGoalTargetAmount) {
        this.additionalMonthlySavings = additionalMonthlySavings != null ? additionalMonthlySavings : BigDecimal.ZERO;
        this.categoryReductionsPercent = categoryReductionsPercent != null ? categoryReductionsPercent : new HashMap<>();
        this.simulatedLoanAmount = simulatedLoanAmount;
        this.simulatedLoanTenureMonths = simulatedLoanTenureMonths != null ? simulatedLoanTenureMonths : 36;
        this.simulatedLoanApr = simulatedLoanApr != null ? simulatedLoanApr : BigDecimal.valueOf(8.99);
        this.simulatedInsuranceMonthlyPremium = simulatedInsuranceMonthlyPremium != null ? simulatedInsuranceMonthlyPremium : BigDecimal.ZERO;
        this.changedGoalCategory = changedGoalCategory;
        this.changedGoalTargetAmount = changedGoalTargetAmount;
    }

    public BigDecimal getAdditionalMonthlySavings() {
        return additionalMonthlySavings;
    }

    public void setAdditionalMonthlySavings(BigDecimal additionalMonthlySavings) {
        this.additionalMonthlySavings = additionalMonthlySavings;
    }

    public Map<String, BigDecimal> getCategoryReductionsPercent() {
        return categoryReductionsPercent;
    }

    public void setCategoryReductionsPercent(Map<String, BigDecimal> categoryReductionsPercent) {
        this.categoryReductionsPercent = categoryReductionsPercent;
    }

    public BigDecimal getSimulatedLoanAmount() {
        return simulatedLoanAmount;
    }

    public void setSimulatedLoanAmount(BigDecimal simulatedLoanAmount) {
        this.simulatedLoanAmount = simulatedLoanAmount;
    }

    public Integer getSimulatedLoanTenureMonths() {
        return simulatedLoanTenureMonths;
    }

    public void setSimulatedLoanTenureMonths(Integer simulatedLoanTenureMonths) {
        this.simulatedLoanTenureMonths = simulatedLoanTenureMonths;
    }

    public BigDecimal getSimulatedLoanApr() {
        return simulatedLoanApr;
    }

    public void setSimulatedLoanApr(BigDecimal simulatedLoanApr) {
        this.simulatedLoanApr = simulatedLoanApr;
    }

    public BigDecimal getSimulatedInsuranceMonthlyPremium() {
        return simulatedInsuranceMonthlyPremium;
    }

    public void setSimulatedInsuranceMonthlyPremium(BigDecimal simulatedInsuranceMonthlyPremium) {
        this.simulatedInsuranceMonthlyPremium = simulatedInsuranceMonthlyPremium;
    }

    public String getChangedGoalCategory() {
        return changedGoalCategory;
    }

    public void setChangedGoalCategory(String changedGoalCategory) {
        this.changedGoalCategory = changedGoalCategory;
    }

    public BigDecimal getChangedGoalTargetAmount() {
        return changedGoalTargetAmount;
    }

    public void setChangedGoalTargetAmount(BigDecimal changedGoalTargetAmount) {
        this.changedGoalTargetAmount = changedGoalTargetAmount;
    }
}

