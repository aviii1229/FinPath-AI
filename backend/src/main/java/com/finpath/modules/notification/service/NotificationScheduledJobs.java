package com.finpath.modules.notification.service;

import com.finpath.security.entity.UserEntity;
import com.finpath.security.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "finpath.notifications.scheduling.enabled", havingValue = "true", matchIfMissing = true)
public class NotificationScheduledJobs {

    private static final Logger log = LoggerFactory.getLogger(NotificationScheduledJobs.class);

    private final UserRepository userRepository;
    private final ProactiveNotificationService proactiveNotificationService;

    public NotificationScheduledJobs(UserRepository userRepository,
                                     ProactiveNotificationService proactiveNotificationService) {
        this.userRepository = userRepository;
        this.proactiveNotificationService = proactiveNotificationService;
    }

    /**
     * Daily morning proactive financial scanning job (defaults to 08:00 AM IST daily).
     */
    @Scheduled(cron = "${finpath.notifications.cron:0 0 8 * * *}")
    public void runDailyProactiveScan() {
        log.info("Starting daily scheduled proactive financial assistance scan...");
        List<UserEntity> users = userRepository.findAll();
        int totalEvaluated = 0;

        for (UserEntity user : users) {
            try {
                proactiveNotificationService.evaluateProactiveNotifications(user.getId());
                totalEvaluated++;
            } catch (Exception e) {
                log.error("Error during scheduled proactive scan for user {}: {}", user.getId(), e.getMessage());
            }
        }

        log.info("Daily proactive financial assistance scan completed for {} users.", totalEvaluated);
    }
}
