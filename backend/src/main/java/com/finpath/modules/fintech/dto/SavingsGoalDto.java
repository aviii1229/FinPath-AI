package com.finpath.modules.fintech.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class SavingsGoalDto {

    private UUID id;
    private String title;
    private String category;
    private BigDecimal targetAmount;
    private BigDecimal currentSavedAmount;
    private LocalDate targetDate;
    private BigDecimal monthlyRequiredSavings;
    private BigDecimal progressPercentage;
    private String status;
    private long monthsRemaining;
    private Instant createdAt;
    private String affordabilityStatus = "AFFORDABLE"; // AFFORDABLE, STRETCH, DEFICIT

    public SavingsGoalDto() {}

    public SavingsGoalDto(UUID id, String title, String category, BigDecimal targetAmount, BigDecimal currentSavedAmount, LocalDate targetDate, BigDecimal monthlyRequiredSavings, BigDecimal progressPercentage, String status, long monthsRemaining, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.targetAmount = targetAmount;
        this.currentSavedAmount = currentSavedAmount;
        this.targetDate = targetDate;
        this.monthlyRequiredSavings = monthlyRequiredSavings;
        this.progressPercentage = progressPercentage;
        this.status = status;
        this.monthsRemaining = monthsRemaining;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public BigDecimal getCurrentSavedAmount() {
        return currentSavedAmount;
    }

    public void setCurrentSavedAmount(BigDecimal currentSavedAmount) {
        this.currentSavedAmount = currentSavedAmount;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public BigDecimal getMonthlyRequiredSavings() {
        return monthlyRequiredSavings;
    }

    public void setMonthlyRequiredSavings(BigDecimal monthlyRequiredSavings) {
        this.monthlyRequiredSavings = monthlyRequiredSavings;
    }

    public BigDecimal getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(BigDecimal progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getMonthsRemaining() {
        return monthsRemaining;
    }

    public void setMonthsRemaining(long monthsRemaining) {
        this.monthsRemaining = monthsRemaining;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getAffordabilityStatus() {
        return affordabilityStatus;
    }

    public void setAffordabilityStatus(String affordabilityStatus) {
        this.affordabilityStatus = affordabilityStatus;
    }
}

