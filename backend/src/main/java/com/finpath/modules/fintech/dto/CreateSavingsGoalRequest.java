package com.finpath.modules.fintech.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateSavingsGoalRequest {

    @NotBlank(message = "Goal title is required")
    private String title;

    private String category = "EMERGENCY_FUND";

    @NotNull(message = "Target amount is required")
    @DecimalMin(value = "10.0", message = "Target amount must be at least 10")
    private BigDecimal targetAmount;

    private BigDecimal currentSavedAmount = BigDecimal.ZERO;

    @NotNull(message = "Target date is required")
    private LocalDate targetDate;

    public CreateSavingsGoalRequest() {}

    public CreateSavingsGoalRequest(String title, String category, BigDecimal targetAmount, BigDecimal currentSavedAmount, LocalDate targetDate) {
        this.title = title;
        this.category = category != null ? category : "EMERGENCY_FUND";
        this.targetAmount = targetAmount;
        this.currentSavedAmount = currentSavedAmount != null ? currentSavedAmount : BigDecimal.ZERO;
        this.targetDate = targetDate;
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
        return currentSavedAmount != null ? currentSavedAmount : BigDecimal.ZERO;
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
}

