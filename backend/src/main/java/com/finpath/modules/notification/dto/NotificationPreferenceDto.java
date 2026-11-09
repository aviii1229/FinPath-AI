package com.finpath.modules.notification.dto;

import java.time.Instant;

public class NotificationPreferenceDto {

    private boolean inAppEnabled = true;
    private boolean emailEnabled = true;
    private boolean pushEnabled = false;

    private boolean loanEmiAlerts = true;
    private boolean insuranceRenewalAlerts = true;
    private boolean budgetThresholdAlerts = true;
    private boolean savingsGoalAlerts = true;
    private boolean journeyIncompleteAlerts = true;
    private boolean documentMissingAlerts = true;

    private int reminderDaysBeforeEmi = 3;
    private int reminderDaysBeforeRenewal = 15;

    private boolean quietHoursEnabled = false;
    private String quietHoursStart = "22:00";
    private String quietHoursEnd = "08:00";

    private boolean emailConsentGranted = true;
    private boolean pushConsentGranted = false;
    private String timeZone = "Asia/Kolkata";

    private Instant updatedAt;

    public NotificationPreferenceDto() {}

    public NotificationPreferenceDto(boolean inAppEnabled, boolean emailEnabled, boolean pushEnabled,
                                   boolean loanEmiAlerts, boolean insuranceRenewalAlerts,
                                   boolean budgetThresholdAlerts, boolean savingsGoalAlerts,
                                   boolean journeyIncompleteAlerts, boolean documentMissingAlerts,
                                   int reminderDaysBeforeEmi, int reminderDaysBeforeRenewal,
                                   boolean quietHoursEnabled, String quietHoursStart, String quietHoursEnd,
                                   boolean emailConsentGranted, boolean pushConsentGranted, String timeZone,
                                   Instant updatedAt) {
        this.inAppEnabled = inAppEnabled;
        this.emailEnabled = emailEnabled;
        this.pushEnabled = pushEnabled;
        this.loanEmiAlerts = loanEmiAlerts;
        this.insuranceRenewalAlerts = insuranceRenewalAlerts;
        this.budgetThresholdAlerts = budgetThresholdAlerts;
        this.savingsGoalAlerts = savingsGoalAlerts;
        this.journeyIncompleteAlerts = journeyIncompleteAlerts;
        this.documentMissingAlerts = documentMissingAlerts;
        this.reminderDaysBeforeEmi = reminderDaysBeforeEmi;
        this.reminderDaysBeforeRenewal = reminderDaysBeforeRenewal;
        this.quietHoursEnabled = quietHoursEnabled;
        this.quietHoursStart = quietHoursStart;
        this.quietHoursEnd = quietHoursEnd;
        this.emailConsentGranted = emailConsentGranted;
        this.pushConsentGranted = pushConsentGranted;
        this.timeZone = timeZone;
        this.updatedAt = updatedAt;
    }

    public boolean isInAppEnabled() {
        return inAppEnabled;
    }

    public void setInAppEnabled(boolean inAppEnabled) {
        this.inAppEnabled = inAppEnabled;
    }

    public boolean isEmailEnabled() {
        return emailEnabled;
    }

    public void setEmailEnabled(boolean emailEnabled) {
        this.emailEnabled = emailEnabled;
    }

    public boolean isPushEnabled() {
        return pushEnabled;
    }

    public void setPushEnabled(boolean pushEnabled) {
        this.pushEnabled = pushEnabled;
    }

    public boolean isLoanEmiAlerts() {
        return loanEmiAlerts;
    }

    public void setLoanEmiAlerts(boolean loanEmiAlerts) {
        this.loanEmiAlerts = loanEmiAlerts;
    }

    public boolean isInsuranceRenewalAlerts() {
        return insuranceRenewalAlerts;
    }

    public void setInsuranceRenewalAlerts(boolean insuranceRenewalAlerts) {
        this.insuranceRenewalAlerts = insuranceRenewalAlerts;
    }

    public boolean isBudgetThresholdAlerts() {
        return budgetThresholdAlerts;
    }

    public void setBudgetThresholdAlerts(boolean budgetThresholdAlerts) {
        this.budgetThresholdAlerts = budgetThresholdAlerts;
    }

    public boolean isSavingsGoalAlerts() {
        return savingsGoalAlerts;
    }

    public void setSavingsGoalAlerts(boolean savingsGoalAlerts) {
        this.savingsGoalAlerts = savingsGoalAlerts;
    }

    public boolean isJourneyIncompleteAlerts() {
        return journeyIncompleteAlerts;
    }

    public void setJourneyIncompleteAlerts(boolean journeyIncompleteAlerts) {
        this.journeyIncompleteAlerts = journeyIncompleteAlerts;
    }

    public boolean isDocumentMissingAlerts() {
        return documentMissingAlerts;
    }

    public void setDocumentMissingAlerts(boolean documentMissingAlerts) {
        this.documentMissingAlerts = documentMissingAlerts;
    }

    public int getReminderDaysBeforeEmi() {
        return reminderDaysBeforeEmi;
    }

    public void setReminderDaysBeforeEmi(int reminderDaysBeforeEmi) {
        this.reminderDaysBeforeEmi = reminderDaysBeforeEmi;
    }

    public int getReminderDaysBeforeRenewal() {
        return reminderDaysBeforeRenewal;
    }

    public void setReminderDaysBeforeRenewal(int reminderDaysBeforeRenewal) {
        this.reminderDaysBeforeRenewal = reminderDaysBeforeRenewal;
    }

    public boolean isQuietHoursEnabled() {
        return quietHoursEnabled;
    }

    public void setQuietHoursEnabled(boolean quietHoursEnabled) {
        this.quietHoursEnabled = quietHoursEnabled;
    }

    public String getQuietHoursStart() {
        return quietHoursStart;
    }

    public void setQuietHoursStart(String quietHoursStart) {
        this.quietHoursStart = quietHoursStart;
    }

    public String getQuietHoursEnd() {
        return quietHoursEnd;
    }

    public void setQuietHoursEnd(String quietHoursEnd) {
        this.quietHoursEnd = quietHoursEnd;
    }

    public boolean isEmailConsentGranted() {
        return emailConsentGranted;
    }

    public void setEmailConsentGranted(boolean emailConsentGranted) {
        this.emailConsentGranted = emailConsentGranted;
    }

    public boolean isPushConsentGranted() {
        return pushConsentGranted;
    }

    public void setPushConsentGranted(boolean pushConsentGranted) {
        this.pushConsentGranted = pushConsentGranted;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
