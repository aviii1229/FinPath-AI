package com.finpath.modules.journey.entity;

import com.finpath.security.entity.UserEntity;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "user_journeys")
@EntityListeners(AuditingEntityListener.class)
public class UserJourneyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "journey_type", nullable = false, length = 60)
    private String journeyType;

    @Column(name = "journey_domain", nullable = false, length = 30)
    private String journeyDomain; // INSURANCE, LENDING, FINTECH

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "status", nullable = false, length = 30)
    private String status = "IN_PROGRESS"; // NOT_STARTED, IN_PROGRESS, PAUSED, COMPLETED, CANCELLED

    @Column(name = "current_step_key", nullable = false, length = 60)
    private String currentStepKey;

    @Column(name = "current_step_index", nullable = false)
    private int currentStepIndex = 0;

    @Column(name = "total_steps", nullable = false)
    private int totalSteps;

    @Column(name = "progress_percentage", nullable = false)
    private int progressPercentage = 0;

    @Column(name = "journey_data_json", columnDefinition = "TEXT")
    private String journeyDataJson;

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    @Column(name = "is_simulated", nullable = false)
    private boolean simulated = true;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    @OneToMany(mappedBy = "journey", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("stepOrder ASC")
    private List<JourneyStepRecordEntity> stepRecords = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    @Column(name = "completed_at")
    private Instant completedAt;

    public UserJourneyEntity() {}

    public UserJourneyEntity(UserEntity user, String journeyType, String journeyDomain, String title, String description, int totalSteps, String firstStepKey) {
        this.user = user;
        this.journeyType = journeyType;
        this.journeyDomain = journeyDomain;
        this.title = title;
        this.description = description;
        this.totalSteps = totalSteps;
        this.currentStepKey = firstStepKey;
        this.currentStepIndex = 0;
        this.status = "IN_PROGRESS";
        this.simulated = true;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void addStepRecord(JourneyStepRecordEntity step) {
        stepRecords.add(step);
        step.setJourney(this);
    }

    public void calculateProgress() {
        if (totalSteps <= 0) {
            this.progressPercentage = 0;
            return;
        }
        long completed = stepRecords.stream()
                .filter(s -> "COMPLETED".equalsIgnoreCase(s.getStatus()))
                .count();
        this.progressPercentage = (int) Math.round(((double) completed / totalSteps) * 100);
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UserEntity getUser() { return user; }
    public void setUser(UserEntity user) { this.user = user; }

    public String getJourneyType() { return journeyType; }
    public void setJourneyType(String journeyType) { this.journeyType = journeyType; }

    public String getJourneyDomain() { return journeyDomain; }
    public void setJourneyDomain(String journeyDomain) { this.journeyDomain = journeyDomain; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCurrentStepKey() { return currentStepKey; }
    public void setCurrentStepKey(String currentStepKey) { this.currentStepKey = currentStepKey; }

    public int getCurrentStepIndex() { return currentStepIndex; }
    public void setCurrentStepIndex(int currentStepIndex) { this.currentStepIndex = currentStepIndex; }

    public int getTotalSteps() { return totalSteps; }
    public void setTotalSteps(int totalSteps) { this.totalSteps = totalSteps; }

    public int getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(int progressPercentage) { this.progressPercentage = progressPercentage; }

    public String getJourneyDataJson() { return journeyDataJson; }
    public void setJourneyDataJson(String journeyDataJson) { this.journeyDataJson = journeyDataJson; }

    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }

    public boolean isSimulated() { return simulated; }
    public void setSimulated(boolean simulated) { this.simulated = simulated; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    public List<JourneyStepRecordEntity> getStepRecords() { return stepRecords; }
    public void setStepRecords(List<JourneyStepRecordEntity> stepRecords) { this.stepRecords = stepRecords; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
}

