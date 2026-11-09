package com.finpath.modules.fintech.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CashflowForecastDto {
    private BigDecimal currentLiquidCash;
    private BigDecimal projectedCashIn30Days;
    private BigDecimal expectedInflows30Days;
    private BigDecimal expectedOutflows30Days;
    private BigDecimal burnRateDailyAverage;
    private String liquidityHealth;
    private List<ForecastPointDto> forecastPoints = new ArrayList<>();

    // Enhanced Phase 04 additions
    private List<RecurringBillItemDto> recurringBills = new ArrayList<>();
    private BigDecimal recurringBillsTotal = BigDecimal.ZERO;
    private boolean potentialShortageDetected = false;
    private String shortageRiskLevel = "LOW"; // LOW, MODERATE, ELEVATED
    private BigDecimal lowestProjectedBalance = BigDecimal.ZERO;
    private List<String> forecastAssumptions = new ArrayList<>();
    private String uncertaintyNotes = "Forecast models cashflow bounds based on 60-day historical standard deviations (95% confidence interval).";
    private String disclaimer = "Cash flow projections are simulated approximations for liquidity planning and do not guarantee future account balances or prevent bank overdrafts.";

    public CashflowForecastDto() {}

    public CashflowForecastDto(BigDecimal currentLiquidCash, BigDecimal projectedCashIn30Days, BigDecimal expectedInflows30Days, BigDecimal expectedOutflows30Days, BigDecimal burnRateDailyAverage, String liquidityHealth, List<ForecastPointDto> forecastPoints) {
        this.currentLiquidCash = currentLiquidCash;
        this.projectedCashIn30Days = projectedCashIn30Days;
        this.expectedInflows30Days = expectedInflows30Days;
        this.expectedOutflows30Days = expectedOutflows30Days;
        this.burnRateDailyAverage = burnRateDailyAverage;
        this.liquidityHealth = liquidityHealth;
        this.forecastPoints = forecastPoints != null ? forecastPoints : new ArrayList<>();
    }

    public static class ForecastPointDto {
        private LocalDate date;
        private BigDecimal projectedBalance;
        private BigDecimal confidenceLow;
        private BigDecimal confidenceHigh;

        public ForecastPointDto() {}

        public ForecastPointDto(LocalDate date, BigDecimal projectedBalance, BigDecimal confidenceLow, BigDecimal confidenceHigh) {
            this.date = date;
            this.projectedBalance = projectedBalance;
            this.confidenceLow = confidenceLow;
            this.confidenceHigh = confidenceHigh;
        }

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }

        public BigDecimal getProjectedBalance() { return projectedBalance; }
        public void setProjectedBalance(BigDecimal projectedBalance) { this.projectedBalance = projectedBalance; }

        public BigDecimal getConfidenceLow() { return confidenceLow; }
        public void setConfidenceLow(BigDecimal confidenceLow) { this.confidenceLow = confidenceLow; }

        public BigDecimal getConfidenceHigh() { return confidenceHigh; }
        public void setConfidenceHigh(BigDecimal confidenceHigh) { this.confidenceHigh = confidenceHigh; }
    }

    public static class RecurringBillItemDto {
        private String billName;
        private String category;
        private BigDecimal amount;
        private int dueDayOfMonth;
        private String status; // UPCOMING, PAID

        public RecurringBillItemDto() {}

        public RecurringBillItemDto(String billName, String category, BigDecimal amount, int dueDayOfMonth, String status) {
            this.billName = billName;
            this.category = category;
            this.amount = amount;
            this.dueDayOfMonth = dueDayOfMonth;
            this.status = status;
        }

        public String getBillName() { return billName; }
        public void setBillName(String billName) { this.billName = billName; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

        public int getDueDayOfMonth() { return dueDayOfMonth; }
        public void setDueDayOfMonth(int dueDayOfMonth) { this.dueDayOfMonth = dueDayOfMonth; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public BigDecimal getCurrentLiquidCash() { return currentLiquidCash; }
    public void setCurrentLiquidCash(BigDecimal currentLiquidCash) { this.currentLiquidCash = currentLiquidCash; }

    public BigDecimal getProjectedCashIn30Days() { return projectedCashIn30Days; }
    public void setProjectedCashIn30Days(BigDecimal projectedCashIn30Days) { this.projectedCashIn30Days = projectedCashIn30Days; }

    public BigDecimal getProjectedNetSavings() {
        return (expectedInflows30Days != null && expectedOutflows30Days != null)
                ? expectedInflows30Days.subtract(expectedOutflows30Days)
                : BigDecimal.ZERO;
    }

    public BigDecimal getExpectedInflows30Days() { return expectedInflows30Days; }
    public void setExpectedInflows30Days(BigDecimal expectedInflows30Days) { this.expectedInflows30Days = expectedInflows30Days; }

    public BigDecimal getExpectedOutflows30Days() { return expectedOutflows30Days; }
    public void setExpectedOutflows30Days(BigDecimal expectedOutflows30Days) { this.expectedOutflows30Days = expectedOutflows30Days; }

    public BigDecimal getBurnRateDailyAverage() { return burnRateDailyAverage; }
    public void setBurnRateDailyAverage(BigDecimal burnRateDailyAverage) { this.burnRateDailyAverage = burnRateDailyAverage; }

    public String getLiquidityHealth() { return liquidityHealth; }
    public void setLiquidityHealth(String liquidityHealth) { this.liquidityHealth = liquidityHealth; }

    public List<ForecastPointDto> getForecastPoints() { return forecastPoints; }
    public void setForecastPoints(List<ForecastPointDto> forecastPoints) { this.forecastPoints = forecastPoints; }

    public List<RecurringBillItemDto> getRecurringBills() { return recurringBills; }
    public void setRecurringBills(List<RecurringBillItemDto> recurringBills) { this.recurringBills = recurringBills; }

    public BigDecimal getRecurringBillsTotal() { return recurringBillsTotal; }
    public void setRecurringBillsTotal(BigDecimal recurringBillsTotal) { this.recurringBillsTotal = recurringBillsTotal; }

    public boolean isPotentialShortageDetected() { return potentialShortageDetected; }
    public void setPotentialShortageDetected(boolean potentialShortageDetected) { this.potentialShortageDetected = potentialShortageDetected; }

    public String getShortageRiskLevel() { return shortageRiskLevel; }
    public void setShortageRiskLevel(String shortageRiskLevel) { this.shortageRiskLevel = shortageRiskLevel; }

    public BigDecimal getLowestProjectedBalance() { return lowestProjectedBalance; }
    public void setLowestProjectedBalance(BigDecimal lowestProjectedBalance) { this.lowestProjectedBalance = lowestProjectedBalance; }

    public List<String> getForecastAssumptions() { return forecastAssumptions; }
    public void setForecastAssumptions(List<String> forecastAssumptions) { this.forecastAssumptions = forecastAssumptions; }

    public String getUncertaintyNotes() { return uncertaintyNotes; }
    public void setUncertaintyNotes(String uncertaintyNotes) { this.uncertaintyNotes = uncertaintyNotes; }

    public String getDisclaimer() { return disclaimer; }
    public void setDisclaimer(String disclaimer) { this.disclaimer = disclaimer; }
}
