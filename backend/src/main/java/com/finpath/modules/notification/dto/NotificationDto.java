package com.finpath.modules.notification.dto;

import java.time.Instant;
import java.util.UUID;

public class NotificationDto {
    private UUID id;
    private String title;
    private String description;
    private String type; // SUCCESS, WARNING, INFO, ERROR
    private String category; // LOAN_EMI, INSURANCE_RENEWAL, BUDGET_ALERT, SAVINGS_GOAL, JOURNEY_INCOMPLETE, DOCUMENT_MISSING, SYSTEM
    private String priority; // LOW, MEDIUM, HIGH, CRITICAL
    private String actionUrl;
    private String actionLabel;
    private boolean isRead;
    private Instant readAt;
    private String deduplicationKey;
    private String channelDeliveryStatus;
    private String metadataJson;
    private Instant createdAt;

    public NotificationDto() {}

    public NotificationDto(UUID id, String title, String description, String type, boolean isRead, Instant createdAt) {
        this(id, title, description, type, "SYSTEM", "MEDIUM", null, null, isRead, null, null, null, null, createdAt);
    }

    public NotificationDto(UUID id, String title, String description, String type, String category,
                           String priority, String actionUrl, String actionLabel, boolean isRead,
                           Instant readAt, String deduplicationKey, String channelDeliveryStatus,
                           String metadataJson, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.category = category;
        this.priority = priority;
        this.actionUrl = actionUrl;
        this.actionLabel = actionLabel;
        this.isRead = isRead;
        this.readAt = readAt;
        this.deduplicationKey = deduplicationKey;
        this.channelDeliveryStatus = channelDeliveryStatus;
        this.metadataJson = metadataJson;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    public String getActionLabel() {
        return actionLabel;
    }

    public void setActionLabel(String actionLabel) {
        this.actionLabel = actionLabel;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public Instant getReadAt() {
        return readAt;
    }

    public void setReadAt(Instant readAt) {
        this.readAt = readAt;
    }

    public String getDeduplicationKey() {
        return deduplicationKey;
    }

    public void setDeduplicationKey(String deduplicationKey) {
        this.deduplicationKey = deduplicationKey;
    }

    public String getChannelDeliveryStatus() {
        return channelDeliveryStatus;
    }

    public void setChannelDeliveryStatus(String channelDeliveryStatus) {
        this.channelDeliveryStatus = channelDeliveryStatus;
    }

    public String getMetadataJson() {
        return metadataJson;
    }

    public void setMetadataJson(String metadataJson) {
        this.metadataJson = metadataJson;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
