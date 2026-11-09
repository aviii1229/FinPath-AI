package com.finpath.modules.journey.dto;

import java.util.ArrayList;
import java.util.List;

public class JourneyStepTemplateDto {

    private String stepKey;
    private int stepOrder;
    private String title;
    private String description;
    private List<String> requiredDocuments = new ArrayList<>();
    private String aiGuidance;
    private List<String> fieldKeys = new ArrayList<>();

    public JourneyStepTemplateDto() {}

    public JourneyStepTemplateDto(String stepKey, int stepOrder, String title, String description, List<String> requiredDocuments, String aiGuidance, List<String> fieldKeys) {
        this.stepKey = stepKey;
        this.stepOrder = stepOrder;
        this.title = title;
        this.description = description;
        this.requiredDocuments = requiredDocuments != null ? requiredDocuments : new ArrayList<>();
        this.aiGuidance = aiGuidance;
        this.fieldKeys = fieldKeys != null ? fieldKeys : new ArrayList<>();
    }

    public String getStepKey() { return stepKey; }
    public void setStepKey(String stepKey) { this.stepKey = stepKey; }

    public int getStepOrder() { return stepOrder; }
    public void setStepOrder(int stepOrder) { this.stepOrder = stepOrder; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getRequiredDocuments() { return requiredDocuments; }
    public void setRequiredDocuments(List<String> requiredDocuments) { this.requiredDocuments = requiredDocuments; }

    public String getAiGuidance() { return aiGuidance; }
    public void setAiGuidance(String aiGuidance) { this.aiGuidance = aiGuidance; }

    public List<String> getFieldKeys() { return fieldKeys; }
    public void setFieldKeys(List<String> fieldKeys) { this.fieldKeys = fieldKeys; }
}

