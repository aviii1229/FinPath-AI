package com.finpath.modules.fintech.dto;

import java.math.BigDecimal;

public class CategorySpendingDto {
    private String category;
    private BigDecimal amount;
    private BigDecimal percentage;
    private int transactionCount;

    public CategorySpendingDto() {}

    public CategorySpendingDto(String category, BigDecimal amount, BigDecimal percentage, int transactionCount) {
        this.category = category;
        this.amount = amount;
        this.percentage = percentage;
        this.transactionCount = transactionCount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(int transactionCount) {
        this.transactionCount = transactionCount;
    }
}

