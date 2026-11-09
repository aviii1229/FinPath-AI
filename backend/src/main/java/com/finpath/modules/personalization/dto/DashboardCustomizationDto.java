package com.finpath.modules.personalization.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DashboardCustomizationDto {
    private String primaryFocusArea;
    private BigDecimal calculatedMonthlySurplus;
    private BigDecimal debtToIncomeRatio; // percentage
    private BigDecimal savingsRatePercentage; // percentage
    private BigDecimal recommendedEmergencyFund;
    private int monthsOfEmergencyRunway;
    private List<String> recommendedFeatureTags = new ArrayList<>();
    private List<String> keyActionAlerts = new ArrayList<>();

    public DashboardCustomizationDto() {}

    public String getPrimaryFocusArea() {
        return primaryFocusArea;
    }

    public void setPrimaryFocusArea(String primaryFocusArea) {
        this.primaryFocusArea = primaryFocusArea;
    }

    public BigDecimal getCalculatedMonthlySurplus() {
        return calculatedMonthlySurplus;
    }

    public void setCalculatedMonthlySurplus(BigDecimal calculatedMonthlySurplus) {
        this.calculatedMonthlySurplus = calculatedMonthlySurplus;
    }

    public BigDecimal getDebtToIncomeRatio() {
        return debtToIncomeRatio;
    }

    public void setDebtToIncomeRatio(BigDecimal debtToIncomeRatio) {
        this.debtToIncomeRatio = debtToIncomeRatio;
    }

    public BigDecimal getSavingsRatePercentage() {
        return savingsRatePercentage;
    }

    public void setSavingsRatePercentage(BigDecimal savingsRatePercentage) {
        this.savingsRatePercentage = savingsRatePercentage;
    }

    public BigDecimal getRecommendedEmergencyFund() {
        return recommendedEmergencyFund;
    }

    public void setRecommendedEmergencyFund(BigDecimal recommendedEmergencyFund) {
        this.recommendedEmergencyFund = recommendedEmergencyFund;
    }

    public int getMonthsOfEmergencyRunway() {
        return monthsOfEmergencyRunway;
    }

    public void setMonthsOfEmergencyRunway(int monthsOfEmergencyRunway) {
        this.monthsOfEmergencyRunway = monthsOfEmergencyRunway;
    }

    public List<String> getRecommendedFeatureTags() {
        return recommendedFeatureTags;
    }

    public void setRecommendedFeatureTags(List<String> recommendedFeatureTags) {
        this.recommendedFeatureTags = recommendedFeatureTags;
    }

    public List<String> getKeyActionAlerts() {
        return keyActionAlerts;
    }

    public void setKeyActionAlerts(List<String> keyActionAlerts) {
        this.keyActionAlerts = keyActionAlerts;
    }
}

