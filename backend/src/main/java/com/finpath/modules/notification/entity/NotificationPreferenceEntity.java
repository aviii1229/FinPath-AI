package com.finpath.modules.notification.entity;

import com.finpath.security.entity.UserEntity;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_notification_preferences")
@EntityListeners(AuditingEntityListener.class)
public class NotificationPreferenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @Column(name = "in_app_enabled", nullable = false)
    private boolean inAppEnabled = true;

    @Column(name = "email_enabled", nullable = false)
    private boolean emailEnabled = true;

    @Column(name = "push_enabled", nullable = false)
    private boolean pushEnabled = false;

    @Column(name = "loan_emi_alerts", nullable = false)
    private boolean loanEmiAlerts = true;

    @Column(name = "insurance_renewal_alerts", nullable = false)
    private boolean insuranceRenewalAlerts = true;

    @Column(name = "budget_threshold_alerts", nullable = false)
    private boolean budgetThresholdAlerts = true;

    @Column(name = "savings_goal_alerts", nullable = false)
    private boolean savingsGoalAlerts = true;

    @Column(name = "journey_incomplete_alerts", nullable = false)
    private boolean journeyIncompleteAlerts = true;

    @Column(name = "document_missing_alerts", nullable = false)
    private boolean documentMissingAlerts = true;

    @Column(name = "reminder_days_before_emi", nullable = false)
    private int reminderDaysBeforeEmi = 3;

    @Column(name = "reminder_days_before_renewal", nullable = false)
    private int reminderDaysBeforeRenewal = 15;

    @Column(name = "quiet_hours_enabled", nullable = false)
    private boolean quietHoursEnabled = false;

    @Column(name = "quiet_hours_start", length = 10)
    private String quietHoursStart = "22:00";

    @Column(name = "quiet_hours_end", length = 10)
    private String quietHoursEnd = "08:00";

    @Column(name = "email_consent_granted", nullable = false)
    private boolean emailConsentGranted = true;

    @Column(name = "push_consent_granted", nullable = false)
    private boolean pushConsentGranted = false;

    @Column(name = "time_zone", nullable = false, length = 50)
    private String timeZone = "Asia/Kolkata";

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public NotificationPreferenceEntity() {}

    public NotificationPreferenceEntity(UserEntity user) {
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

