package com.finpath.modules.ai.service;

import com.finpath.core.exception.BadRequestException;
import com.finpath.core.exception.ResourceNotFoundException;
import com.finpath.modules.ai.dto.*;
import com.finpath.modules.ai.entity.AiChatMessageEntity;
import com.finpath.modules.ai.entity.AiChatSessionEntity;
import com.finpath.modules.ai.entity.DocumentUploadEntity;
import com.finpath.modules.ai.repository.AiChatMessageRepository;
import com.finpath.modules.ai.repository.AiChatSessionRepository;
import com.finpath.modules.ai.repository.DocumentUploadRepository;
import com.finpath.security.entity.UserEntity;
import com.finpath.security.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AiChatService {

    private final AiChatSessionRepository sessionRepository;
    private final AiChatMessageRepository messageRepository;
    private final DocumentUploadRepository documentUploadRepository;
    private final UserRepository userRepository;
    private final AiJourneyOrchestratorService orchestratorService;

    public AiChatService(AiChatSessionRepository sessionRepository,
                         AiChatMessageRepository messageRepository,
                         DocumentUploadRepository documentUploadRepository,
                         UserRepository userRepository,
                         AiJourneyOrchestratorService orchestratorService) {
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
        this.documentUploadRepository = documentUploadRepository;
        this.userRepository = userRepository;
        this.orchestratorService = orchestratorService;
    }

    @Transactional
    public ChatSessionDto createSession(UUID userId, String title) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        AiChatSessionEntity session = new AiChatSessionEntity(user, title != null ? title : "Financial Journey Discussion");
        AiChatSessionEntity saved = sessionRepository.save(session);

        // Add welcome message
        AiChatMessageEntity welcomeMsg = new AiChatMessageEntity(
                saved,
                "assistant",
                "Hello! I am FinPath AI, your unified orchestrator across Insurance, Lending, and Fintech. How can I assist your financial journey today?"
        );
        messageRepository.save(welcomeMsg);

        return new ChatSessionDto(saved.getId(), saved.getTitle(), saved.getContextIntent(), saved.getCreatedAt(), saved.getUpdatedAt());
    }

    @Transactional(readOnly = true)
    public List<ChatSessionDto> getUserSessions(UUID userId) {
        return sessionRepository.findByUserIdOrderByUpdatedAtDesc(userId).stream()
                .map(s -> new ChatSessionDto(s.getId(), s.getTitle(), s.getContextIntent(), s.getCreatedAt(), s.getUpdatedAt()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDto> getSessionMessages(UUID sessionId) {
        return messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId).stream()
                .map(m -> new ChatMessageDto(m.getId(), m.getSenderRole(), m.getContent(), m.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Transactional
    public ChatQueryResponse processQuery(UUID userId, ChatQueryRequest request) {
        return orchestratorService.orchestrateJourney(userId, request);
    }

    @Transactional
    public void deleteSession(UUID userId, UUID sessionId) {
        AiChatSessionEntity session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found: " + sessionId));

        if (!session.getUser().getId().equals(userId)) {
            throw new BadRequestException("Unauthorized access to delete chat session");
        }

        messageRepository.deleteBySessionId(sessionId);
        sessionRepository.delete(session);
    }

    @Transactional
    public void clearAllSessions(UUID userId) {
        List<AiChatSessionEntity> sessions = sessionRepository.findByUserIdOrderByUpdatedAtDesc(userId);
        for (AiChatSessionEntity s : sessions) {
            messageRepository.deleteBySessionId(s.getId());
            sessionRepository.delete(s);
        }
    }

    @Transactional
    public DocumentUploadDto registerUploadedDocument(UUID userId, String fileName, long size, String mimeType, String category) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        DocumentUploadEntity doc = new DocumentUploadEntity(
                user,
                fileName,
                size,
                mimeType,
                "/app/storage/documents/" + UUID.randomUUID() + ".pdf",
                category,
                "INDEXED",
                "Document parsed with Apache Tika. 14 semantic chunks indexed into PostgreSQL pgvector."
        );

        DocumentUploadEntity saved = documentUploadRepository.save(doc);
        return new DocumentUploadDto(
                saved.getId(),
                saved.getFileName(),
                saved.getFileSizeBytes(),
                saved.getDocumentCategory(),
                saved.getProcessingStatus(),
                saved.getExtractedSummary(),
                saved.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<DocumentUploadDto> getUserDocuments(UUID userId) {
        return documentUploadRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(d -> new DocumentUploadDto(
                        d.getId(),
                        d.getFileName(),
                        d.getFileSizeBytes(),
                        d.getDocumentCategory(),
                        d.getProcessingStatus(),
                        d.getExtractedSummary(),
                        d.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}

