package com.finpath.modules.notification.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class NotificationSummaryDto {

    private long unreadCount;
    private long totalCount;
    private long criticalCount;
    private long highCount;
    private List<NotificationDto> recentNotifications;
    private Map<String, Long> categoryCounts;
    private Instant lastEvaluatedAt;

    public NotificationSummaryDto() {}

    public NotificationSummaryDto(long unreadCount, long totalCount, long criticalCount, long highCount,
                                List<NotificationDto> recentNotifications, Map<String, Long> categoryCounts,
                                Instant lastEvaluatedAt) {
        this.unreadCount = unreadCount;
        this.totalCount = totalCount;
        this.criticalCount = criticalCount;
        this.highCount = highCount;
        this.recentNotifications = recentNotifications;
        this.categoryCounts = categoryCounts;
        this.lastEvaluatedAt = lastEvaluatedAt;
    }

    public long getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(long unreadCount) {
        this.unreadCount = unreadCount;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getCriticalCount() {
        return criticalCount;
    }

    public void setCriticalCount(long criticalCount) {
        this.criticalCount = criticalCount;
    }

    public long getHighCount() {
        return highCount;
    }

    public void setHighCount(long highCount) {
        this.highCount = highCount;
    }

    public List<NotificationDto> getRecentNotifications() {
        return recentNotifications;
    }

    public void setRecentNotifications(List<NotificationDto> recentNotifications) {
        this.recentNotifications = recentNotifications;
    }

    public Map<String, Long> getCategoryCounts() {
        return categoryCounts;
    }

    public void setCategoryCounts(Map<String, Long> categoryCounts) {
        this.categoryCounts = categoryCounts;
    }

    public Instant getLastEvaluatedAt() {
        return lastEvaluatedAt;
    }

    public void setLastEvaluatedAt(Instant lastEvaluatedAt) {
        this.lastEvaluatedAt = lastEvaluatedAt;
    }
}
