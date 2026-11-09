package com.finpath.security.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class UserDataExportDto {

    private String exportId;
    private Instant exportedAt;
    private String dataSubjectEmail;
    private UserDto profile;
    private List<Map<String, Object>> transactions;
    private List<Map<String, Object>> insurancePolicies;
    private List<Map<String, Object>> loanApplications;
    private List<Map<String, Object>> customerJourneys;
    private List<Map<String, Object>> notifications;
    private PrivacyConsentDto privacyConsents;
    private String legalNotice;

    public UserDataExportDto() {}

    public String getExportId() {
        return exportId;
    }

    public void setExportId(String exportId) {
        this.exportId = exportId;
    }

    public Instant getExportAt() {
        return exportedAt;
    }

    public void setExportedAt(Instant exportedAt) {
        this.exportedAt = exportedAt;
    }

    public String getDataSubjectEmail() {
        return dataSubjectEmail;
    }

    public void setDataSubjectEmail(String dataSubjectEmail) {
        this.dataSubjectEmail = dataSubjectEmail;
    }

    public UserDto getProfile() {
        return profile;
    }

    public void setProfile(UserDto profile) {
        this.profile = profile;
    }

    public List<Map<String, Object>> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Map<String, Object>> transactions) {
        this.transactions = transactions;
    }

    public List<Map<String, Object>> getInsurancePolicies() {
        return insurancePolicies;
    }

    public void setInsurancePolicies(List<Map<String, Object>> insurancePolicies) {
        this.insurancePolicies = insurancePolicies;
    }

    public List<Map<String, Object>> getLoanApplications() {
        return loanApplications;
    }

    public void setLoanApplications(List<Map<String, Object>> loanApplications) {
        this.loanApplications = loanApplications;
    }

    public List<Map<String, Object>> getCustomerJourneys() {
        return customerJourneys;
    }

    public void setCustomerJourneys(List<Map<String, Object>> customerJourneys) {
        this.customerJourneys = customerJourneys;
    }

    public List<Map<String, Object>> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Map<String, Object>> notifications) {
        this.notifications = notifications;
    }

    public PrivacyConsentDto getPrivacyConsents() {
        return privacyConsents;
    }

    public void setPrivacyConsents(PrivacyConsentDto privacyConsents) {
        this.privacyConsents = privacyConsents;
    }

    public String getLegalNotice() {
        return legalNotice;
    }

    public void setLegalNotice(String legalNotice) {
        this.legalNotice = legalNotice;
    }
}
