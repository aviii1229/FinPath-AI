package com.finpath.security.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_privacy_consents")
@EntityListeners(AuditingEntityListener.class)
public class UserPrivacyConsentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @Column(name = "data_collection_consent", nullable = false)
    private boolean dataCollectionConsent = true;

    @Column(name = "ai_processing_consent", nullable = false)
    private boolean aiProcessingConsent = true;

    @Column(name = "marketing_communication_consent", nullable = false)
    private boolean marketingCommunicationConsent = false;

    @Column(name = "third_party_sharing_consent", nullable = false)
    private boolean thirdPartySharingConsent = false;

    @Column(name = "document_ocr_storage_consent", nullable = false)
    private boolean documentOcrStorageConsent = true;

    @Column(name = "store_chat_history", nullable = false)
    private boolean storeChatHistory = true;

    @Column(name = "consent_version", nullable = false, length = 20)
    private String consentVersion = "v2026.1";

    @CreatedDate
    @Column(name = "consent_timestamp", nullable = false, updatable = false)
    private Instant consentTimestamp;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public UserPrivacyConsentEntity() {}

    public UserPrivacyConsentEntity(UserEntity user) {
        this.user = user;
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

    public boolean isDataCollectionConsent() {
        return dataCollectionConsent;
    }

    public void setDataCollectionConsent(boolean dataCollectionConsent) {
        this.dataCollectionConsent = dataCollectionConsent;
    }

    public boolean isAiProcessingConsent() {
        return aiProcessingConsent;
    }

    public void setAiProcessingConsent(boolean aiProcessingConsent) {
        this.aiProcessingConsent = aiProcessingConsent;
    }

    public boolean isMarketingCommunicationConsent() {
        return marketingCommunicationConsent;
    }

    public void setMarketingCommunicationConsent(boolean marketingCommunicationConsent) {
        this.marketingCommunicationConsent = marketingCommunicationConsent;
    }

    public boolean isThirdPartySharingConsent() {
        return thirdPartySharingConsent;
    }

    public void setThirdPartySharingConsent(boolean thirdPartySharingConsent) {
        this.thirdPartySharingConsent = thirdPartySharingConsent;
    }

    public boolean isDocumentOcrStorageConsent() {
        return documentOcrStorageConsent;
    }

    public void setDocumentOcrStorageConsent(boolean documentOcrStorageConsent) {
        this.documentOcrStorageConsent = documentOcrStorageConsent;
    }

    public boolean isStoreChatHistory() {
        return storeChatHistory;
    }

    public void setStoreChatHistory(boolean storeChatHistory) {
        this.storeChatHistory = storeChatHistory;
    }

    public String getConsentVersion() {
        return consentVersion;
    }

    public void setConsentVersion(String consentVersion) {
        this.consentVersion = consentVersion;
    }

    public Instant getConsentTimestamp() {
        return consentTimestamp;
    }

    public void setConsentTimestamp(Instant consentTimestamp) {
        this.consentTimestamp = consentTimestamp;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
