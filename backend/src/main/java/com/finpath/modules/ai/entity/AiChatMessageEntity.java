package com.finpath.modules.ai.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ai_chat_messages")
@EntityListeners(AuditingEntityListener.class)
public class AiChatMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private AiChatSessionEntity session;

    @Column(name = "sender_role", nullable = false, length = 20)
    private String senderRole; // user, assistant, system

    @Column(nullable = false, length = 4000)
    private String content;

    @Column(name = "tool_calls_summary", length = 1000)
    private String toolCallsSummary;

    @Column(name = "citations_summary", length = 1000)
    private String citationsSummary;

    @Column(name = "tokens_used")
    private Integer tokensUsed;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public AiChatMessageEntity() {}

    public AiChatMessageEntity(AiChatSessionEntity session, String senderRole, String content) {
        this.session = session;
        this.senderRole = senderRole;
        this.content = content;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public AiChatSessionEntity getSession() {
        return session;
    }

    public void setSession(AiChatSessionEntity session) {
        this.session = session;
    }

    public String getSenderRole() {
        return senderRole;
    }

    public void setSenderRole(String senderRole) {
        this.senderRole = senderRole;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getToolCallsSummary() {
        return toolCallsSummary;
    }

    public void setToolCallsSummary(String toolCallsSummary) {
        this.toolCallsSummary = toolCallsSummary;
    }

    public String getCitationsSummary() {
        return citationsSummary;
    }

    public void setCitationsSummary(String citationsSummary) {
        this.citationsSummary = citationsSummary;
    }

    public Integer getTokensUsed() {
        return tokensUsed;
    }

    public void setTokensUsed(Integer tokensUsed) {
        this.tokensUsed = tokensUsed;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}

