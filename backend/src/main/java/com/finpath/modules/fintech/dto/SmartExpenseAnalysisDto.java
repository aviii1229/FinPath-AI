package com.finpath.modules.fintech.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SmartExpenseAnalysisDto {

    private List<RecurringSubscriptionDto> subscriptions = new ArrayList<>();
    private BigDecimal monthlySubscriptionsTotal = BigDecimal.ZERO;
    private List<UnusualExpenseDto> unusualExpenses = new ArrayList<>();
    private List<SpendingIncreaseDto> categoryIncreases = new ArrayList<>();
    private List<DuplicateSuspectDto> duplicateSuspects = new ArrayList<>();

    public SmartExpenseAnalysisDto() {}

    public SmartExpenseAnalysisDto(List<RecurringSubscriptionDto> subscriptions,
                                   BigDecimal monthlySubscriptionsTotal,
                                   List<UnusualExpenseDto> unusualExpenses,
                                   List<SpendingIncreaseDto> categoryIncreases,
                                   List<DuplicateSuspectDto> duplicateSuspects) {
        this.subscriptions = subscriptions != null ? subscriptions : new ArrayList<>();
        this.monthlySubscriptionsTotal = monthlySubscriptionsTotal != null ? monthlySubscriptionsTotal : BigDecimal.ZERO;
        this.unusualExpenses = unusualExpenses != null ? unusualExpenses : new ArrayList<>();
        this.categoryIncreases = categoryIncreases != null ? categoryIncreases : new ArrayList<>();
        this.duplicateSuspects = duplicateSuspects != null ? duplicateSuspects : new ArrayList<>();
    }

    public static class RecurringSubscriptionDto {
        private String merchantName;
        private String category;
        private BigDecimal monthlyAmount;
        private LocalDate lastChargedDate;
        private String frequency; // MONTHLY, ANNUAL
        private String utilityLevel; // ESSENTIAL, DISCRETIONARY, UNDERUTILIZED

        public RecurringSubscriptionDto() {}

        public RecurringSubscriptionDto(String merchantName, String category, BigDecimal monthlyAmount, LocalDate lastChargedDate, String frequency, String utilityLevel) {
            this.merchantName = merchantName;
            this.category = category;
            this.monthlyAmount = monthlyAmount;
            this.lastChargedDate = lastChargedDate;
            this.frequency = frequency;
            this.utilityLevel = utilityLevel;
        }

        public String getMerchantName() { return merchantName; }
        public void setMerchantName(String merchantName) { this.merchantName = merchantName; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public BigDecimal getMonthlyAmount() { return monthlyAmount; }
        public void setMonthlyAmount(BigDecimal monthlyAmount) { this.monthlyAmount = monthlyAmount; }

        public LocalDate getLastChargedDate() { return lastChargedDate; }
        public void setLastChargedDate(LocalDate lastChargedDate) { this.lastChargedDate = lastChargedDate; }

        public String getFrequency() { return frequency; }
        public void setFrequency(String frequency) { this.frequency = frequency; }

        public String getUtilityLevel() { return utilityLevel; }
        public void setUtilityLevel(String utilityLevel) { this.utilityLevel = utilityLevel; }
    }

    public static class UnusualExpenseDto {
        private UUID transactionId;
        private String merchantName;
        private String category;
        private BigDecimal amount;
        private LocalDate date;
        private BigDecimal categoryAverage;
        private String reason;

        public UnusualExpenseDto() {}

        public UnusualExpenseDto(UUID transactionId, String merchantName, String category, BigDecimal amount, LocalDate date, BigDecimal categoryAverage, String reason) {
            this.transactionId = transactionId;
            this.merchantName = merchantName;
            this.category = category;
            this.amount = amount;
            this.date = date;
            this.categoryAverage = categoryAverage;
            this.reason = reason;
        }

        public UUID getTransactionId() { return transactionId; }
        public void setTransactionId(UUID transactionId) { this.transactionId = transactionId; }

        public String getMerchantName() { return merchantName; }
        public void setMerchantName(String merchantName) { this.merchantName = merchantName; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }

        public BigDecimal getCategoryAverage() { return categoryAverage; }
        public void setCategoryAverage(BigDecimal categoryAverage) { this.categoryAverage = categoryAverage; }

        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    public static class SpendingIncreaseDto {
        private String category;
        private BigDecimal currentMonthSpent;
        private BigDecimal previousMonthSpent;
        private BigDecimal percentageIncrease;
        private String insightText;

        public SpendingIncreaseDto() {}

        public SpendingIncreaseDto(String category, BigDecimal currentMonthSpent, BigDecimal previousMonthSpent, BigDecimal percentageIncrease, String insightText) {
            this.category = category;
            this.currentMonthSpent = currentMonthSpent;
            this.previousMonthSpent = previousMonthSpent;
            this.percentageIncrease = percentageIncrease;
            this.insightText = insightText;
        }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public BigDecimal getCurrentMonthSpent() { return currentMonthSpent; }
        public void setCurrentMonthSpent(BigDecimal currentMonthSpent) { this.currentMonthSpent = currentMonthSpent; }

        public BigDecimal getPreviousMonthSpent() { return previousMonthSpent; }
        public void setPreviousMonthSpent(BigDecimal previousMonthSpent) { this.previousMonthSpent = previousMonthSpent; }

        public BigDecimal getPercentageIncrease() { return percentageIncrease; }
        public void setPercentageIncrease(BigDecimal percentageIncrease) { this.percentageIncrease = percentageIncrease; }

        public String getInsightText() { return insightText; }
        public void setInsightText(String insightText) { this.insightText = insightText; }
    }

    public static class DuplicateSuspectDto {
        private UUID suspectId;
        private UUID primaryTransactionId;
        private UUID duplicateTransactionId;
        private String merchantName;
        private BigDecimal amount;
        private LocalDate primaryDate;
        private LocalDate duplicateDate;
        private String status; // PENDING_CONFIRMATION, CONFIRMED_DUPLICATE, DISMISSED
        private String explanation;

        public DuplicateSuspectDto() {}

        public DuplicateSuspectDto(UUID suspectId, UUID primaryTransactionId, UUID duplicateTransactionId, String merchantName, BigDecimal amount, LocalDate primaryDate, LocalDate duplicateDate, String status, String explanation) {
            this.suspectId = suspectId;
            this.primaryTransactionId = primaryTransactionId;
            this.duplicateTransactionId = duplicateTransactionId;
            this.merchantName = merchantName;
            this.amount = amount;
            this.primaryDate = primaryDate;
            this.duplicateDate = duplicateDate;
            this.status = status;
            this.explanation = explanation;
        }

        public UUID getSuspectId() { return suspectId; }
        public void setSuspectId(UUID suspectId) { this.suspectId = suspectId; }

        public UUID getPrimaryTransactionId() { return primaryTransactionId; }
        public void setPrimaryTransactionId(UUID primaryTransactionId) { this.primaryTransactionId = primaryTransactionId; }

        public UUID getDuplicateTransactionId() { return duplicateTransactionId; }
        public void setDuplicateTransactionId(UUID duplicateTransactionId) { this.duplicateTransactionId = duplicateTransactionId; }

        public String getMerchantName() { return merchantName; }
        public void setMerchantName(String merchantName) { this.merchantName = merchantName; }

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

        public LocalDate getPrimaryDate() { return primaryDate; }
        public void setPrimaryDate(LocalDate primaryDate) { this.primaryDate = primaryDate; }

        public LocalDate getDuplicateDate() { return duplicateDate; }
        public void setDuplicateDate(LocalDate duplicateDate) { this.duplicateDate = duplicateDate; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getExplanation() { return explanation; }
        public void setExplanation(String explanation) { this.explanation = explanation; }
    }

    public List<RecurringSubscriptionDto> getSubscriptions() { return subscriptions; }
    public void setSubscriptions(List<RecurringSubscriptionDto> subscriptions) { this.subscriptions = subscriptions; }

    public BigDecimal getMonthlySubscriptionsTotal() { return monthlySubscriptionsTotal; }
    public void setMonthlySubscriptionsTotal(BigDecimal monthlySubscriptionsTotal) { this.monthlySubscriptionsTotal = monthlySubscriptionsTotal; }

    public List<UnusualExpenseDto> getUnusualExpenses() { return unusualExpenses; }
    public void setUnusualExpenses(List<UnusualExpenseDto> unusualExpenses) { this.unusualExpenses = unusualExpenses; }

    public List<SpendingIncreaseDto> getCategoryIncreases() { return categoryIncreases; }
    public void setCategoryIncreases(List<SpendingIncreaseDto> categoryIncreases) { this.categoryIncreases = categoryIncreases; }

    public List<DuplicateSuspectDto> getDuplicateSuspects() { return duplicateSuspects; }
    public void setDuplicateSuspects(List<DuplicateSuspectDto> duplicateSuspects) { this.duplicateSuspects = duplicateSuspects; }
}

