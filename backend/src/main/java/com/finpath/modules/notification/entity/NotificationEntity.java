package com.finpath.modules.notification.entity;

import com.finpath.security.entity.UserEntity;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@EntityListeners(AuditingEntityListener.class)
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false, length = 30)
    private String type = "INFO"; // SUCCESS, WARNING, INFO, ERROR

    @Column(nullable = false, length = 50)
    private String category = "SYSTEM"; // LOAN_EMI, INSURANCE_RENEWAL, BUDGET_ALERT, SAVINGS_GOAL, JOURNEY_INCOMPLETE, DOCUMENT_MISSING, SYSTEM

    @Column(nullable = false, length = 20)
    private String priority = "MEDIUM"; // LOW, MEDIUM, HIGH, CRITICAL

    @Column(name = "action_url", length = 255)
    private String actionUrl;

    @Column(name = "action_label", length = 100)
    private String actionLabel;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Column(name = "read_at")
    private Instant readAt;

    @Column(name = "deduplication_key", length = 150)
    private String deduplicationKey;

    @Column(name = "metadata_json", columnDefinition = "TEXT")
    private String metadataJson;

    @Column(name = "channel_delivery_json", columnDefinition = "TEXT")
    private String channelDeliveryJson;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public NotificationEntity() {}

    public NotificationEntity(UserEntity user, String title, String description, String type, String category, String priority) {
        this.user = user;
        this.title = title;
        this.description = description;
        this.type = type;
        this.category = category;
        this.priority = priority;
        this.isRead = false;
    }

    public NotificationEntity(UserEntity user, String title, String description, String type) {
        this(user, title, description, type, "SYSTEM", "MEDIUM");
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
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
        this.isRead = read;
        if (read && this.readAt == null) {
            this.readAt = Instant.now();
        } else if (!read) {
            this.readAt = null;
        }
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

    public String getMetadataJson() {
        return metadataJson;
    }

    public void setMetadataJson(String metadataJson) {
        this.metadataJson = metadataJson;
    }

    public String getChannelDeliveryJson() {
        return channelDeliveryJson;
    }

    public void setChannelDeliveryJson(String channelDeliveryJson) {
        this.channelDeliveryJson = channelDeliveryJson;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
