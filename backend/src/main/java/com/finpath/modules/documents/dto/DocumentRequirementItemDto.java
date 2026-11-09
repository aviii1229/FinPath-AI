package com.finpath.modules.documents.dto;

import java.util.UUID;

public class DocumentRequirementItemDto {

    private String requirementKey;
    private String title;
    private String description;
    private String category;
    private boolean required;
    private boolean fulfilled;
    private UUID matchedDocumentId;
    private String matchedDocumentName;
    private boolean expired;
    private String statusMessage;

    public DocumentRequirementItemDto() {}

    public DocumentRequirementItemDto(String requirementKey, String title, String description, String category, boolean required, boolean fulfilled, UUID matchedDocumentId, String matchedDocumentName, boolean expired, String statusMessage) {
        this.requirementKey = requirementKey;
        this.title = title;
        this.description = description;
        this.category = category;
        this.required = required;
        this.fulfilled = fulfilled;
        this.matchedDocumentId = matchedDocumentId;
        this.matchedDocumentName = matchedDocumentName;
        this.expired = expired;
        this.statusMessage = statusMessage;
    }

    public String getRequirementKey() { return requirementKey; }
    public void setRequirementKey(String requirementKey) { this.requirementKey = requirementKey; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }

    public boolean isFulfilled() { return fulfilled; }
    public void setFulfilled(boolean fulfilled) { this.fulfilled = fulfilled; }

    public UUID getMatchedDocumentId() { return matchedDocumentId; }
    public void setMatchedDocumentId(UUID matchedDocumentId) { this.matchedDocumentId = matchedDocumentId; }

    public String getMatchedDocumentName() { return matchedDocumentName; }
    public void setMatchedDocumentName(String matchedDocumentName) { this.matchedDocumentName = matchedDocumentName; }

    public boolean isExpired() { return expired; }
    public void setExpired(boolean expired) { this.expired = expired; }

    public String getStatusMessage() { return statusMessage; }
    public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }
}
