package com.finpath.modules.lending.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class LoanRecommendationDto {
    private UUID productId;
    private String lenderName;
    private String productName;
    private String loanType;
    private BigDecimal interestRateApr;
    private String interestRateType;
    private BigDecimal monthlyEmi;
    private BigDecimal totalInterest;
    private BigDecimal processingFee;
    private int matchScorePercentage;
    private String matchReason;
    private List<String> highlights;
    private boolean isSimulated = true;

    public LoanRecommendationDto() {}

    public LoanRecommendationDto(UUID productId, String lenderName, String productName, String loanType, BigDecimal interestRateApr, String interestRateType, BigDecimal monthlyEmi, BigDecimal totalInterest, BigDecimal processingFee, int matchScorePercentage, String matchReason, List<String> highlights, boolean isSimulated) {
        this.productId = productId;
        this.lenderName = lenderName;
        this.productName = productName;
        this.loanType = loanType;
        this.interestRateApr = interestRateApr;
        this.interestRateType = interestRateType;
        this.monthlyEmi = monthlyEmi;
        this.totalInterest = totalInterest;
        this.processingFee = processingFee;
        this.matchScorePercentage = matchScorePercentage;
        this.matchReason = matchReason;
        this.highlights = highlights;
        this.isSimulated = isSimulated;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getLenderName() {
        return lenderName;
    }

    public void setLenderName(String lenderName) {
        this.lenderName = lenderName;
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

    public BigDecimal getInterestRateApr() {
        return interestRateApr;
    }

    public void setInterestRateApr(BigDecimal interestRateApr) {
        this.interestRateApr = interestRateApr;
    }

    public String getInterestRateType() {
        return interestRateType;
    }

    public void setInterestRateType(String interestRateType) {
        this.interestRateType = interestRateType;
    }

    public BigDecimal getMonthlyEmi() {
        return monthlyEmi;
    }

    public void setMonthlyEmi(BigDecimal monthlyEmi) {
        this.monthlyEmi = monthlyEmi;
    }

    public BigDecimal getTotalInterest() {
        return totalInterest;
    }

    public void setTotalInterest(BigDecimal totalInterest) {
        this.totalInterest = totalInterest;
    }

    public BigDecimal getProcessingFee() {
        return processingFee;
    }

    public void setProcessingFee(BigDecimal processingFee) {
        this.processingFee = processingFee;
    }

    public int getMatchScorePercentage() {
        return matchScorePercentage;
    }

    public void setMatchScorePercentage(int matchScorePercentage) {
        this.matchScorePercentage = matchScorePercentage;
    }

    public String getMatchReason() {
        return matchReason;
    }

    public void setMatchReason(String matchReason) {
        this.matchReason = matchReason;
    }

    public List<String> getHighlights() {
        return highlights;
    }

    public void setHighlights(List<String> highlights) {
        this.highlights = highlights;
    }

    public boolean isSimulated() {
        return isSimulated;
    }

    public void setSimulated(boolean simulated) {
        isSimulated = simulated;
    }
}

