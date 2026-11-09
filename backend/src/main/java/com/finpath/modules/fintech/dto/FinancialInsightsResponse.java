package com.finpath.modules.fintech.dto;

import java.util.List;

public class FinancialInsightsResponse {
    private MonthlySummaryDto monthlySummary;
    private List<FinancialInsightDto> insights;
    private List<CategorySpendingDto> unusualSpikes;
    private List<MonthlyTrendPointDto> historicalMonthlyTrends;

    public FinancialInsightsResponse() {}

    public FinancialInsightsResponse(MonthlySummaryDto monthlySummary, List<FinancialInsightDto> insights, List<CategorySpendingDto> unusualSpikes, List<MonthlyTrendPointDto> historicalMonthlyTrends) {
        this.monthlySummary = monthlySummary;
        this.insights = insights;
        this.unusualSpikes = unusualSpikes;
        this.historicalMonthlyTrends = historicalMonthlyTrends;
    }

    public MonthlySummaryDto getMonthlySummary() {
        return monthlySummary;
    }

    public void setMonthlySummary(MonthlySummaryDto monthlySummary) {
        this.monthlySummary = monthlySummary;
    }

    public List<FinancialInsightDto> getInsights() {
        return insights;
    }

    public void setInsights(List<FinancialInsightDto> insights) {
        this.insights = insights;
    }

    public List<CategorySpendingDto> getUnusualSpikes() {
        return unusualSpikes;
    }

    public void setUnusualSpikes(List<CategorySpendingDto> unusualSpikes) {
        this.unusualSpikes = unusualSpikes;
    }

    public List<MonthlyTrendPointDto> getHistoricalMonthlyTrends() {
        return historicalMonthlyTrends;
    }

    public void setHistoricalMonthlyTrends(List<MonthlyTrendPointDto> historicalMonthlyTrends) {
        this.historicalMonthlyTrends = historicalMonthlyTrends;
    }
}

