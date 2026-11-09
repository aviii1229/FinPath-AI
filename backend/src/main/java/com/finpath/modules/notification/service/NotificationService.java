package com.finpath.modules.notification.service;

import com.finpath.core.exception.ResourceNotFoundException;
import com.finpath.modules.notification.dto.NotificationDto;
import com.finpath.modules.notification.dto.NotificationPreferenceDto;
import com.finpath.modules.notification.dto.NotificationSummaryDto;
import com.finpath.modules.notification.entity.NotificationEntity;
import com.finpath.modules.notification.entity.NotificationPreferenceEntity;
import com.finpath.modules.notification.repository.NotificationPreferenceRepository;
import com.finpath.modules.notification.repository.NotificationRepository;
import com.finpath.security.entity.UserEntity;
import com.finpath.security.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               NotificationPreferenceRepository preferenceRepository,
                               UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.preferenceRepository = preferenceRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> getNotifications(UUID userId, String category, Boolean isRead, String priority) {
        String cleanCategory = (category != null && !category.isBlank() && !category.equalsIgnoreCase("ALL"))
                ? category.trim().toUpperCase() : null;
        String cleanPriority = (priority != null && !priority.isBlank() && !priority.equalsIgnoreCase("ALL"))
                ? priority.trim().toUpperCase() : null;

        List<NotificationEntity> entities = notificationRepository.searchNotifications(
                userId, cleanCategory, isRead, cleanPriority);

        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public NotificationSummaryDto getSummary(UUID userId) {
        long unreadCount = notificationRepository.countByUserIdAndIsReadFalse(userId);
        long totalCount = notificationRepository.countByUserId(userId);
        long criticalCount = notificationRepository.countByUserIdAndPriorityAndIsReadFalse(userId, "CRITICAL");
        long highCount = notificationRepository.countByUserIdAndPriorityAndIsReadFalse(userId, "HIGH");

        List<NotificationEntity> top5 = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().limit(5).collect(Collectors.toList());

        List<NotificationEntity> allUserNotifs = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        Map<String, Long> categoryCounts = allUserNotifs.stream()
                .collect(Collectors.groupingBy(NotificationEntity::getCategory, Collectors.counting()));

        return new NotificationSummaryDto(
                unreadCount,
                totalCount,
                criticalCount,
                highCount,
                top5.stream().map(this::toDto).collect(Collectors.toList()),
                categoryCounts,
                Instant.now()
        );
    }

    @Transactional
    public NotificationDto markAsRead(UUID id, UUID userId) {
        NotificationEntity entity = notificationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));

        entity.setRead(true);
        NotificationEntity saved = notificationRepository.save(entity);
        return toDto(saved);
    }

    @Transactional
    public int markAllAsRead(UUID userId) {
        return notificationRepository.markAllAsReadForUser(userId, Instant.now());
    }

    @Transactional
    public void deleteNotification(UUID id, UUID userId) {
        NotificationEntity entity = notificationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));
        notificationRepository.delete(entity);
    }

    @Transactional
    public void clearAllRead(UUID userId) {
        notificationRepository.deleteAllReadByUserId(userId);
    }

    @Transactional
    public NotificationPreferenceDto getPreferences(UUID userId) {
        NotificationPreferenceEntity prefs = preferenceRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserEntity user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
                    NotificationPreferenceEntity defaultPrefs = new NotificationPreferenceEntity(user);
                    return preferenceRepository.save(defaultPrefs);
                });
        return toPreferenceDto(prefs);
    }

    @Transactional
    public NotificationPreferenceDto updatePreferences(UUID userId, NotificationPreferenceDto dto) {
        NotificationPreferenceEntity prefs = preferenceRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserEntity user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
                    return new NotificationPreferenceEntity(user);
                });

        prefs.setInAppEnabled(dto.isInAppEnabled());
        prefs.setEmailEnabled(dto.isEmailEnabled());
        prefs.setPushEnabled(dto.isPushEnabled());
        prefs.setLoanEmiAlerts(dto.isLoanEmiAlerts());
        prefs.setInsuranceRenewalAlerts(dto.isInsuranceRenewalAlerts());
        prefs.setBudgetThresholdAlerts(dto.isBudgetThresholdAlerts());
        prefs.setSavingsGoalAlerts(dto.isSavingsGoalAlerts());
        prefs.setJourneyIncompleteAlerts(dto.isJourneyIncompleteAlerts());
        prefs.setDocumentMissingAlerts(dto.isDocumentMissingAlerts());
        prefs.setReminderDaysBeforeEmi(dto.getReminderDaysBeforeEmi());
        prefs.setReminderDaysBeforeRenewal(dto.getReminderDaysBeforeRenewal());
        prefs.setQuietHoursEnabled(dto.isQuietHoursEnabled());
        prefs.setQuietHoursStart(dto.getQuietHoursStart());
        prefs.setQuietHoursEnd(dto.getQuietHoursEnd());
        prefs.setEmailConsentGranted(dto.isEmailConsentGranted());
        prefs.setPushConsentGranted(dto.isPushConsentGranted());
        if (dto.getTimeZone() != null && !dto.getTimeZone().isBlank()) {
            prefs.setTimeZone(dto.getTimeZone());
        }

        NotificationPreferenceEntity saved = preferenceRepository.save(prefs);
        return toPreferenceDto(saved);
    }

    public NotificationDto toDto(NotificationEntity entity) {
        return new NotificationDto(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getType(),
                entity.getCategory(),
                entity.getPriority(),
                entity.getActionUrl(),
                entity.getActionLabel(),
                entity.isRead(),
                entity.getReadAt(),
                entity.getDeduplicationKey(),
                entity.getChannelDeliveryJson(),
                entity.getMetadataJson(),
                entity.getCreatedAt()
        );
    }

    public NotificationPreferenceDto toPreferenceDto(NotificationPreferenceEntity entity) {
        return new NotificationPreferenceDto(
                entity.isInAppEnabled(),
                entity.isEmailEnabled(),
                entity.isPushEnabled(),
                entity.isLoanEmiAlerts(),
                entity.isInsuranceRenewalAlerts(),
                entity.isBudgetThresholdAlerts(),
                entity.isSavingsGoalAlerts(),
                entity.isJourneyIncompleteAlerts(),
                entity.isDocumentMissingAlerts(),
                entity.getReminderDaysBeforeEmi(),
                entity.getReminderDaysBeforeRenewal(),
                entity.isQuietHoursEnabled(),
                entity.getQuietHoursStart(),
                entity.getQuietHoursEnd(),
                entity.isEmailConsentGranted(),
                entity.isPushConsentGranted(),
                entity.getTimeZone(),
                entity.getUpdatedAt()
        );
    }
}
