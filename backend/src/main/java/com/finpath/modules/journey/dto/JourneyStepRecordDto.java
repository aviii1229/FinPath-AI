package com.finpath.modules.journey.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JourneyStepRecordDto {

    private UUID id;
    private String stepKey;
    private int stepOrder;
    private String title;
    private String description;
    private String status; // PENDING, IN_PROGRESS, COMPLETED, SKIPPED
    private Map<String, Object> stepData;
    private List<String> requiredDocuments = new ArrayList<>();
    private String aiGuidance;
    private Instant completedAt;
    private Instant updatedAt;

    public JourneyStepRecordDto() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getStepKey() { return stepKey; }
    public void setStepKey(String stepKey) { this.stepKey = stepKey; }

    public int getStepOrder() { return stepOrder; }
    public void setStepOrder(int stepOrder) { this.stepOrder = stepOrder; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Map<String, Object> getStepData() { return stepData; }
    public void setStepData(Map<String, Object> stepData) { this.stepData = stepData; }

    public List<String> getRequiredDocuments() { return requiredDocuments; }
    public void setRequiredDocuments(List<String> requiredDocuments) { this.requiredDocuments = requiredDocuments; }

    public String getAiGuidance() { return aiGuidance; }
    public void setAiGuidance(String aiGuidance) { this.aiGuidance = aiGuidance; }

    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
