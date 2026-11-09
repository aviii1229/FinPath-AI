package com.finpath.modules.journey.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "journey_step_records")
@EntityListeners(AuditingEntityListener.class)
public class JourneyStepRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journey_id", nullable = false)
    private UserJourneyEntity journey;

    @Column(name = "step_key", nullable = false, length = 60)
    private String stepKey;

    @Column(name = "step_order", nullable = false)
    private int stepOrder;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "status", nullable = false, length = 30)
    private String status = "PENDING"; // PENDING, IN_PROGRESS, COMPLETED, SKIPPED

    @Column(name = "step_data_json", columnDefinition = "TEXT")
    private String stepDataJson;

    @Column(name = "required_documents_json", columnDefinition = "TEXT")
    private String requiredDocumentsJson;

    @Column(name = "ai_guidance", columnDefinition = "TEXT")
    private String aiGuidance;

    @Column(name = "completed_at")
    private Instant completedAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    public JourneyStepRecordEntity() {}

    public JourneyStepRecordEntity(UserJourneyEntity journey, String stepKey, int stepOrder, String title, String description, String status, String requiredDocumentsJson, String aiGuidance) {
        this.journey = journey;
        this.stepKey = stepKey;
        this.stepOrder = stepOrder;
        this.title = title;
        this.description = description;
        this.status = status;
        this.requiredDocumentsJson = requiredDocumentsJson;
        this.aiGuidance = aiGuidance;
        this.updatedAt = Instant.now();
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UserJourneyEntity getJourney() { return journey; }
    public void setJourney(UserJourneyEntity journey) { this.journey = journey; }

    public String getStepKey() { return stepKey; }
    public void setStepKey(String stepKey) { this.stepKey = stepKey; }

    public int getStepOrder() { return stepOrder; }
    public void setStepOrder(int stepOrder) { this.stepOrder = stepOrder; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getStepDataJson() { return stepDataJson; }
    public void setStepDataJson(String stepDataJson) { this.stepDataJson = stepDataJson; }

    public String getRequiredDocumentsJson() { return requiredDocumentsJson; }
    public void setRequiredDocumentsJson(String requiredDocumentsJson) { this.requiredDocumentsJson = requiredDocumentsJson; }

    public String getAiGuidance() { return aiGuidance; }
    public void setAiGuidance(String aiGuidance) { this.aiGuidance = aiGuidance; }

    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}

