package com.finpath.security.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenRevocationService {

    private static final Logger log = LoggerFactory.getLogger(TokenRevocationService.class);

    // In-memory blacklist of revoked access tokens mapped to their expiry timestamp
    private final Map<String, Instant> revokedTokens = new ConcurrentHashMap<>();

    /**
     * Revokes a JWT access token until its expiry.
     */
    public void revokeToken(String token, Instant expiry) {
        if (token == null || token.isBlank()) return;
        revokedTokens.put(token, expiry != null ? expiry : Instant.now().plusSeconds(86400));
        log.info("Token revoked and blacklisted until {}", expiry);
        cleanupExpired();
    }

    public void revokeToken(String token) {
        revokeToken(token, Instant.now().plusSeconds(86400));
    }

    /**
     * Checks if a token is in the blacklist.
     */
    public boolean isRevoked(String token) {
        if (token == null) return false;
        Instant expiry = revokedTokens.get(token);
        if (expiry == null) return false;
        if (Instant.now().isAfter(expiry)) {
            revokedTokens.remove(token);
            return false;
        }
        return true;
    }

    private void cleanupExpired() {
        if (revokedTokens.size() > 1000) {
            Instant now = Instant.now();
            revokedTokens.entrySet().removeIf(entry -> now.isAfter(entry.getValue()));
        }
    }
}
