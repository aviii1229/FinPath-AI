package com.finpath.modules.lending.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class LoanComparisonItemDto {
    private UUID productId;
    private String lenderName;
    private String productName;
    private String loanType;
    private BigDecimal interestRateApr;
    private String interestRateType;
    private BigDecimal processingFeePercentage;
    private BigDecimal processingFeeAmount;
    private BigDecimal monthlyEmi;
    private BigDecimal totalInterest;
    private BigDecimal totalBorrowingCost; // Principal + Total Interest + Processing Fee
    private int minTermMonths;
    private int maxTermMonths;
    private String prepaymentTerms;
    private List<String> features;
    private boolean isSimulated = true;

    public LoanComparisonItemDto() {}

    public LoanComparisonItemDto(UUID productId, String lenderName, String productName, String loanType, BigDecimal interestRateApr, String interestRateType, BigDecimal processingFeePercentage, BigDecimal processingFeeAmount, BigDecimal monthlyEmi, BigDecimal totalInterest, BigDecimal totalBorrowingCost, int minTermMonths, int maxTermMonths, String prepaymentTerms, List<String> features, boolean isSimulated) {
        this.productId = productId;
        this.lenderName = lenderName;
        this.productName = productName;
        this.loanType = loanType;
        this.interestRateApr = interestRateApr;
        this.interestRateType = interestRateType;
        this.processingFeePercentage = processingFeePercentage;
        this.processingFeeAmount = processingFeeAmount;
        this.monthlyEmi = monthlyEmi;
        this.totalInterest = totalInterest;
        this.totalBorrowingCost = totalBorrowingCost;
        this.minTermMonths = minTermMonths;
        this.maxTermMonths = maxTermMonths;
        this.prepaymentTerms = prepaymentTerms;
        this.features = features;
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

    public BigDecimal getProcessingFeePercentage() {
        return processingFeePercentage;
    }

    public void setProcessingFeePercentage(BigDecimal processingFeePercentage) {
        this.processingFeePercentage = processingFeePercentage;
    }

    public BigDecimal getProcessingFeeAmount() {
        return processingFeeAmount;
    }

    public void setProcessingFeeAmount(BigDecimal processingFeeAmount) {
        this.processingFeeAmount = processingFeeAmount;
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

    public BigDecimal getTotalBorrowingCost() {
        return totalBorrowingCost;
    }

    public void setTotalBorrowingCost(BigDecimal totalBorrowingCost) {
        this.totalBorrowingCost = totalBorrowingCost;
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

    public String getPrepaymentTerms() {
        return prepaymentTerms;
    }

    public void setPrepaymentTerms(String prepaymentTerms) {
        this.prepaymentTerms = prepaymentTerms;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public boolean isSimulated() {
        return isSimulated;
    }

    public void setSimulated(boolean simulated) {
        isSimulated = simulated;
    }
}

