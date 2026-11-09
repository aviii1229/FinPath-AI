package com.finpath.modules.insurance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class SubmitClaimRequest {

    @NotNull(message = "Policy ID is required")
    private UUID policyId;

    @NotNull(message = "Claim amount is required")
    private BigDecimal claimAmount;

    private LocalDate incidentDate;

    @NotBlank(message = "Incident description is required")
    private String incidentDescription;

    public SubmitClaimRequest() {}

    public SubmitClaimRequest(UUID policyId, BigDecimal claimAmount, LocalDate incidentDate, String incidentDescription) {
        this.policyId = policyId;
        this.claimAmount = claimAmount;
        this.incidentDate = incidentDate;
        this.incidentDescription = incidentDescription;
    }

    public SubmitClaimRequest(String policyId, BigDecimal claimAmount, LocalDate incidentDate, String incidentDescription) {
        this(UUID.fromString(policyId), claimAmount, incidentDate, incidentDescription);
    }

    public UUID getPolicyId() {
        return policyId;
    }

    public void setPolicyId(UUID policyId) {
        this.policyId = policyId;
    }

    public BigDecimal getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(BigDecimal claimAmount) {
        this.claimAmount = claimAmount;
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
}
