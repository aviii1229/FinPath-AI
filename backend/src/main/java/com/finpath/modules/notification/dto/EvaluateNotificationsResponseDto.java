package com.finpath.modules.notification.dto;

import java.time.Instant;
import java.util.List;

public class EvaluateNotificationsResponseDto {

    private int evaluatedRulesCount;
    private int generatedCount;
    private int skippedDuplicatesCount;
    private List<NotificationDto> generatedNotifications;
    private Instant evaluationTimestamp;

    public EvaluateNotificationsResponseDto() {}

    public EvaluateNotificationsResponseDto(int evaluatedRulesCount, int generatedCount, int skippedDuplicatesCount,
                                           List<NotificationDto> generatedNotifications, Instant evaluationTimestamp) {
        this.evaluatedRulesCount = evaluatedRulesCount;
        this.generatedCount = generatedCount;
        this.skippedDuplicatesCount = skippedDuplicatesCount;
        this.generatedNotifications = generatedNotifications;
        this.evaluationTimestamp = evaluationTimestamp;
    }

    public int getEvaluatedRulesCount() {
        return evaluatedRulesCount;
    }

    public void setEvaluatedRulesCount(int evaluatedRulesCount) {
        this.evaluatedRulesCount = evaluatedRulesCount;
    }

    public int getGeneratedCount() {
        return generatedCount;
    }

    public void setGeneratedCount(int generatedCount) {
        this.generatedCount = generatedCount;
    }

    public int getSkippedDuplicatesCount() {
        return skippedDuplicatesCount;
    }

    public void setSkippedDuplicatesCount(int skippedDuplicatesCount) {
        this.skippedDuplicatesCount = skippedDuplicatesCount;
    }

    public List<NotificationDto> getGeneratedNotifications() {
        return generatedNotifications;
    }

    public void setGeneratedNotifications(List<NotificationDto> generatedNotifications) {
        this.generatedNotifications = generatedNotifications;
    }

    public Instant getEvaluationTimestamp() {
        return evaluationTimestamp;
    }

    public void setEvaluationTimestamp(Instant evaluationTimestamp) {
        this.evaluationTimestamp = evaluationTimestamp;
    }
}
