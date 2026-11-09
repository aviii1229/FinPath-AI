package com.finpath.modules.insurance.dto;

import java.util.List;
import java.util.UUID;

public class DocumentExtractionResponse {

    private UUID documentId;
    private String fileName;
    private long fileSizeBytes;
    private String mimeType;
    private int chunkCount;
    private String extractedSummary;
    private List<String> extractedSections;

    public DocumentExtractionResponse() {}

    public DocumentExtractionResponse(UUID documentId, String fileName, long fileSizeBytes, String mimeType, int chunkCount, String extractedSummary, List<String> extractedSections) {
        this.documentId = documentId;
        this.fileName = fileName;
        this.fileSizeBytes = fileSizeBytes;
        this.mimeType = mimeType;
        this.chunkCount = chunkCount;
        this.extractedSummary = extractedSummary;
        this.extractedSections = extractedSections;
    }

    public UUID getDocumentId() {
        return documentId;
    }

    public void setDocumentId(UUID documentId) {
        this.documentId = documentId;
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

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public int getChunkCount() {
        return chunkCount;
    }

    public void setChunkCount(int chunkCount) {
        this.chunkCount = chunkCount;
    }

    public String getExtractedSummary() {
        return extractedSummary;
    }

    public void setExtractedSummary(String extractedSummary) {
        this.extractedSummary = extractedSummary;
    }

    public List<String> getExtractedSections() {
        return extractedSections;
    }

    public void setExtractedSections(List<String> extractedSections) {
        this.extractedSections = extractedSections;
    }
}

