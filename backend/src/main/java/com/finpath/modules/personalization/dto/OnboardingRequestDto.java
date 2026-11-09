package com.finpath.modules.personalization.dto;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class OnboardingRequestDto {
    // Step 1: Basic Information
    private String firstName;
    private String lastName;
    private Integer age;
    private String city;
    private String employmentCategory;
    private String occupation;

    // Step 2: Financial Overview
    private BigDecimal monthlyIncome;
    private BigDecimal monthlyExpenses;
    private BigDecimal existingObligations;
    private BigDecimal approximateSavings;

    // Step 3: Financial Goals (Multiple selections)
    private Set<String> financialGoals = new HashSet<>();

    // Step 4: Preferences
    private String preferredAssistance;
    private String financialKnowledge;
    private String communicationStyle;
    private String shortTermGoal;
    private String longTermGoal;

    // Navigation and status
    private Integer step = 1;
    private Boolean isCompleted = false;

    public OnboardingRequestDto() {}

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }
}

