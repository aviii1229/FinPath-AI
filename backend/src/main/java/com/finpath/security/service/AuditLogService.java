package com.finpath.security.service;

import com.finpath.security.entity.AuditLogEntity;
import com.finpath.security.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class AuditLogService {

    private static final Logger log = LoggerFactory.getLogger(AuditLogService.class);

    private static final Pattern JWT_PATTERN = Pattern.compile("eyJ[a-zA-Z0-9_-]+\\.[a-zA-Z0-9_-]+\\.[a-zA-Z0-9_-]+");
    private static final Pattern PAN_PATTERN = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]");
    private static final Pattern AADHAAR_PATTERN = Pattern.compile("\\b\\d{4}\\s?\\d{4}\\s?\\d{4}\\b");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("(?i)(\"password\"\\s*:\\s*\")[^\"]+(\")");

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Async
    public void logSecurityEvent(UUID userId, String action, String resourceType, String resourceId,
                                 String ipAddress, String userAgent, String rawDetails) {
        try {
            String sanitized = sanitizeDetails(rawDetails);
            AuditLogEntity audit = new AuditLogEntity(
                    userId,
                    action,
                    resourceType != null ? resourceType : "SECURITY",
                    resourceId != null ? resourceId : "N/A",
                    ipAddress != null ? ipAddress : "127.0.0.1",
                    userAgent != null ? (userAgent.length() > 250 ? userAgent.substring(0, 250) : userAgent) : "API_CLIENT",
                    sanitized
            );
            auditLogRepository.save(audit);
            log.info("Security Audit Logged: [Action: {}] [User: {}] [Resource: {}]", action, userId, resourceId);
        } catch (Exception ex) {
            log.error("Failed to persist security audit event: {}", ex.getMessage());
        }
    }

    public String sanitizeDetails(String text) {
        if (text == null) return null;
        String result = text;
        result = PASSWORD_PATTERN.matcher(result).replaceAll("$1[REDACTED]$2");
        result = JWT_PATTERN.matcher(result).replaceAll("[REDACTED_JWT_TOKEN]");
        result = AADHAAR_PATTERN.matcher(result).replaceAll("[REDACTED_AADHAAR]");
        result = PAN_PATTERN.matcher(result).replaceAll("[REDACTED_PAN]");
        return result;
    }
}
