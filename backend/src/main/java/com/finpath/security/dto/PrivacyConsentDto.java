package com.finpath.security.dto;

import java.time.Instant;

public class PrivacyConsentDto {

    private boolean dataCollectionConsent = true;
    private boolean aiProcessingConsent = true;
    private boolean marketingCommunicationConsent = false;
    private boolean thirdPartySharingConsent = false;
    private boolean documentOcrStorageConsent = true;
    private boolean storeChatHistory = true;
    private String consentVersion = "v2026.1";
    private Instant consentTimestamp;
    private Instant updatedAt;

    public PrivacyConsentDto() {}

    public PrivacyConsentDto(boolean dataCollectionConsent, boolean aiProcessingConsent,
                             boolean marketingCommunicationConsent, boolean thirdPartySharingConsent,
                             boolean documentOcrStorageConsent, boolean storeChatHistory,
                             String consentVersion, Instant consentTimestamp, Instant updatedAt) {
        this.dataCollectionConsent = dataCollectionConsent;
        this.aiProcessingConsent = aiProcessingConsent;
        this.marketingCommunicationConsent = marketingCommunicationConsent;
        this.thirdPartySharingConsent = thirdPartySharingConsent;
        this.documentOcrStorageConsent = documentOcrStorageConsent;
        this.storeChatHistory = storeChatHistory;
        this.consentVersion = consentVersion;
        this.consentTimestamp = consentTimestamp;
        this.updatedAt = updatedAt;
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
