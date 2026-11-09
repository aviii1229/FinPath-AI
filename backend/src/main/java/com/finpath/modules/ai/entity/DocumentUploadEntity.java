package com.finpath.modules.ai.entity;

import com.finpath.security.entity.UserEntity;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "document_uploads")
@EntityListeners(AuditingEntityListener.class)
public class DocumentUploadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "file_size_bytes", nullable = false)
    private long fileSizeBytes;

    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    @Column(name = "storage_path", nullable = false, length = 500)
    private String storagePath;

    @Column(name = "document_category", nullable = false, length = 50)
    private String documentCategory; // INSURANCE_POLICY, LOAN_AGREEMENT, BANK_STATEMENT, SALARY_SLIP, TAX_DOCUMENT, IDENTITY_PROOF, ADDRESS_PROOF, OTHER_FINANCIAL

    @Column(name = "processing_status", nullable = false, length = 30)
    private String processingStatus = "INDEXED"; // QUEUED, PROCESSING, INDEXED, FAILED

    @Column(name = "extracted_summary", length = 2000)
    private String extractedSummary;

    @Column(name = "raw_text", columnDefinition = "TEXT")
    private String rawText;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "extracted_entities_json", columnDefinition = "TEXT")
    private String extractedEntitiesJson;

    @Column(name = "classification_confidence", precision = 4, scale = 3)
    private BigDecimal classificationConfidence = BigDecimal.valueOf(0.950);

    @Column(name = "is_user_corrected")
    private boolean userCorrected = false;

    @Column(name = "source_journey", length = 50)
    private String sourceJourney = "GENERAL";

    @Column(name = "document_date")
    private LocalDate documentDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    public DocumentUploadEntity() {}

    public DocumentUploadEntity(UserEntity user, String fileName, long fileSizeBytes, String mimeType, String storagePath, String documentCategory, String processingStatus, String extractedSummary) {
        this.user = user;
        this.fileName = fileName;
        this.fileSizeBytes = fileSizeBytes;
        this.mimeType = mimeType;
        this.storagePath = storagePath;
        this.documentCategory = documentCategory;
        this.processingStatus = processingStatus;
        this.extractedSummary = extractedSummary;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
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

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
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

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getExtractedEntitiesJson() {
        return extractedEntitiesJson;
    }

    public void setExtractedEntitiesJson(String extractedEntitiesJson) {
        this.extractedEntitiesJson = extractedEntitiesJson;
    }

    public BigDecimal getClassificationConfidence() {
        return classificationConfidence;
    }

    public void setClassificationConfidence(BigDecimal classificationConfidence) {
        this.classificationConfidence = classificationConfidence;
    }

    public boolean isUserCorrected() {
        return userCorrected;
    }

    public void setUserCorrected(boolean userCorrected) {
        this.userCorrected = userCorrected;
    }

    public String getSourceJourney() {
        return sourceJourney;
    }

    public void setSourceJourney(String sourceJourney) {
        this.sourceJourney = sourceJourney;
    }

    public LocalDate getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(LocalDate documentDate) {
        this.documentDate = documentDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
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
