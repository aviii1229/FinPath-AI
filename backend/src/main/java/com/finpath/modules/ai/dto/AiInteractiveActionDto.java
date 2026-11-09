package com.finpath.modules.ai.dto;

public class AiInteractiveActionDto {
    private String label;
    private String actionType; // PROMPT, NAVIGATE, ACTION
    private String payload;
    private String targetUrl;

    public AiInteractiveActionDto() {}

    public AiInteractiveActionDto(String label, String actionType, String payload, String targetUrl) {
        this.label = label;
        this.actionType = actionType;
        this.payload = payload;
        this.targetUrl = targetUrl;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }
}

