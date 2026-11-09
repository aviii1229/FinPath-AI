package com.finpath.modules.ai.dto;

import java.time.Instant;
import java.util.UUID;

public class ChatSessionDto {
    private UUID id;
    private String title;
    private String contextIntent;
    private Instant createdAt;
    private Instant updatedAt;

    public ChatSessionDto() {}

    public ChatSessionDto(UUID id, String title, String contextIntent, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.contextIntent = contextIntent;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContextIntent() {
        return contextIntent;
    }

    public void setContextIntent(String contextIntent) {
        this.contextIntent = contextIntent;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}

