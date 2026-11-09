package com.finpath.modules.documents.controller;

import com.finpath.core.dto.ApiResponse;
import com.finpath.modules.documents.dto.*;
import com.finpath.modules.documents.service.DocumentCompletenessService;
import com.finpath.modules.documents.service.DocumentVaultService;
import com.finpath.security.model.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@Tag(name = "Document Vault & Intelligence", description = "Unified AI-Powered Financial Document Vault, Extraction & Completeness")
public class DocumentVaultController {

    private final DocumentVaultService vaultService;
    private final DocumentCompletenessService completenessService;

    public DocumentVaultController(DocumentVaultService vaultService, DocumentCompletenessService completenessService) {
        this.vaultService = vaultService;
        this.completenessService = completenessService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload and automatically parse, classify, and extract a financial document")
    public ResponseEntity<ApiResponse<DocumentVaultItemDto>> uploadDocument(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "journey", required = false) String journey) {
        DocumentVaultItemDto dto = vaultService.uploadDocument(user.getId(), file, journey);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Document uploaded and indexed successfully", dto));
    }

    @GetMapping
    @Operation(summary = "Search and list all documents in user's vault with optional category or status filter")
    public ResponseEntity<ApiResponse<List<DocumentVaultItemDto>>> getDocuments(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "search", required = false) String search) {
        List<DocumentVaultItemDto> items = vaultService.getVaultItems(user.getId(), category, status, search);
        return ResponseEntity.ok(ApiResponse.success(items));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detailed metadata, extracted parameters, and audit status of a specific document")
    public ResponseEntity<ApiResponse<DocumentVaultItemDto>> getDocument(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID id) {
        DocumentVaultItemDto item = vaultService.getDocument(user.getId(), id);
        return ResponseEntity.ok(ApiResponse.success(item));
    }

    @PatchMapping("/{id}/category")
    @Operation(summary = "Manually correct the category classification of a document")
    public ResponseEntity<ApiResponse<DocumentVaultItemDto>> correctCategory(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID id,
            @Valid @RequestBody CategoryCorrectionRequest request) {
        DocumentVaultItemDto updated = vaultService.correctCategory(user.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Permanently remove a document and its stored files from user's vault")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID id) {
        vaultService.deleteDocument(user.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Document deleted successfully", null));
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "Download original document binary/PDF stream")
    public ResponseEntity<Resource> downloadDocument(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID id) {
        DocumentVaultService.DownloadResult result = vaultService.downloadDocument(user.getId(), id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(result.mimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + result.fileName() + "\"")
                .body(result.resource());
    }

    @PostMapping("/{id}/query")
    @Operation(summary = "Ask grounded questions about a specific document with citations and clause references")
    public ResponseEntity<ApiResponse<DocumentQueryResponse>> queryDocument(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID id,
            @Valid @RequestBody DocumentQueryRequest request) {
        DocumentQueryResponse response = vaultService.queryDocument(user.getId(), id, request.getQuestion());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/completeness/{journeyType}")
    @Operation(summary = "Evaluate checklist completeness for a specific financial journey (e.g., HOME_LOAN, PERSONAL_LOAN)")
    public ResponseEntity<ApiResponse<JourneyCompletenessDto>> getCompleteness(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable String journeyType) {
        JourneyCompletenessDto dto = completenessService.evaluateCompleteness(user.getId(), journeyType);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping("/stats")
    @Operation(summary = "Retrieve aggregate statistics for user's document vault")
    public ResponseEntity<ApiResponse<VaultSummaryStatsDto>> getVaultStats(
            @AuthenticationPrincipal UserPrincipal user) {
        VaultSummaryStatsDto stats = vaultService.getVaultStats(user.getId());
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}

