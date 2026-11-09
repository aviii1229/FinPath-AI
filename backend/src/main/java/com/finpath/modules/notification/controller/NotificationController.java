package com.finpath.modules.notification.controller;

import com.finpath.core.dto.ApiResponse;
import com.finpath.modules.notification.dto.EvaluateNotificationsResponseDto;
import com.finpath.modules.notification.dto.NotificationDto;
import com.finpath.modules.notification.dto.NotificationPreferenceDto;
import com.finpath.modules.notification.dto.NotificationSummaryDto;
import com.finpath.modules.notification.service.NotificationService;
import com.finpath.modules.notification.service.ProactiveNotificationService;
import com.finpath.security.model.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications", description = "Proactive financial assistance, smart reminders & notification center")
public class NotificationController {

    private final NotificationService notificationService;
    private final ProactiveNotificationService proactiveNotificationService;

    public NotificationController(NotificationService notificationService,
                                  ProactiveNotificationService proactiveNotificationService) {
        this.notificationService = notificationService;
        this.proactiveNotificationService = proactiveNotificationService;
    }

    @GetMapping
    @Operation(summary = "Get user notification feed with optional filters")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getNotifications(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean unreadOnly,
            @RequestParam(required = false) String priority) {

        Boolean isRead = (unreadOnly != null && unreadOnly) ? Boolean.FALSE : null;
        List<NotificationDto> notifications = notificationService.getNotifications(
                user.getId(), category, isRead, priority);
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    @GetMapping("/summary")
    @Operation(summary = "Get notification summary metrics and top alerts")
    public ResponseEntity<ApiResponse<NotificationSummaryDto>> getSummary(
            @AuthenticationPrincipal UserPrincipal user) {
        NotificationSummaryDto summary = notificationService.getSummary(user.getId());
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark a single notification as read")
    public ResponseEntity<ApiResponse<NotificationDto>> markAsRead(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID id) {
        NotificationDto updated = notificationService.markAsRead(id, user.getId());
        return ResponseEntity.ok(ApiResponse.success("Marked as read", updated));
    }

    @PutMapping("/read-all")
    @Operation(summary = "Mark all unread notifications as read")
    public ResponseEntity<ApiResponse<Integer>> markAllAsRead(
            @AuthenticationPrincipal UserPrincipal user) {
        int updatedCount = notificationService.markAllAsRead(user.getId());
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read", updatedCount));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a notification")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID id) {
        notificationService.deleteNotification(id, user.getId());
        return ResponseEntity.ok(ApiResponse.success("Notification deleted", null));
    }

    @DeleteMapping("/clear-read")
    @Operation(summary = "Delete all read notifications")
    public ResponseEntity<ApiResponse<Void>> clearAllRead(
            @AuthenticationPrincipal UserPrincipal user) {
        notificationService.clearAllRead(user.getId());
        return ResponseEntity.ok(ApiResponse.success("All read notifications cleared", null));
    }

    @PostMapping("/evaluate")
    @Operation(summary = "Manually trigger proactive evaluation scan for the current user")
    public ResponseEntity<ApiResponse<EvaluateNotificationsResponseDto>> evaluateNotifications(
            @AuthenticationPrincipal UserPrincipal user) {
        EvaluateNotificationsResponseDto response = proactiveNotificationService
                .evaluateProactiveNotifications(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Proactive evaluation scan complete", response));
    }

    @GetMapping("/preferences")
    @Operation(summary = "Get user notification preferences and channel configurations")
    public ResponseEntity<ApiResponse<NotificationPreferenceDto>> getPreferences(
            @AuthenticationPrincipal UserPrincipal user) {
        NotificationPreferenceDto prefs = notificationService.getPreferences(user.getId());
        return ResponseEntity.ok(ApiResponse.success(prefs));
    }

    @PutMapping("/preferences")
    @Operation(summary = "Update user notification preferences, categories, and quiet hours")
    public ResponseEntity<ApiResponse<NotificationPreferenceDto>> updatePreferences(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody NotificationPreferenceDto dto) {
        NotificationPreferenceDto updated = notificationService.updatePreferences(user.getId(), dto);
        return ResponseEntity.ok(ApiResponse.success("Preferences updated", updated));
    }
}
