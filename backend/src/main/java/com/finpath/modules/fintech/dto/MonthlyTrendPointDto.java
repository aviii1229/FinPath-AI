package com.finpath.modules.fintech.dto;

import java.math.BigDecimal;

public class MonthlyTrendPointDto {
    private String month;
    private BigDecimal income;
    private BigDecimal expenses;
    private BigDecimal savings;
    private BigDecimal savingsRate;

    public MonthlyTrendPointDto() {}

    public MonthlyTrendPointDto(String month, BigDecimal income, BigDecimal expenses, BigDecimal savings, BigDecimal savingsRate) {
        this.month = month;
        this.income = income;
        this.expenses = expenses;
        this.savings = savings;
        this.savingsRate = savingsRate;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public BigDecimal getExpenses() {
        return expenses;
    }

    public void setExpenses(BigDecimal expenses) {
        this.expenses = expenses;
    }

    public BigDecimal getSavings() {
        return savings;
    }

    public void setSavings(BigDecimal savings) {
        this.savings = savings;
    }

    public BigDecimal getSavingsRate() {
        return savingsRate;
    }

    public void setSavingsRate(BigDecimal savingsRate) {
        this.savingsRate = savingsRate;
    }
}

