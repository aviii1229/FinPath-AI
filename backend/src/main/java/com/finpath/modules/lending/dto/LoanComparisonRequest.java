package com.finpath.modules.lending.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class LoanComparisonRequest {
    private List<UUID> productIds;
    private BigDecimal requestedAmount;
    private Integer tenureMonths;

    public LoanComparisonRequest() {}

    public LoanComparisonRequest(List<UUID> productIds, BigDecimal requestedAmount, Integer tenureMonths) {
        this.productIds = productIds;
        this.requestedAmount = requestedAmount != null ? requestedAmount : BigDecimal.valueOf(25000);
        this.tenureMonths = tenureMonths != null ? tenureMonths : 36;
    }

    public List<UUID> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<UUID> productIds) {
        this.productIds = productIds;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount != null ? requestedAmount : BigDecimal.valueOf(25000);
    }

    public void setRequestedAmount(BigDecimal requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public Integer getTenureMonths() {
        return tenureMonths != null ? tenureMonths : 36;
    }

    public void setTenureMonths(Integer tenureMonths) {
        this.tenureMonths = tenureMonths;
    }
}

