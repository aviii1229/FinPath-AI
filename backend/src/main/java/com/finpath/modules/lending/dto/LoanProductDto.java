package com.finpath.modules.lending.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class LoanProductDto {
    private UUID id;
    private String productName;
    private String loanType;
    private BigDecimal minPrincipal;
    private BigDecimal maxPrincipal;
    private BigDecimal minInterestRate;
    private BigDecimal maxInterestRate;
    private int minTermMonths;
    private int maxTermMonths;
    private int minCreditScore;
    private String lenderName;
    private BigDecimal processingFeePercentage;
    private String interestRateType;
    private String prepaymentTerms;
    private String features;
    private boolean isSimulated;

    public LoanProductDto() {}

    public LoanProductDto(UUID id, String productName, String loanType, BigDecimal minPrincipal, BigDecimal maxPrincipal, BigDecimal minInterestRate, BigDecimal maxInterestRate, int minTermMonths, int maxTermMonths, int minCreditScore) {
        this(id, productName, loanType, minPrincipal, maxPrincipal, minInterestRate, maxInterestRate, minTermMonths, maxTermMonths, minCreditScore, "FinPath Prime Lending", BigDecimal.valueOf(1.00), "FIXED", "Zero penalty after 6 months", null, true);
    }

    public LoanProductDto(UUID id, String productName, String loanType, BigDecimal minPrincipal, BigDecimal maxPrincipal, BigDecimal minInterestRate, BigDecimal maxInterestRate, int minTermMonths, int maxTermMonths, int minCreditScore, String lenderName, BigDecimal processingFeePercentage, String interestRateType, String prepaymentTerms, String features, boolean isSimulated) {
        this.id = id;
        this.productName = productName;
        this.loanType = loanType;
        this.minPrincipal = minPrincipal;
        this.maxPrincipal = maxPrincipal;
        this.minInterestRate = minInterestRate;
        this.maxInterestRate = maxInterestRate;
        this.minTermMonths = minTermMonths;
        this.maxTermMonths = maxTermMonths;
        this.minCreditScore = minCreditScore;
        this.lenderName = lenderName;
        this.processingFeePercentage = processingFeePercentage;
        this.interestRateType = interestRateType;
        this.prepaymentTerms = prepaymentTerms;
        this.features = features;
        this.isSimulated = isSimulated;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public BigDecimal getMinPrincipal() {
        return minPrincipal;
    }

    public void setMinPrincipal(BigDecimal minPrincipal) {
        this.minPrincipal = minPrincipal;
    }

    public BigDecimal getMaxPrincipal() {
        return maxPrincipal;
    }

    public void setMaxPrincipal(BigDecimal maxPrincipal) {
        this.maxPrincipal = maxPrincipal;
    }

    public BigDecimal getMinInterestRate() {
        return minInterestRate;
    }

    public void setMinInterestRate(BigDecimal minInterestRate) {
        this.minInterestRate = minInterestRate;
    }

    public BigDecimal getMaxInterestRate() {
        return maxInterestRate;
    }

    public void setMaxInterestRate(BigDecimal maxInterestRate) {
        this.maxInterestRate = maxInterestRate;
    }

    public int getMinTermMonths() {
        return minTermMonths;
    }

    public void setMinTermMonths(int minTermMonths) {
        this.minTermMonths = minTermMonths;
    }

    public int getMaxTermMonths() {
        return maxTermMonths;
    }

    public void setMaxTermMonths(int maxTermMonths) {
        this.maxTermMonths = maxTermMonths;
    }

    public int getMinCreditScore() {
        return minCreditScore;
    }

    public void setMinCreditScore(int minCreditScore) {
        this.minCreditScore = minCreditScore;
    }

    public String getLenderName() {
        return lenderName;
    }

    public void setLenderName(String lenderName) {
        this.lenderName = lenderName;
    }

    public BigDecimal getProcessingFeePercentage() {
        return processingFeePercentage;
    }

    public void setProcessingFeePercentage(BigDecimal processingFeePercentage) {
        this.processingFeePercentage = processingFeePercentage;
    }

    public String getInterestRateType() {
        return interestRateType;
    }

    public void setInterestRateType(String interestRateType) {
        this.interestRateType = interestRateType;
    }

    public String getPrepaymentTerms() {
        return prepaymentTerms;
    }

    public void setPrepaymentTerms(String prepaymentTerms) {
        this.prepaymentTerms = prepaymentTerms;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public boolean isSimulated() {
        return isSimulated;
    }

    public void setSimulated(boolean simulated) {
        isSimulated = simulated;
    }
}

