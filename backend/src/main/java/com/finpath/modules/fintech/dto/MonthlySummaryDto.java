package com.finpath.modules.fintech.dto;

import java.math.BigDecimal;
import java.util.List;

public class MonthlySummaryDto {
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netSavings;
    private BigDecimal savingsRate;
    private BigDecimal previousMonthExpenses;
    private BigDecimal expenseChangePercentage;
    private List<CategorySpendingDto> categoryDistribution;

    public MonthlySummaryDto() {}

    public MonthlySummaryDto(BigDecimal totalIncome, BigDecimal totalExpenses, BigDecimal netSavings, BigDecimal savingsRate, BigDecimal previousMonthExpenses, BigDecimal expenseChangePercentage, List<CategorySpendingDto> categoryDistribution) {
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.netSavings = netSavings;
        this.savingsRate = savingsRate;
        this.previousMonthExpenses = previousMonthExpenses;
        this.expenseChangePercentage = expenseChangePercentage;
        this.categoryDistribution = categoryDistribution;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public BigDecimal getNetSavings() {
        return netSavings;
    }

    public void setNetSavings(BigDecimal netSavings) {
        this.netSavings = netSavings;
    }

    public BigDecimal getSavingsRate() {
        return savingsRate;
    }

    public void setSavingsRate(BigDecimal savingsRate) {
        this.savingsRate = savingsRate;
    }

    public BigDecimal getPreviousMonthExpenses() {
        return previousMonthExpenses;
    }

    public void setPreviousMonthExpenses(BigDecimal previousMonthExpenses) {
        this.previousMonthExpenses = previousMonthExpenses;
    }

    public BigDecimal getExpenseChangePercentage() {
        return expenseChangePercentage;
    }

    public void setExpenseChangePercentage(BigDecimal expenseChangePercentage) {
        this.expenseChangePercentage = expenseChangePercentage;
    }

    public List<CategorySpendingDto> getCategoryDistribution() {
        return categoryDistribution;
    }

    public void setCategoryDistribution(List<CategorySpendingDto> categoryDistribution) {
        this.categoryDistribution = categoryDistribution;
    }
}

