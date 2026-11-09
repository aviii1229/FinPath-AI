package com.finpath.modules.ai.dto;

import java.util.List;

public class AiJourneyStageDto {
    private String stageName;
    private int currentStep;
    private int totalSteps;
    private String stepTitle;
    private List<String> completedSteps;
    private List<String> pendingSteps;

    public AiJourneyStageDto() {}

    public AiJourneyStageDto(String stageName, int currentStep, int totalSteps, String stepTitle, List<String> completedSteps, List<String> pendingSteps) {
        this.stageName = stageName;
        this.currentStep = currentStep;
        this.totalSteps = totalSteps;
        this.stepTitle = stepTitle;
        this.completedSteps = completedSteps;
        this.pendingSteps = pendingSteps;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }

    public int getTotalSteps() {
        return totalSteps;
    }

    public void setTotalSteps(int totalSteps) {
        this.totalSteps = totalSteps;
    }

    public String getStepTitle() {
        return stepTitle;
    }

    public void setStepTitle(String stepTitle) {
        this.stepTitle = stepTitle;
    }

    public List<String> getCompletedSteps() {
        return completedSteps;
    }

    public void setCompletedSteps(List<String> completedSteps) {
        this.completedSteps = completedSteps;
    }

    public List<String> getPendingSteps() {
        return pendingSteps;
    }

    public void setPendingSteps(List<String> pendingSteps) {
        this.pendingSteps = pendingSteps;
    }
}

