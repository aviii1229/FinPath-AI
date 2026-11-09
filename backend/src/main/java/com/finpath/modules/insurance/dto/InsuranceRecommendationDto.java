package com.finpath.modules.insurance.dto;

import java.math.BigDecimal;

public class InsuranceRecommendationDto {

    private InsuranceProductDto product;
    private int matchScorePercentage;
    private String matchReason;
    private String priorityBenefit;
    private BigDecimal estimatedMonthlyPremium;
    private boolean simulated = true;

    public InsuranceRecommendationDto() {}

    public InsuranceRecommendationDto(InsuranceProductDto product, int matchScorePercentage, String matchReason, String priorityBenefit, BigDecimal estimatedMonthlyPremium, boolean simulated) {
        this.product = product;
        this.matchScorePercentage = matchScorePercentage;
        this.matchReason = matchReason;
        this.priorityBenefit = priorityBenefit;
        this.estimatedMonthlyPremium = estimatedMonthlyPremium;
        this.simulated = simulated;
    }

    public InsuranceProductDto getProduct() {
        return product;
    }

    public void setProduct(InsuranceProductDto product) {
        this.product = product;
    }

    public int getMatchScorePercentage() {
        return matchScorePercentage;
    }

    public void setMatchScorePercentage(int matchScorePercentage) {
        this.matchScorePercentage = matchScorePercentage;
    }

    public String getMatchReason() {
        return matchReason;
    }

    public void setMatchReason(String matchReason) {
        this.matchReason = matchReason;
    }

    public String getPriorityBenefit() {
        return priorityBenefit;
    }

    public void setPriorityBenefit(String priorityBenefit) {
        this.priorityBenefit = priorityBenefit;
    }

    public BigDecimal getEstimatedMonthlyPremium() {
        return estimatedMonthlyPremium;
    }

    public void setEstimatedMonthlyPremium(BigDecimal estimatedMonthlyPremium) {
        this.estimatedMonthlyPremium = estimatedMonthlyPremium;
    }

    public boolean isSimulated() {
        return simulated;
    }

    public void setSimulated(boolean simulated) {
        this.simulated = simulated;
    }
}

