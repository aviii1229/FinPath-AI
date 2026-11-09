package com.finpath.modules.insurance.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public class QuoteCalculationRequest {

    @NotNull(message = "Product ID is required")
    private UUID productId;

    @NotNull(message = "Coverage amount is required")
    private BigDecimal coverageAmount;

    private boolean isSmoker = false;
    private boolean hasPreExistingConditions = false;

    public QuoteCalculationRequest() {}

    public QuoteCalculationRequest(UUID productId, BigDecimal coverageAmount, boolean isSmoker, boolean hasPreExistingConditions) {
        this.productId = productId;
        this.coverageAmount = coverageAmount;
        this.isSmoker = isSmoker;
        this.hasPreExistingConditions = hasPreExistingConditions;
    }

    public QuoteCalculationRequest(String productId, BigDecimal coverageAmount, boolean isSmoker, boolean hasPreExistingConditions) {
        this(UUID.fromString(productId), coverageAmount, isSmoker, hasPreExistingConditions);
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public BigDecimal getCoverageAmount() {
        return coverageAmount;
    }

    public void setCoverageAmount(BigDecimal coverageAmount) {
        this.coverageAmount = coverageAmount;
    }

    public boolean isSmoker() {
        return isSmoker;
    }

    public void setSmoker(boolean smoker) {
        isSmoker = smoker;
    }

    public boolean isHasPreExistingConditions() {
        return hasPreExistingConditions;
    }

    public void setHasPreExistingConditions(boolean hasPreExistingConditions) {
        this.hasPreExistingConditions = hasPreExistingConditions;
    }
}
