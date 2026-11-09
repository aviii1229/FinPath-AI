package com.finpath.modules.insurance.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class InsuranceProductDto {
    private UUID id;
    private String carrierName;
    private String productName;
    private String category;
    private BigDecimal basePremiumMonthly;
    private BigDecimal coverageAmountMin;
    private BigDecimal coverageAmountMax;
    private String termsSummary;
    private Integer waitingPeriodDays = 0;
    private String benefits;
    private String exclusions;
    private String deductibleOptions = "$500 / $1,000 / $2,500";
    private BigDecimal copayPercentage = BigDecimal.valueOf(10.00);
    private boolean simulated = true;

    public InsuranceProductDto() {}

    public InsuranceProductDto(UUID id, String carrierName, String productName, String category, BigDecimal basePremiumMonthly, BigDecimal coverageAmountMin, BigDecimal coverageAmountMax, String termsSummary) {
        this.id = id;
        this.carrierName = carrierName;
        this.productName = productName;
        this.category = category;
        this.basePremiumMonthly = basePremiumMonthly;
        this.coverageAmountMin = coverageAmountMin;
        this.coverageAmountMax = coverageAmountMax;
        this.termsSummary = termsSummary;
    }

    public InsuranceProductDto(UUID id, String carrierName, String productName, String category, BigDecimal basePremiumMonthly, BigDecimal coverageAmountMin, BigDecimal coverageAmountMax, String termsSummary, Integer waitingPeriodDays, String benefits, String exclusions, String deductibleOptions, BigDecimal copayPercentage, boolean simulated) {
        this(id, carrierName, productName, category, basePremiumMonthly, coverageAmountMin, coverageAmountMax, termsSummary);
        this.waitingPeriodDays = waitingPeriodDays;
        this.benefits = benefits;
        this.exclusions = exclusions;
        this.deductibleOptions = deductibleOptions;
        this.copayPercentage = copayPercentage;
        this.simulated = simulated;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getBasePremiumMonthly() {
        return basePremiumMonthly;
    }

    public void setBasePremiumMonthly(BigDecimal basePremiumMonthly) {
        this.basePremiumMonthly = basePremiumMonthly;
    }

    public BigDecimal getCoverageAmountMin() {
        return coverageAmountMin;
    }

    public void setCoverageAmountMin(BigDecimal coverageAmountMin) {
        this.coverageAmountMin = coverageAmountMin;
    }

    public BigDecimal getCoverageAmountMax() {
        return coverageAmountMax;
    }

    public void setCoverageAmountMax(BigDecimal coverageAmountMax) {
        this.coverageAmountMax = coverageAmountMax;
    }

    public String getTermsSummary() {
        return termsSummary;
    }

    public void setTermsSummary(String termsSummary) {
        this.termsSummary = termsSummary;
    }

    public Integer getWaitingPeriodDays() {
        return waitingPeriodDays;
    }

    public void setWaitingPeriodDays(Integer waitingPeriodDays) {
        this.waitingPeriodDays = waitingPeriodDays;
    }

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public String getExclusions() {
        return exclusions;
    }

    public void setExclusions(String exclusions) {
        this.exclusions = exclusions;
    }

    public String getDeductibleOptions() {
        return deductibleOptions;
    }

    public void setDeductibleOptions(String deductibleOptions) {
        this.deductibleOptions = deductibleOptions;
    }

    public BigDecimal getCopayPercentage() {
        return copayPercentage;
    }

    public void setCopayPercentage(BigDecimal copayPercentage) {
        this.copayPercentage = copayPercentage;
    }

    public boolean isSimulated() {
        return simulated;
    }

    public void setSimulated(boolean simulated) {
        this.simulated = simulated;
    }
}
