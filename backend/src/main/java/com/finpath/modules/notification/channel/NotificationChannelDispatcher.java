package com.finpath.modules.notification.channel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finpath.modules.notification.entity.NotificationEntity;
import com.finpath.modules.notification.entity.NotificationPreferenceEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationChannelDispatcher {

    private static final Logger log = LoggerFactory.getLogger(NotificationChannelDispatcher.class);
    private final ObjectMapper objectMapper;

    public NotificationChannelDispatcher(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Dispatches notification across configured channels (In-App, Email, Browser Push)
     * enforcing privacy, consent, and quiet hours.
     */
    public String dispatch(NotificationEntity notification, NotificationPreferenceEntity prefs, Instant eventTime) {
        List<ChannelDeliveryResult> results = new ArrayList<>();
        Map<String, String> deliverySummary = new LinkedHashMap<>();

        boolean inQuietHours = isQuietHours(prefs, eventTime);

        // 1. In-App Channel (Database persistence is the primary medium)
        if (prefs == null || prefs.isInAppEnabled()) {
            results.add(new ChannelDeliveryResult("IN_APP", "DELIVERED", "Rendered to In-App Notification Center"));
            deliverySummary.put("inApp", "DELIVERED");
        } else {
            results.add(new ChannelDeliveryResult("IN_APP", "DISABLED", "In-App notifications disabled in preferences"));
            deliverySummary.put("inApp", "DISABLED");
        }

        // 2. Email Channel
        if (prefs == null || !prefs.isEmailEnabled()) {
            results.add(new ChannelDeliveryResult("EMAIL", "DISABLED", "Email alerts disabled in user preferences"));
            deliverySummary.put("email", "DISABLED");
        } else if (!prefs.isEmailConsentGranted()) {
            results.add(new ChannelDeliveryResult("EMAIL", "NO_CONSENT", "User consent not granted for email notifications"));
            deliverySummary.put("email", "NO_CONSENT");
        } else if (inQuietHours) {
            results.add(new ChannelDeliveryResult("EMAIL", "SUPPRESSED_QUIET_HOURS", "Suppressed during quiet hours (" + prefs.getQuietHoursStart() + " - " + prefs.getQuietHoursEnd() + ")"));
            deliverySummary.put("email", "SUPPRESSED_QUIET_HOURS");
        } else {
            // Deliver privacy-compliant email
            String safeSubject = getPrivacySafeSubject(notification.getCategory());
            log.info("Dispatching email notification [To: User {}] [Safe Subject: '{}'] [Category: {}]",
                    notification.getUser().getId(), safeSubject, notification.getCategory());
            results.add(new ChannelDeliveryResult("EMAIL", "DELIVERED", "Simulated email sent with privacy-safe subject: " + safeSubject));
            deliverySummary.put("email", "DELIVERED");
        }

        // 3. Browser Push Channel
        if (prefs == null || !prefs.isPushEnabled()) {
            results.add(new ChannelDeliveryResult("PUSH", "DISABLED", "Browser push disabled in user preferences"));
            deliverySummary.put("push", "DISABLED");
        } else if (!prefs.isPushConsentGranted()) {
            results.add(new ChannelDeliveryResult("PUSH", "NO_CONSENT", "Browser push permission/consent not granted"));
            deliverySummary.put("push", "NO_CONSENT");
        } else if (inQuietHours) {
            results.add(new ChannelDeliveryResult("PUSH", "SUPPRESSED_QUIET_HOURS", "Suppressed during quiet hours"));
            deliverySummary.put("push", "SUPPRESSED_QUIET_HOURS");
        } else {
            // Deliver privacy-compliant push
            String safeTitle = "FinPath AI: Financial Reminder";
            log.info("Dispatching browser push [To: User {}] [Title: '{}'] [Category: {}]",
                    notification.getUser().getId(), safeTitle, notification.getCategory());
            results.add(new ChannelDeliveryResult("PUSH", "DELIVERED", "Web push sent"));
            deliverySummary.put("push", "DELIVERED");
        }

        try {
            return objectMapper.writeValueAsString(deliverySummary);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize delivery summary", e);
            return "{\"inApp\":\"DELIVERED\"}";
        }
    }

    /**
     * Determines whether current time in user's time zone is inside quiet hours.
     */
    public boolean isQuietHours(NotificationPreferenceEntity prefs, Instant nowInstant) {
        if (prefs == null || !prefs.isQuietHoursEnabled()) {
            return false;
        }

        try {
            String tz = (prefs.getTimeZone() != null && !prefs.getTimeZone().isBlank()) ? prefs.getTimeZone() : "Asia/Kolkata";
            ZoneId zoneId = ZoneId.of(tz);
            ZonedDateTime zdt = nowInstant.atZone(zoneId);
            LocalTime currentLocal = zdt.toLocalTime();

            LocalTime start = LocalTime.parse(prefs.getQuietHoursStart() != null ? prefs.getQuietHoursStart() : "22:00");
            LocalTime end = LocalTime.parse(prefs.getQuietHoursEnd() != null ? prefs.getQuietHoursEnd() : "08:00");

            if (start.isBefore(end)) {
                return (currentLocal.isAfter(start) || currentLocal.equals(start)) && currentLocal.isBefore(end);
            } else {
                // Overnight quiet hours, e.g. 22:00 to 08:00
                return currentLocal.isAfter(start) || currentLocal.equals(start) || currentLocal.isBefore(end);
            }
        } catch (Exception e) {
            log.warn("Error calculating quiet hours for zone: " + prefs.getTimeZone(), e);
            return false;
        }
    }

    /**
     * Enforces privacy: never exposes sensitive amounts, PAN, or account identifiers in external subjects.
     */
    public String getPrivacySafeSubject(String category) {
        if (category == null) return "FinPath AI: Account Update";
        return switch (category.toUpperCase()) {
            case "LOAN_EMI" -> "FinPath AI: Upcoming Payment Due";
            case "INSURANCE_RENEWAL" -> "FinPath AI: Insurance Policy Notice";
            case "BUDGET_ALERT" -> "FinPath AI: Monthly Spending Update";
            case "SAVINGS_GOAL" -> "FinPath AI: Savings Plan Update";
            case "JOURNEY_INCOMPLETE" -> "FinPath AI: Action Required on Your Financial Journey";
            case "DOCUMENT_MISSING" -> "FinPath AI: Document Checklist Reminder";
            default -> "FinPath AI: Important Notification";
        };
    }
}

