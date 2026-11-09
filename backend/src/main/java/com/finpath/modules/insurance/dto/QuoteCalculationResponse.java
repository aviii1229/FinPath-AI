package com.finpath.modules.insurance.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class QuoteCalculationResponse {
    private UUID quoteId;
    private String carrierName;
    private String productName;
    private BigDecimal coverageSelected;
    private BigDecimal quotedPremiumMonthly;
    private BigDecimal deductible;
    private BigDecimal riskScore;
    private String status;
    private Instant expiresAt;

    public QuoteCalculationResponse() {}

    public QuoteCalculationResponse(UUID quoteId, String carrierName, String productName, BigDecimal coverageSelected, BigDecimal quotedPremiumMonthly, BigDecimal deductible, BigDecimal riskScore, String status, Instant expiresAt) {
        this.quoteId = quoteId;
        this.carrierName = carrierName;
        this.productName = productName;
        this.coverageSelected = coverageSelected;
        this.quotedPremiumMonthly = quotedPremiumMonthly;
        this.deductible = deductible;
        this.riskScore = riskScore;
        this.status = status;
        this.expiresAt = expiresAt;
    }

    public UUID getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(UUID quoteId) {
        this.quoteId = quoteId;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getCoverageSelected() {
        return coverageSelected;
    }

    public void setCoverageSelected(BigDecimal coverageSelected) {
        this.coverageSelected = coverageSelected;
    }

    public BigDecimal getQuotedPremiumMonthly() {
        return quotedPremiumMonthly;
    }

    public BigDecimal getMonthlyPremium() {
        return quotedPremiumMonthly;
    }

    public void setQuotedPremiumMonthly(BigDecimal quotedPremiumMonthly) {
        this.quotedPremiumMonthly = quotedPremiumMonthly;
    }

    public BigDecimal getDeductible() {
        return deductible;
    }

    public void setDeductible(BigDecimal deductible) {
        this.deductible = deductible;
    }

    public BigDecimal getRiskScore() {
        return riskScore;
    }

    public BigDecimal getRiskTier() {
        return riskScore;
    }

    public void setRiskScore(BigDecimal riskScore) {
        this.riskScore = riskScore;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }
}
