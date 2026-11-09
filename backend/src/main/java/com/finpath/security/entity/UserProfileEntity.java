package com.finpath.security.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "user_profiles")
@EntityListeners(AuditingEntityListener.class)
public class UserProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "encrypted_national_id")
    private String encryptedNationalId;

    @Column(name = "national_id_hash", length = 64)
    private String nationalIdHash;

    @Column(name = "employment_status", length = 50)
    private String employmentStatus;

    @Column(name = "annual_income", precision = 15, scale = 2)
    private BigDecimal annualIncome = BigDecimal.ZERO;

    @Column(name = "risk_tolerance", length = 30)
    private String riskTolerance = "MODERATE";

    @Column(name = "address_line1")
    private String addressLine1;

    @Column(length = 100)
    private String city;

    @Column(name = "state_province", length = 100)
    private String stateProvince;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "country_code", length = 3)
    private String countryCode = "IND";

    @Column(name = "age")
    private Integer age;

    @Column(name = "employment_category", length = 50)
    private String employmentCategory;

    @Column(name = "occupation", length = 100)
    private String occupation;

    @Column(name = "monthly_income", precision = 15, scale = 2)
    private BigDecimal monthlyIncome = BigDecimal.valueOf(75000.00);

    @Column(name = "monthly_expenses", precision = 15, scale = 2)
    private BigDecimal monthlyExpenses = BigDecimal.valueOf(33740.00);

    @Column(name = "existing_obligations", precision = 15, scale = 2)
    private BigDecimal existingObligations = BigDecimal.valueOf(8540.00);

    @Column(name = "approximate_savings", precision = 15, scale = 2)
    private BigDecimal approximateSavings = BigDecimal.valueOf(345250.00);

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_financial_goals", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "goal_name")
    private Set<String> financialGoals = new HashSet<>();

    @Column(name = "preferred_assistance", length = 50)
    private String preferredAssistance = "AI_CONVERSATIONAL";

    @Column(name = "financial_knowledge", length = 50)
    private String financialKnowledge = "INTERMEDIATE";

    @Column(name = "communication_style", length = 50)
    private String communicationStyle = "ANALYTICAL";

    @Column(name = "short_term_goal", length = 255)
    private String shortTermGoal;

    @Column(name = "long_term_goal", length = 255)
    private String longTermGoal;

    @Column(name = "onboarding_completed", nullable = false)
    private boolean onboardingCompleted = false;

    @Column(name = "onboarding_step", nullable = false)
    private int onboardingStep = 0;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public UserProfileEntity() {}

    public UserProfileEntity(UserEntity user) {
        this.user = user;
        this.riskTolerance = "MODERATE";
        this.annualIncome = BigDecimal.valueOf(900000);
        this.employmentStatus = "EMPLOYED";
        this.countryCode = "IND";
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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEncryptedNationalId() {
        return encryptedNationalId;
    }

    public void setEncryptedNationalId(String encryptedNationalId) {
        this.encryptedNationalId = encryptedNationalId;
    }

    public String getNationalIdHash() {
        return nationalIdHash;
    }

    public void setNationalIdHash(String nationalIdHash) {
        this.nationalIdHash = nationalIdHash;
    }

    public String getEmploymentStatus() {
        return employmentStatus;
    }

    public void setEmploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    public BigDecimal getAnnualIncome() {
        return annualIncome;
    }

    public void setAnnualIncome(BigDecimal annualIncome) {
        this.annualIncome = annualIncome;
    }

    public String getRiskTolerance() {
        return riskTolerance;
    }

    public void setRiskTolerance(String riskTolerance) {
        this.riskTolerance = riskTolerance;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getEmploymentCategory() {
        return employmentCategory;
    }

    public void setEmploymentCategory(String employmentCategory) {
        this.employmentCategory = employmentCategory;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public BigDecimal getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(BigDecimal monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public BigDecimal getMonthlyExpenses() {
        return monthlyExpenses;
    }

    public void setMonthlyExpenses(BigDecimal monthlyExpenses) {
        this.monthlyExpenses = monthlyExpenses;
    }

    public BigDecimal getExistingObligations() {
        return existingObligations;
    }

    public void setExistingObligations(BigDecimal existingObligations) {
        this.existingObligations = existingObligations;
    }

    public BigDecimal getApproximateSavings() {
        return approximateSavings;
    }

    public void setApproximateSavings(BigDecimal approximateSavings) {
        this.approximateSavings = approximateSavings;
    }

    public Set<String> getFinancialGoals() {
        return financialGoals;
    }

    public void setFinancialGoals(Set<String> financialGoals) {
        this.financialGoals = financialGoals;
    }

    public String getPreferredAssistance() {
        return preferredAssistance;
    }

    public void setPreferredAssistance(String preferredAssistance) {
        this.preferredAssistance = preferredAssistance;
    }

    public String getFinancialKnowledge() {
        return financialKnowledge;
    }

    public void setFinancialKnowledge(String financialKnowledge) {
        this.financialKnowledge = financialKnowledge;
    }

    public String getCommunicationStyle() {
        return communicationStyle;
    }

    public void setCommunicationStyle(String communicationStyle) {
        this.communicationStyle = communicationStyle;
    }

    public String getShortTermGoal() {
        return shortTermGoal;
    }

    public void setShortTermGoal(String shortTermGoal) {
        this.shortTermGoal = shortTermGoal;
    }

    public String getLongTermGoal() {
        return longTermGoal;
    }

    public void setLongTermGoal(String longTermGoal) {
        this.longTermGoal = longTermGoal;
    }

    public boolean isOnboardingCompleted() {
        return onboardingCompleted;
    }

    public void setOnboardingCompleted(boolean onboardingCompleted) {
        this.onboardingCompleted = onboardingCompleted;
    }

    public int getOnboardingStep() {
        return onboardingStep;
    }

    public void setOnboardingStep(int onboardingStep) {
        this.onboardingStep = onboardingStep;
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

