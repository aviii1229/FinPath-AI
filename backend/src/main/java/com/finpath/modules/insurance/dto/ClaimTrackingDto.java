package com.finpath.modules.insurance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class ClaimTrackingDto {

    private UUID claimId;
    private String claimNumber;
    private String policyNumber;
    private String policyName;
    private String category;
    private BigDecimal claimAmount;
    private BigDecimal approvedAmount;
    private LocalDate incidentDate;
    private String currentStatus;
    private int currentStepIndex; // 0: Submitted, 1: Verification, 2: Underwriting, 3: Approved/Settled
    private List<ClaimStageDto> stages;
    private BigDecimal aiFraudScore;
    private String nextActionPrompt;

    public ClaimTrackingDto() {}

    public static class ClaimStageDto {
        private String stageName;
        private String status; // COMPLETED, CURRENT, PENDING
        private String timestamp;
        private String note;

        public ClaimStageDto() {}

        public ClaimStageDto(String stageName, String status, String timestamp, String note) {
            this.stageName = stageName;
            this.status = status;
            this.timestamp = timestamp;
            this.note = note;
        }

        public String getStageName() {
            return stageName;
        }

        public void setStageName(String stageName) {
            this.stageName = stageName;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }
    }

    public UUID getClaimId() {
        return claimId;
    }

    public void setClaimId(UUID claimId) {
        this.claimId = claimId;
    }

    public String getClaimNumber() {
        return claimNumber;
    }

    public void setClaimNumber(String claimNumber) {
        this.claimNumber = claimNumber;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
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

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public int getCurrentStepIndex() {
        return currentStepIndex;
    }

    public void setCurrentStepIndex(int currentStepIndex) {
        this.currentStepIndex = currentStepIndex;
    }

    public List<ClaimStageDto> getStages() {
        return stages;
    }

    public void setStages(List<ClaimStageDto> stages) {
        this.stages = stages;
    }

    public BigDecimal getAiFraudScore() {
        return aiFraudScore;
    }

    public void setAiFraudScore(BigDecimal aiFraudScore) {
        this.aiFraudScore = aiFraudScore;
    }

    public String getNextActionPrompt() {
        return nextActionPrompt;
    }

    public void setNextActionPrompt(String nextActionPrompt) {
        this.nextActionPrompt = nextActionPrompt;
    }
}

