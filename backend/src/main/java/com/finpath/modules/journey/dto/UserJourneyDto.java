package com.finpath.modules.journey.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UserJourneyDto {

    private UUID id;
    private String journeyType;
    private String journeyDomain;
    private String title;
    private String description;
    private String status; // NOT_STARTED, IN_PROGRESS, PAUSED, COMPLETED, CANCELLED
    private String currentStepKey;
    private int currentStepIndex;
    private int totalSteps;
    private int progressPercentage;
    private Map<String, Object> journeyData;
    private String cancellationReason;
    private boolean simulated;
    private Long version;
    private List<JourneyStepRecordDto> steps = new ArrayList<>();
    private String nextRecommendedAction;
    private List<String> pendingRequiredDocuments = new ArrayList<>();
    private Instant createdAt;
    private Instant updatedAt;
    private Instant completedAt;

    public UserJourneyDto() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getJourneyType() { return journeyType; }
    public void setJourneyType(String journeyType) { this.journeyType = journeyType; }

    public String getJourneyDomain() { return journeyDomain; }
    public void setJourneyDomain(String journeyDomain) { this.journeyDomain = journeyDomain; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCurrentStepKey() { return currentStepKey; }
    public void setCurrentStepKey(String currentStepKey) { this.currentStepKey = currentStepKey; }

    public int getCurrentStepIndex() { return currentStepIndex; }
    public void setCurrentStepIndex(int currentStepIndex) { this.currentStepIndex = currentStepIndex; }

    public int getTotalSteps() { return totalSteps; }
    public void setTotalSteps(int totalSteps) { this.totalSteps = totalSteps; }

    public int getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(int progressPercentage) { this.progressPercentage = progressPercentage; }

    public Map<String, Object> getJourneyData() { return journeyData; }
    public void setJourneyData(Map<String, Object> journeyData) { this.journeyData = journeyData; }

    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }

    public boolean isSimulated() { return simulated; }
    public void setSimulated(boolean simulated) { this.simulated = simulated; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    public List<JourneyStepRecordDto> getSteps() { return steps; }
    public void setSteps(List<JourneyStepRecordDto> steps) { this.steps = steps; }

    public String getNextRecommendedAction() { return nextRecommendedAction; }
    public void setNextRecommendedAction(String nextRecommendedAction) { this.nextRecommendedAction = nextRecommendedAction; }

    public List<String> getPendingRequiredDocuments() { return pendingRequiredDocuments; }
    public void setPendingRequiredDocuments(List<String> pendingRequiredDocuments) { this.pendingRequiredDocuments = pendingRequiredDocuments; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
}

