package com.finpath.modules.ai.controller;

import com.finpath.core.dto.ApiResponse;
import com.finpath.modules.ai.dto.*;
import com.finpath.modules.ai.service.AiChatService;
import com.finpath.security.model.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ai")
@Tag(name = "AI Co-Pilot & Documents", description = "Central AI Orchestration, Chat Conversations & RAG Vector Processing")
public class AiChatController {

    private final AiChatService aiChatService;

    public AiChatController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @PostMapping("/chat/sessions")
    @Operation(summary = "Start a new conversational journey session")
    public ResponseEntity<ApiResponse<ChatSessionDto>> createSession(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam(required = false) String title) {
        ChatSessionDto session = aiChatService.createSession(user.getId(), title);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(session));
    }

    @GetMapping("/chat/sessions")
    @Operation(summary = "List all conversational sessions for the current user")
    public ResponseEntity<ApiResponse<List<ChatSessionDto>>> getSessions(
            @AuthenticationPrincipal UserPrincipal user) {
        List<ChatSessionDto> sessions = aiChatService.getUserSessions(user.getId());
        return ResponseEntity.ok(ApiResponse.success(sessions));
    }

    @GetMapping("/chat/sessions/{id}/messages")
    @Operation(summary = "Get full message turn history for a session")
    public ResponseEntity<ApiResponse<List<ChatMessageDto>>> getSessionMessages(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID id) {
        List<ChatMessageDto> messages = aiChatService.getSessionMessages(user != null ? user.getId() : null, id);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }

    @DeleteMapping("/chat/sessions/{id}")
    @Operation(summary = "Delete a specific conversational session and its turn history")
    public ResponseEntity<ApiResponse<Void>> deleteSession(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID id) {
        aiChatService.deleteSession(user.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Session deleted successfully", null));
    }

    @DeleteMapping("/chat/sessions")
    @Operation(summary = "Clear all conversational session history for the current user")
    public ResponseEntity<ApiResponse<Void>> clearAllSessions(
            @AuthenticationPrincipal UserPrincipal user) {
        aiChatService.clearAllSessions(user.getId());
        return ResponseEntity.ok(ApiResponse.success("All conversation sessions cleared", null));
    }

    @PostMapping("/chat/query")
    @Operation(summary = "Send a question to the Central AI Orchestrator")
    public ResponseEntity<ApiResponse<ChatQueryResponse>> sendQuery(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody ChatQueryRequest request) {
        ChatQueryResponse response = aiChatService.processQuery(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/chat/orchestrate")
    @Operation(summary = "Unified multi-domain orchestration connecting Insurance, Lending, and Fintech")
    public ResponseEntity<ApiResponse<ChatQueryResponse>> orchestrate(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody ChatQueryRequest request) {
        ChatQueryResponse response = aiChatService.processQuery(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/documents/upload")
    @Operation(summary = "Upload PDF/document for RAG text extraction and vector indexing")
    public ResponseEntity<ApiResponse<DocumentUploadDto>> uploadDocument(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "category", defaultValue = "INSURANCE_POLICY") String category) {

        DocumentUploadDto doc = aiChatService.registerUploadedDocument(
                user.getId(),
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "document.pdf",
                file.getSize(),
                file.getContentType() != null ? file.getContentType() : "application/pdf",
                category
        );

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(ApiResponse.success("Document accepted for vector indexing", doc));
    }

    @GetMapping("/documents")
    @Operation(summary = "List uploaded and indexed RAG documents")
    public ResponseEntity<ApiResponse<List<DocumentUploadDto>>> getDocuments(
            @AuthenticationPrincipal UserPrincipal user) {
        List<DocumentUploadDto> docs = aiChatService.getUserDocuments(user.getId());
        return ResponseEntity.ok(ApiResponse.success(docs));
    }
}

