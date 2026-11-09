package com.finpath.modules.ai.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatQueryResponse {
    private UUID messageId;
    private UUID sessionId;
    private String senderRole;
    private String content;
    private List<ChatMessageDto.ToolInvocationDto> toolInvocations = new ArrayList<>();
    private List<ChatMessageDto.CitationDto> citations = new ArrayList<>();

    // Phase 7: Orchestrator Context and Multi-Step Fields
    private String intent;
    private AiJourneyStageDto journeyStage;
    private List<AiRecommendationCardDto> cards = new ArrayList<>();
    private List<AiInteractiveActionDto> suggestedActions = new ArrayList<>();
    private boolean requiresUserInput = false;
    private List<String> missingFields = new ArrayList<>();

    // Upgrade Phase 03: Advanced Orchestration, Tool Calling & Explainability
    private AiExplainabilityDto explainability;
    private Double confidenceScore = 1.0;
    private boolean clarificationNeeded = false;
    private List<String> clarificationOptions = new ArrayList<>();
    private Long latencyMs = 0L;
    private Integer tokensUsed = 350;

    public ChatQueryResponse() {}

    public ChatQueryResponse(UUID messageId, UUID sessionId, String senderRole, String content,
                             List<ChatMessageDto.ToolInvocationDto> toolInvocations,
                             List<ChatMessageDto.CitationDto> citations) {
        this.messageId = messageId;
        this.sessionId = sessionId;
        this.senderRole = senderRole;
        this.content = content;
        this.toolInvocations = toolInvocations != null ? toolInvocations : new ArrayList<>();
        this.citations = citations != null ? citations : new ArrayList<>();
    }

    public UUID getMessageId() {
        return messageId;
    }

    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
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

    public String getReply() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<ChatMessageDto.ToolInvocationDto> getToolInvocations() {
        return toolInvocations;
    }

    public List<ChatMessageDto.ToolInvocationDto> getToolExecutions() {
        return toolInvocations;
    }

    public void setToolInvocations(List<ChatMessageDto.ToolInvocationDto> toolInvocations) {
        this.toolInvocations = toolInvocations;
    }

    public List<ChatMessageDto.CitationDto> getCitations() {
        return citations;
    }

    public List<ChatMessageDto.CitationDto> getRagCitations() {
        return citations;
    }

    public void setCitations(List<ChatMessageDto.CitationDto> citations) {
        this.citations = citations;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public AiJourneyStageDto getJourneyStage() {
        return journeyStage;
    }

    public void setJourneyStage(AiJourneyStageDto journeyStage) {
        this.journeyStage = journeyStage;
    }

    public List<AiRecommendationCardDto> getCards() {
        return cards;
    }

    public void setCards(List<AiRecommendationCardDto> cards) {
        this.cards = cards;
    }

    public List<AiInteractiveActionDto> getSuggestedActions() {
        return suggestedActions;
    }

    public void setSuggestedActions(List<AiInteractiveActionDto> suggestedActions) {
        this.suggestedActions = suggestedActions;
    }

    public boolean isRequiresUserInput() {
        return requiresUserInput;
    }

    public void setRequiresUserInput(boolean requiresUserInput) {
        this.requiresUserInput = requiresUserInput;
    }

    public List<String> getMissingFields() {
        return missingFields;
    }

    public void setMissingFields(List<String> missingFields) {
        this.missingFields = missingFields;
    }

    public AiExplainabilityDto getExplainability() {
        return explainability;
    }

    public void setExplainability(AiExplainabilityDto explainability) {
        this.explainability = explainability;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public boolean isClarificationNeeded() {
        return clarificationNeeded;
    }

    public void setClarificationNeeded(boolean clarificationNeeded) {
        this.clarificationNeeded = clarificationNeeded;
    }

    public List<String> getClarificationOptions() {
        return clarificationOptions;
    }

    public void setClarificationOptions(List<String> clarificationOptions) {
        this.clarificationOptions = clarificationOptions;
    }

    public Long getLatencyMs() {
        return latencyMs;
    }

    public void setLatencyMs(Long latencyMs) {
        this.latencyMs = latencyMs;
    }

    public Integer getTokensUsed() {
        return tokensUsed;
    }

    public void setTokensUsed(Integer tokensUsed) {
        this.tokensUsed = tokensUsed;
    }
}
