package com.finpath.security.service;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginRateLimiter {

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_SECONDS = 900; // 15 minutes

    private static class AttemptRecord {
        int attempts;
        Instant firstAttemptTime;
        Instant lockedUntil;

        AttemptRecord(Instant now) {
            this.attempts = 1;
            this.firstAttemptTime = now;
            this.lockedUntil = null;
        }
    }

    private final Map<String, AttemptRecord> ipAttempts = new ConcurrentHashMap<>();

    /**
     * Checks if IP is currently blocked by rate limiting.
     */
    public boolean isIpBlocked(String ip) {
        if (ip == null) return false;
        AttemptRecord record = ipAttempts.get(ip);
        if (record == null) return false;

        Instant now = Instant.now();
        if (record.lockedUntil != null) {
            if (now.isBefore(record.lockedUntil)) {
                return true;
            } else {
                ipAttempts.remove(ip);
                return false;
            }
        }
        return false;
    }

    /**
     * Records a failed login attempt for an IP.
     */
    public void recordFailedAttempt(String ip) {
        if (ip == null) return;
        Instant now = Instant.now();
        ipAttempts.compute(ip, (k, existing) -> {
            if (existing == null || now.isAfter(existing.firstAttemptTime.plusSeconds(LOCKOUT_DURATION_SECONDS))) {
                return new AttemptRecord(now);
            }
            existing.attempts++;
            if (existing.attempts >= MAX_ATTEMPTS) {
                existing.lockedUntil = now.plusSeconds(LOCKOUT_DURATION_SECONDS);
            }
            return existing;
        });
    }

    /**
     * Resets rate limit records for an IP upon successful login.
     */
    public void resetAttempts(String ip) {
        if (ip != null) {
            ipAttempts.remove(ip);
        }
    }

    public void resetAll() {
        ipAttempts.clear();
    }
}
