package com.finpath.modules.insurance.entity;

import com.finpath.security.entity.UserEntity;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "insurance_claims")
@EntityListeners(AuditingEntityListener.class)
public class InsuranceClaimEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "claim_number", nullable = false, unique = true, length = 64)
    private String claimNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private InsurancePolicyEntity policy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "claim_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal claimAmount;

    @Column(name = "approved_amount", precision = 15, scale = 2)
    private BigDecimal approvedAmount;

    @Column(name = "incident_date", nullable = false)
    private LocalDate incidentDate;

    @Column(name = "incident_description", nullable = false, length = 1500)
    private String incidentDescription;

    @Column(nullable = false, length = 30)
    private String status = "SUBMITTED"; // SUBMITTED, IN_REVIEW, APPROVED, SETTLED, REJECTED

    @Column(name = "ai_fraud_risk_score", precision = 5, scale = 2)
    private BigDecimal aiFraudRiskScore = BigDecimal.valueOf(4.5);

    @Column(name = "adjudication_notes", length = 1000)
    private String adjudicationNotes;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public InsuranceClaimEntity() {}

    public InsuranceClaimEntity(String claimNumber, InsurancePolicyEntity policy, UserEntity user, BigDecimal claimAmount, LocalDate incidentDate, String incidentDescription, String status, BigDecimal aiFraudRiskScore) {
        this.claimNumber = claimNumber;
        this.policy = policy;
        this.user = user;
        this.claimAmount = claimAmount;
        this.incidentDate = incidentDate;
        this.incidentDescription = incidentDescription;
        this.status = status;
        this.aiFraudRiskScore = aiFraudRiskScore;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getClaimNumber() {
        return claimNumber;
    }

    public void setClaimNumber(String claimNumber) {
        this.claimNumber = claimNumber;
    }

    public InsurancePolicyEntity getPolicy() {
        return policy;
    }

    public void setPolicy(InsurancePolicyEntity policy) {
        this.policy = policy;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public BigDecimal getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(BigDecimal claimAmount) {
        this.claimAmount = claimAmount;
    }

    public BigDecimal getApprovedAmount() {
        return approvedAmount;
    }

    public void setApprovedAmount(BigDecimal approvedAmount) {
        this.approvedAmount = approvedAmount;
    }

    public LocalDate getIncidentDate() {
        return incidentDate;
    }

    public void setIncidentDate(LocalDate incidentDate) {
        this.incidentDate = incidentDate;
    }

    public String getIncidentDescription() {
        return incidentDescription;
    }

    public void setIncidentDescription(String incidentDescription) {
        this.incidentDescription = incidentDescription;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getAiFraudRiskScore() {
        return aiFraudRiskScore;
    }

    public void setAiFraudRiskScore(BigDecimal aiFraudRiskScore) {
        this.aiFraudRiskScore = aiFraudRiskScore;
    }

    public String getAdjudicationNotes() {
        return adjudicationNotes;
    }

    public void setAdjudicationNotes(String adjudicationNotes) {
        this.adjudicationNotes = adjudicationNotes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}

