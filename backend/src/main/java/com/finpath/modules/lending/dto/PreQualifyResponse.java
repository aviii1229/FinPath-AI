package com.finpath.modules.lending.dto;

import java.math.BigDecimal;
import java.util.List;

public class PreQualifyResponse {
    private boolean preQualified;
    private BigDecimal maxEligibleAmount;
    private BigDecimal estimatedApr;
    private BigDecimal calculatedDti;
    private BigDecimal projectedMonthlyEmi;
    private List<String> recommendations;

    public PreQualifyResponse() {}

    public PreQualifyResponse(boolean preQualified, BigDecimal maxEligibleAmount, BigDecimal estimatedApr, BigDecimal calculatedDti, BigDecimal projectedMonthlyEmi, List<String> recommendations) {
        this.preQualified = preQualified;
        this.maxEligibleAmount = maxEligibleAmount;
        this.estimatedApr = estimatedApr;
        this.calculatedDti = calculatedDti;
        this.projectedMonthlyEmi = projectedMonthlyEmi;
        this.recommendations = recommendations;
    }

    public boolean isPreQualified() {
        return preQualified;
    }

    public boolean isApproved() {
        return preQualified;
    }

    public void setPreQualified(boolean preQualified) {
        this.preQualified = preQualified;
    }

    public BigDecimal getMaxEligibleAmount() {
        return maxEligibleAmount;
    }

    public void setMaxEligibleAmount(BigDecimal maxEligibleAmount) {
        this.maxEligibleAmount = maxEligibleAmount;
    }

    public BigDecimal getEstimatedApr() {
        return estimatedApr;
    }

    public void setEstimatedApr(BigDecimal estimatedApr) {
        this.estimatedApr = estimatedApr;
    }

    public BigDecimal getCalculatedDti() {
        return calculatedDti;
    }

    public BigDecimal getDtiPercentage() {
        return calculatedDti;
    }

    public void setCalculatedDti(BigDecimal calculatedDti) {
        this.calculatedDti = calculatedDti;
    }

    public BigDecimal getProjectedMonthlyEmi() {
        return projectedMonthlyEmi;
    }

    public BigDecimal getEstimatedMonthlyPayment() {
        return projectedMonthlyEmi;
    }

    public void setProjectedMonthlyEmi(BigDecimal projectedMonthlyEmi) {
        this.projectedMonthlyEmi = projectedMonthlyEmi;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }
}
