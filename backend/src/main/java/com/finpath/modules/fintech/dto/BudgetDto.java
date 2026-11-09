package com.finpath.modules.fintech.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class BudgetDto {
    private UUID id;
    private String category;
    private BigDecimal monthlyLimit;
    private BigDecimal currentSpent;
    private int alertThresholdPercent;

    public BudgetDto() {}

    public BudgetDto(UUID id, String category, BigDecimal monthlyLimit, BigDecimal currentSpent, int alertThresholdPercent) {
        this.id = id;
        this.category = category;
        this.monthlyLimit = monthlyLimit;
        this.currentSpent = currentSpent;
        this.alertThresholdPercent = alertThresholdPercent;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getMonthlyLimit() {
        return monthlyLimit;
    }

    public void setMonthlyLimit(BigDecimal monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
    }

    public BigDecimal getCurrentSpent() {
        return currentSpent;
    }

    public void setCurrentSpent(BigDecimal currentSpent) {
        this.currentSpent = currentSpent;
    }

    public int getAlertThresholdPercent() {
        return alertThresholdPercent;
    }

    public void setAlertThresholdPercent(int alertThresholdPercent) {
        this.alertThresholdPercent = alertThresholdPercent;
    }
}

