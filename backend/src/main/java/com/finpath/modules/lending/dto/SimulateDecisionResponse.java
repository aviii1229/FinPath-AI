package com.finpath.modules.lending.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class SimulateDecisionResponse {

    private UUID applicationId;
    private String applicationNumber;
    private String decision; // APPROVED, CONDITIONALLY_APPROVED, DECLINED, DOCUMENTS_REQUIRED
    private String explanation;
    private BigDecimal approvedAmount;
    private BigDecimal finalApr;
    private BigDecimal monthlyEmi;
    private BigDecimal evaluatedFoir;
    private List<String> conditions;
    private List<String> requestedDocuments;
    private boolean isSimulated = true;

    public SimulateDecisionResponse() {}

    public SimulateDecisionResponse(UUID applicationId, String applicationNumber, String decision, String explanation, BigDecimal approvedAmount, BigDecimal finalApr, BigDecimal monthlyEmi, BigDecimal evaluatedFoir, List<String> conditions, List<String> requestedDocuments, boolean isSimulated) {
        this.applicationId = applicationId;
        this.applicationNumber = applicationNumber;
        this.decision = decision;
        this.explanation = explanation;
        this.approvedAmount = approvedAmount;
        this.finalApr = finalApr;
        this.monthlyEmi = monthlyEmi;
        this.evaluatedFoir = evaluatedFoir;
        this.conditions = conditions;
        this.requestedDocuments = requestedDocuments;
        this.isSimulated = isSimulated;
    }

    public UUID getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(UUID applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public BigDecimal getApprovedAmount() {
        return approvedAmount;
    }

    public void setApprovedAmount(BigDecimal approvedAmount) {
        this.approvedAmount = approvedAmount;
    }

    public BigDecimal getFinalApr() {
        return finalApr;
    }

    public void setFinalApr(BigDecimal finalApr) {
        this.finalApr = finalApr;
    }

    public BigDecimal getMonthlyEmi() {
        return monthlyEmi;
    }

    public void setMonthlyEmi(BigDecimal monthlyEmi) {
        this.monthlyEmi = monthlyEmi;
    }

    public BigDecimal getEvaluatedFoir() {
        return evaluatedFoir;
    }

    public void setEvaluatedFoir(BigDecimal evaluatedFoir) {
        this.evaluatedFoir = evaluatedFoir;
    }

    public List<String> getConditions() {
        return conditions;
    }

    public void setConditions(List<String> conditions) {
        this.conditions = conditions;
    }

    public List<String> getRequestedDocuments() {
        return requestedDocuments;
    }

    public void setRequestedDocuments(List<String> requestedDocuments) {
        this.requestedDocuments = requestedDocuments;
    }

    public boolean isSimulated() {
        return isSimulated;
    }

    public void setSimulated(boolean simulated) {
        isSimulated = simulated;
    }
}

