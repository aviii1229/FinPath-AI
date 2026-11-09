package com.finpath.modules.journey.dto;

public class JourneyRecommendationDto {

    private String journeyType;
    private String domain;
    private String title;
    private String rationale;
    private int estimatedMinutes;
    private String suggestedFirstStep;
    private int priorityOrder;

    public JourneyRecommendationDto() {}

    public JourneyRecommendationDto(String journeyType, String domain, String title, String rationale, int estimatedMinutes, String suggestedFirstStep, int priorityOrder) {
        this.journeyType = journeyType;
        this.domain = domain;
        this.title = title;
        this.rationale = rationale;
        this.estimatedMinutes = estimatedMinutes;
        this.suggestedFirstStep = suggestedFirstStep;
        this.priorityOrder = priorityOrder;
    }

    public String getJourneyType() { return journeyType; }
    public void setJourneyType(String journeyType) { this.journeyType = journeyType; }

    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getRationale() { return rationale; }
    public void setRationale(String rationale) { this.rationale = rationale; }

    public int getEstimatedMinutes() { return estimatedMinutes; }
    public void setEstimatedMinutes(int estimatedMinutes) { this.estimatedMinutes = estimatedMinutes; }

    public String getSuggestedFirstStep() { return suggestedFirstStep; }
    public void setSuggestedFirstStep(String suggestedFirstStep) { this.suggestedFirstStep = suggestedFirstStep; }

    public int getPriorityOrder() { return priorityOrder; }
    public void setPriorityOrder(int priorityOrder) { this.priorityOrder = priorityOrder; }
}
