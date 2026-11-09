package com.finpath.modules.fintech.dto;

import java.math.BigDecimal;

public class FinancialInsightDto {
    private String type; // ANOMALY, OPPORTUNITY, ACHIEVEMENT, WARNING
    private String title;
    private String description;
    private BigDecimal potentialMonthlySavings;
    private String impactLevel; // HIGH, MEDIUM, LOW
    private String actionRecommendation;

    public FinancialInsightDto() {}

    public FinancialInsightDto(String type, String title, String description, BigDecimal potentialMonthlySavings, String impactLevel, String actionRecommendation) {
        this.type = type;
        this.title = title;
        this.description = description;
        this.potentialMonthlySavings = potentialMonthlySavings;
        this.impactLevel = impactLevel;
        this.actionRecommendation = actionRecommendation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPotentialMonthlySavings() {
        return potentialMonthlySavings;
    }

    public void setPotentialMonthlySavings(BigDecimal potentialMonthlySavings) {
        this.potentialMonthlySavings = potentialMonthlySavings;
    }

    public String getImpactLevel() {
        return impactLevel;
    }

    public void setImpactLevel(String impactLevel) {
        this.impactLevel = impactLevel;
    }

    public String getActionRecommendation() {
        return actionRecommendation;
    }

    public void setActionRecommendation(String actionRecommendation) {
        this.actionRecommendation = actionRecommendation;
    }
}

