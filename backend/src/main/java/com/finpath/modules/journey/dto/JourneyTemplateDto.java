package com.finpath.modules.journey.dto;

import java.util.ArrayList;
import java.util.List;

public class JourneyTemplateDto {

    private String journeyType;
    private String domain; // INSURANCE, LENDING, FINTECH
    private String title;
    private String description;
    private int estimatedMinutes;
    private int totalSteps;
    private List<JourneyStepTemplateDto> steps = new ArrayList<>();

    public JourneyTemplateDto() {}

    public JourneyTemplateDto(String journeyType, String domain, String title, String description, int estimatedMinutes, List<JourneyStepTemplateDto> steps) {
        this.journeyType = journeyType;
        this.domain = domain;
        this.title = title;
        this.description = description;
        this.estimatedMinutes = estimatedMinutes;
        this.steps = steps != null ? steps : new ArrayList<>();
        this.totalSteps = this.steps.size();
    }

    public String getJourneyType() { return journeyType; }
    public void setJourneyType(String journeyType) { this.journeyType = journeyType; }

    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getEstimatedMinutes() { return estimatedMinutes; }
    public void setEstimatedMinutes(int estimatedMinutes) { this.estimatedMinutes = estimatedMinutes; }

    public int getTotalSteps() { return totalSteps; }
    public void setTotalSteps(int totalSteps) { this.totalSteps = totalSteps; }

    public List<JourneyStepTemplateDto> getSteps() { return steps; }
    public void setSteps(List<JourneyStepTemplateDto> steps) {
        this.steps = steps;
        this.totalSteps = steps != null ? steps.size() : 0;
    }
}

