package com.finpath.modules.insurance.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public class InsuranceDiscoveryRequest {

    private Integer age = 32;

    @NotNull(message = "Annual income is required")
    private BigDecimal annualIncome;

    private BigDecimal monthlyBudget = BigDecimal.valueOf(300.00);

    private Integer dependents = 0;

    private List<String> coverageNeeds; // HEALTH, TERM_LIFE, AUTO, HOMEOWNERS

    private String primaryPriority = "BALANCED"; // LOW_PREMIUM, MAX_COVERAGE, LOW_DEDUCTIBLE, BALANCED

    private boolean isSmoker = false;

    private boolean hasPreExistingConditions = false;

    public InsuranceDiscoveryRequest() {}

    public InsuranceDiscoveryRequest(Integer age, BigDecimal annualIncome, BigDecimal monthlyBudget, Integer dependents, List<String> coverageNeeds, String primaryPriority, boolean isSmoker, boolean hasPreExistingConditions) {
        this.age = age;
        this.annualIncome = annualIncome;
        this.monthlyBudget = monthlyBudget;
        this.dependents = dependents;
        this.coverageNeeds = coverageNeeds;
        this.primaryPriority = primaryPriority;
        this.isSmoker = isSmoker;
        this.hasPreExistingConditions = hasPreExistingConditions;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public BigDecimal getAnnualIncome() {
        return annualIncome;
    }

    public void setAnnualIncome(BigDecimal annualIncome) {
        this.annualIncome = annualIncome;
    }

    public BigDecimal getMonthlyBudget() {
        return monthlyBudget;
    }

    public void setMonthlyBudget(BigDecimal monthlyBudget) {
        this.monthlyBudget = monthlyBudget;
    }

    public Integer getDependents() {
        return dependents;
    }

    public void setDependents(Integer dependents) {
        this.dependents = dependents;
    }

    public List<String> getCoverageNeeds() {
        return coverageNeeds;
    }

    public void setCoverageNeeds(List<String> coverageNeeds) {
        this.coverageNeeds = coverageNeeds;
    }

    public String getPrimaryPriority() {
        return primaryPriority;
    }

    public void setPrimaryPriority(String primaryPriority) {
        this.primaryPriority = primaryPriority;
    }

    public boolean isSmoker() {
        return isSmoker;
    }

    public void setSmoker(boolean smoker) {
        isSmoker = smoker;
    }

    public boolean isHasPreExistingConditions() {
        return hasPreExistingConditions;
    }

    public void setHasPreExistingConditions(boolean hasPreExistingConditions) {
        this.hasPreExistingConditions = hasPreExistingConditions;
    }
}

