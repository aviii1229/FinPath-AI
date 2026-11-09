package com.finpath.modules.personalization.dto;

public class SuggestedJourneyDto {
    private String journeyId;
    private String title;
    private String category;
    private String description;
    private String actionLabel;
    private String route;
    private int estimatedMinutes;
    private String impactBadge;

    public SuggestedJourneyDto() {}

    public SuggestedJourneyDto(String journeyId, String title, String category, String description, String actionLabel, String route, int estimatedMinutes, String impactBadge) {
        this.journeyId = journeyId;
        this.title = title;
        this.category = category;
        this.description = description;
        this.actionLabel = actionLabel;
        this.route = route;
        this.estimatedMinutes = estimatedMinutes;
        this.impactBadge = impactBadge;
    }

    public String getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(String journeyId) {
        this.journeyId = journeyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getActionLabel() {
        return actionLabel;
    }

    public void setActionLabel(String actionLabel) {
        this.actionLabel = actionLabel;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public int getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setEstimatedMinutes(int estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public String getImpactBadge() {
        return impactBadge;
    }

    public void setImpactBadge(String impactBadge) {
        this.impactBadge = impactBadge;
    }
}

