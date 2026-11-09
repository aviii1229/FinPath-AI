package com.finpath.modules.documents.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DocumentVaultItemDto {

    private UUID id;
    private String fileName;
    private long fileSizeBytes;
    private String formattedSize;
    private String mimeType;
    private String documentCategory;
    private String categoryLabel;
    private String processingStatus;
    private BigDecimal classificationConfidence;
    private boolean userCorrected;
    private String sourceJourney;
    private LocalDate documentDate;
    private LocalDate expiryDate;
    private String extractedSummary;
    private List<ExtractedEntityDto> extractedEntities = new ArrayList<>();
    private Instant createdAt;
    private Instant updatedAt;

    public DocumentVaultItemDto() {}

    public static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        char pre = "KMGTPE".charAt(exp - 1);
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    public static String getHumanLabel(String category) {
        if (category == null) return "Other Financial Document";
        return switch (category.toUpperCase()) {
            case "INSURANCE_POLICY" -> "Insurance Policy & Schedule";
            case "LOAN_AGREEMENT" -> "Loan Agreement / Sanction";
            case "BANK_STATEMENT" -> "Bank Account Statement";
            case "SALARY_SLIP" -> "Salary Slip / Payslip";
            case "TAX_DOCUMENT" -> "Tax Return / Form 16 / ITR";
            case "IDENTITY_PROOF" -> "Identity Proof (PAN/Aadhaar)";
            case "ADDRESS_PROOF" -> "Address Proof (Utility/Rent)";
            default -> "Financial Document";
        };
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public long getFileSizeBytes() { return fileSizeBytes; }
    public void setFileSizeBytes(long fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
        this.formattedSize = formatBytes(fileSizeBytes);
    }

    public String getFormattedSize() { return formattedSize; }
    public void setFormattedSize(String formattedSize) { this.formattedSize = formattedSize; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public String getDocumentCategory() { return documentCategory; }
    public void setDocumentCategory(String documentCategory) {
        this.documentCategory = documentCategory;
        this.categoryLabel = getHumanLabel(documentCategory);
    }

    public String getCategoryLabel() { return categoryLabel; }
    public void setCategoryLabel(String categoryLabel) { this.categoryLabel = categoryLabel; }

    public String getProcessingStatus() { return processingStatus; }
    public void setProcessingStatus(String processingStatus) { this.processingStatus = processingStatus; }

    public BigDecimal getClassificationConfidence() { return classificationConfidence; }
    public void setClassificationConfidence(BigDecimal classificationConfidence) { this.classificationConfidence = classificationConfidence; }

    public boolean isUserCorrected() { return userCorrected; }
    public void setUserCorrected(boolean userCorrected) { this.userCorrected = userCorrected; }

    public String getSourceJourney() { return sourceJourney; }
    public void setSourceJourney(String sourceJourney) { this.sourceJourney = sourceJourney; }

    public LocalDate getDocumentDate() { return documentDate; }
    public void setDocumentDate(LocalDate documentDate) { this.documentDate = documentDate; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public String getExtractedSummary() { return extractedSummary; }
    public void setExtractedSummary(String extractedSummary) { this.extractedSummary = extractedSummary; }

    public List<ExtractedEntityDto> getExtractedEntities() { return extractedEntities; }
    public void setExtractedEntities(List<ExtractedEntityDto> extractedEntities) { this.extractedEntities = extractedEntities; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}

