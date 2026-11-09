package com.finpath.modules.insurance.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public class ClaimChecklistRequest {

    @NotBlank(message = "Category is required (HEALTH, AUTO, TERM_LIFE, HOMEOWNERS)")
    private String category;

    private String incidentType; // HOSPITALIZATION, VEHICLE_COLLISION, THEFT, WATER_DAMAGE, CRITICAL_ILLNESS

    private BigDecimal estimatedLoss;

    public ClaimChecklistRequest() {}

    public ClaimChecklistRequest(String category, String incidentType, BigDecimal estimatedLoss) {
        this.category = category;
        this.incidentType = incidentType;
        this.estimatedLoss = estimatedLoss;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIncidentType() {
        return incidentType;
    }

    public void setIncidentType(String incidentType) {
        this.incidentType = incidentType;
    }

    public BigDecimal getEstimatedLoss() {
        return estimatedLoss;
    }

    public void setEstimatedLoss(BigDecimal estimatedLoss) {
        this.estimatedLoss = estimatedLoss;
    }
}

