package com.finpath.modules.fintech.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WhatIfSimulationResponseDto {

    private BigDecimal baselineIncome;
    private BigDecimal baselineExpenses;
    private BigDecimal baselineMonthlySurplus;
    private BigDecimal baselineSavingsRate;
    private BigDecimal baselineRunwayMonths;

    private BigDecimal simulatedIncome;
    private BigDecimal simulatedExpenses;
    private BigDecimal simulatedMonthlySurplus;
    private BigDecimal simulatedSavingsRate;
    private BigDecimal simulatedRunwayMonths;

    private BigDecimal netMonthlyCashflowDelta; // positive = increased surplus, negative = decreased surplus
    private BigDecimal simulatedLoanEmi = BigDecimal.ZERO;
    private BigDecimal categorySavingsTotal = BigDecimal.ZERO;
    private Map<String, BigDecimal> categoryBreakdownSimulated = new HashMap<>();
    private List<String> impactObservations = new ArrayList<>();
    private String feasibilityVerdict; // HIGHLY_FEASIBLE, BALANCED, CASHFLOW_STRAIN

    public WhatIfSimulationResponseDto() {}

    public BigDecimal getBaselineIncome() { return baselineIncome; }
    public void setBaselineIncome(BigDecimal baselineIncome) { this.baselineIncome = baselineIncome; }

    public BigDecimal getBaselineExpenses() { return baselineExpenses; }
    public void setBaselineExpenses(BigDecimal baselineExpenses) { this.baselineExpenses = baselineExpenses; }

    public BigDecimal getBaselineMonthlySurplus() { return baselineMonthlySurplus; }
    public void setBaselineMonthlySurplus(BigDecimal baselineMonthlySurplus) { this.baselineMonthlySurplus = baselineMonthlySurplus; }

    public BigDecimal getBaselineSavingsRate() { return baselineSavingsRate; }
    public void setBaselineSavingsRate(BigDecimal baselineSavingsRate) { this.baselineSavingsRate = baselineSavingsRate; }

    public BigDecimal getBaselineRunwayMonths() { return baselineRunwayMonths; }
    public void setBaselineRunwayMonths(BigDecimal baselineRunwayMonths) { this.baselineRunwayMonths = baselineRunwayMonths; }

    public BigDecimal getSimulatedIncome() { return simulatedIncome; }
    public void setSimulatedIncome(BigDecimal simulatedIncome) { this.simulatedIncome = simulatedIncome; }

    public BigDecimal getSimulatedExpenses() { return simulatedExpenses; }
    public void setSimulatedExpenses(BigDecimal simulatedExpenses) { this.simulatedExpenses = simulatedExpenses; }

    public BigDecimal getSimulatedMonthlySurplus() { return simulatedMonthlySurplus; }
    public void setSimulatedMonthlySurplus(BigDecimal simulatedMonthlySurplus) { this.simulatedMonthlySurplus = simulatedMonthlySurplus; }

    public BigDecimal getSimulatedSavingsRate() { return simulatedSavingsRate; }
    public void setSimulatedSavingsRate(BigDecimal simulatedSavingsRate) { this.simulatedSavingsRate = simulatedSavingsRate; }

    public BigDecimal getSimulatedRunwayMonths() { return simulatedRunwayMonths; }
    public void setSimulatedRunwayMonths(BigDecimal simulatedRunwayMonths) { this.simulatedRunwayMonths = simulatedRunwayMonths; }

    public BigDecimal getNetMonthlyCashflowDelta() { return netMonthlyCashflowDelta; }
    public void setNetMonthlyCashflowDelta(BigDecimal netMonthlyCashflowDelta) { this.netMonthlyCashflowDelta = netMonthlyCashflowDelta; }

    public BigDecimal getSimulatedLoanEmi() { return simulatedLoanEmi; }
    public void setSimulatedLoanEmi(BigDecimal simulatedLoanEmi) { this.simulatedLoanEmi = simulatedLoanEmi; }

    public BigDecimal getCategorySavingsTotal() { return categorySavingsTotal; }
    public void setCategorySavingsTotal(BigDecimal categorySavingsTotal) { this.categorySavingsTotal = categorySavingsTotal; }

    public Map<String, BigDecimal> getCategoryBreakdownSimulated() { return categoryBreakdownSimulated; }
    public void setCategoryBreakdownSimulated(Map<String, BigDecimal> categoryBreakdownSimulated) { this.categoryBreakdownSimulated = categoryBreakdownSimulated; }

    public List<String> getImpactObservations() { return impactObservations; }
    public void setImpactObservations(List<String> impactObservations) { this.impactObservations = impactObservations; }

    public String getFeasibilityVerdict() { return feasibilityVerdict; }
    public void setFeasibilityVerdict(String feasibilityVerdict) { this.feasibilityVerdict = feasibilityVerdict; }
}

