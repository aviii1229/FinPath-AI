package com.finpath.modules.ai.dto;

import java.time.Instant;
import java.util.UUID;

public class DocumentUploadDto {
    private UUID id;
    private String fileName;
    private long fileSizeBytes;
    private String documentCategory;
    private String processingStatus;
    private String extractedSummary;
    private Instant createdAt;

    public DocumentUploadDto() {}

    public DocumentUploadDto(UUID id, String fileName, long fileSizeBytes, String documentCategory, String processingStatus, String extractedSummary, Instant createdAt) {
        this.id = id;
        this.fileName = fileName;
        this.fileSizeBytes = fileSizeBytes;
        this.documentCategory = documentCategory;
        this.processingStatus = processingStatus;
        this.extractedSummary = extractedSummary;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public void setFileSizeBytes(long fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }

    public String getDocumentCategory() {
        return documentCategory;
    }

    public void setDocumentCategory(String documentCategory) {
        this.documentCategory = documentCategory;
    }

    public String getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(String processingStatus) {
        this.processingStatus = processingStatus;
    }

    public String getExtractedSummary() {
        return extractedSummary;
    }

    public void setExtractedSummary(String extractedSummary) {
        this.extractedSummary = extractedSummary;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}

