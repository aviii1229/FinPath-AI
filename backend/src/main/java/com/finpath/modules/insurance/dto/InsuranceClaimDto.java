package com.finpath.modules.insurance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class InsuranceClaimDto {
    private UUID id;
    private String claimNumber;
    private UUID policyId;
    private String policyName;
    private String category;
    private BigDecimal claimAmount;
    private BigDecimal approvedAmount;
    private LocalDate incidentDate;
    private String incidentDescription;
    private String status;
    private BigDecimal aiFraudRiskScore;

    public InsuranceClaimDto() {}

    public InsuranceClaimDto(UUID id, String claimNumber, UUID policyId, String policyName, String category, BigDecimal claimAmount, BigDecimal approvedAmount, LocalDate incidentDate, String incidentDescription, String status, BigDecimal aiFraudRiskScore) {
        this.id = id;
        this.claimNumber = claimNumber;
        this.policyId = policyId;
        this.policyName = policyName;
        this.category = category;
        this.claimAmount = claimAmount;
        this.approvedAmount = approvedAmount;
        this.incidentDate = incidentDate;
        this.incidentDescription = incidentDescription;
        this.status = status;
        this.aiFraudRiskScore = aiFraudRiskScore;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getClaimNumber() {
        return claimNumber;
    }

    public void setClaimNumber(String claimNumber) {
        this.claimNumber = claimNumber;
    }

    public UUID getPolicyId() {
        return policyId;
    }

    public void setPolicyId(UUID policyId) {
        this.policyId = policyId;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(BigDecimal claimAmount) {
        this.claimAmount = claimAmount;
    }

    public BigDecimal getApprovedAmount() {
        return approvedAmount;
    }

    public void setApprovedAmount(BigDecimal approvedAmount) {
        this.approvedAmount = approvedAmount;
    }

    public LocalDate getIncidentDate() {
        return incidentDate;
    }

    public void setIncidentDate(LocalDate incidentDate) {
        this.incidentDate = incidentDate;
    }

    public String getIncidentDescription() {
        return incidentDescription;
    }

    public void setIncidentDescription(String incidentDescription) {
        this.incidentDescription = incidentDescription;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getAiFraudRiskScore() {
        return aiFraudRiskScore;
    }

    public BigDecimal getFraudRiskScore() {
        return aiFraudRiskScore;
    }

    public void setAiFraudRiskScore(BigDecimal aiFraudRiskScore) {
        this.aiFraudRiskScore = aiFraudRiskScore;
    }
}
