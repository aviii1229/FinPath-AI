package com.finpath.modules.personalization.dto;

public class RelevantModuleDto {
    private String moduleId; // "INSURANCE", "LENDING", "FINTECH", "AI_ORCHESTRATOR"
    private String title;
    private String subtitle;
    private String priority; // "HIGH", "MEDIUM", "LOW"
    private String matchReason;
    private String route;
    private String iconName;

    public RelevantModuleDto() {}

    public RelevantModuleDto(String moduleId, String title, String subtitle, String priority, String matchReason, String route, String iconName) {
        this.moduleId = moduleId;
        this.title = title;
        this.subtitle = subtitle;
        this.priority = priority;
        this.matchReason = matchReason;
        this.route = route;
        this.iconName = iconName;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getMatchReason() {
        return matchReason;
    }

    public void setMatchReason(String matchReason) {
        this.matchReason = matchReason;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }
}

