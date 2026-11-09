package com.finpath.modules.lending.dto;

import java.math.BigDecimal;
import java.util.List;

public class LoanComparisonResponse {
    private BigDecimal requestedAmount;
    private int tenureMonths;
    private List<LoanComparisonItemDto> comparisonMatrix;
    private String lowestCostLender;
    private String lowestEmiLender;

    public LoanComparisonResponse() {}

    public LoanComparisonResponse(BigDecimal requestedAmount, int tenureMonths, List<LoanComparisonItemDto> comparisonMatrix, String lowestCostLender, String lowestEmiLender) {
        this.requestedAmount = requestedAmount;
        this.tenureMonths = tenureMonths;
        this.comparisonMatrix = comparisonMatrix;
        this.lowestCostLender = lowestCostLender;
        this.lowestEmiLender = lowestEmiLender;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(BigDecimal requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public int getTenureMonths() {
        return tenureMonths;
    }

    public void setTenureMonths(int tenureMonths) {
        this.tenureMonths = tenureMonths;
    }

    public List<LoanComparisonItemDto> getComparisonMatrix() {
        return comparisonMatrix;
    }

    public void setComparisonMatrix(List<LoanComparisonItemDto> comparisonMatrix) {
        this.comparisonMatrix = comparisonMatrix;
    }

    public String getLowestCostLender() {
        return lowestCostLender;
    }

    public void setLowestCostLender(String lowestCostLender) {
        this.lowestCostLender = lowestCostLender;
    }

    public String getLowestEmiLender() {
        return lowestEmiLender;
    }

    public void setLowestEmiLender(String lowestEmiLender) {
        this.lowestEmiLender = lowestEmiLender;
    }
}

