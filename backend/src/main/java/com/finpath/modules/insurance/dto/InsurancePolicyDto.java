package com.finpath.modules.insurance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class InsurancePolicyDto {
    private UUID id;
    private String policyNumber;
    private String carrierName;
    private String category;
    private String productName;
    private BigDecimal coverageAmount;
    private BigDecimal monthlyPremium;
    private BigDecimal deductible;
    private LocalDate startDate;
    private LocalDate renewalDate;
    private String status;

    public InsurancePolicyDto() {}

    public InsurancePolicyDto(UUID id, String policyNumber, String carrierName, String category, String productName, BigDecimal coverageAmount, BigDecimal monthlyPremium, BigDecimal deductible, LocalDate startDate, LocalDate renewalDate, String status) {
        this.id = id;
        this.policyNumber = policyNumber;
        this.carrierName = carrierName;
        this.category = category;
        this.productName = productName;
        this.coverageAmount = coverageAmount;
        this.monthlyPremium = monthlyPremium;
        this.deductible = deductible;
        this.startDate = startDate;
        this.renewalDate = renewalDate;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getCoverageAmount() {
        return coverageAmount;
    }

    public void setCoverageAmount(BigDecimal coverageAmount) {
        this.coverageAmount = coverageAmount;
    }

    public BigDecimal getMonthlyPremium() {
        return monthlyPremium;
    }

    public void setMonthlyPremium(BigDecimal monthlyPremium) {
        this.monthlyPremium = monthlyPremium;
    }

    public BigDecimal getDeductible() {
        return deductible;
    }

    public void setDeductible(BigDecimal deductible) {
        this.deductible = deductible;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getRenewalDate() {
        return renewalDate;
    }

    public void setRenewalDate(LocalDate renewalDate) {
        this.renewalDate = renewalDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

