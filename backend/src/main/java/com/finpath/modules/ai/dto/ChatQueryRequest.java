package com.finpath.modules.ai.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public class ChatQueryRequest {

    private UUID sessionId;

    @NotBlank(message = "Query message cannot be blank")
    private String message;

    private boolean includeDomainData = true;

    public ChatQueryRequest() {}

    public ChatQueryRequest(String message) {
        this((UUID) null, message, true);
    }

    public ChatQueryRequest(UUID sessionId, String message, boolean includeDomainData) {
        this.sessionId = sessionId;
        this.message = message;
        this.includeDomainData = includeDomainData;
    }

    public ChatQueryRequest(String sessionId, String message, boolean includeDomainData) {
        this(UUID.fromString(sessionId), message, includeDomainData);
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIncludeDomainData() {
        return includeDomainData;
    }

    public void setIncludeDomainData(boolean includeDomainData) {
        this.includeDomainData = includeDomainData;
    }
}
