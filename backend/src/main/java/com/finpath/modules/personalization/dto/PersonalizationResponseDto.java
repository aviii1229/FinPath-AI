package com.finpath.modules.personalization.dto;

import com.finpath.security.dto.UserProfileDto;

import java.util.ArrayList;
import java.util.List;

public class PersonalizationResponseDto {
    private boolean onboardingCompleted;
    private int currentStep;
    private String contextualWelcomeTitle;
    private String contextualWelcomeMessage;
    private List<RelevantModuleDto> relevantModules = new ArrayList<>();
    private List<SuggestedJourneyDto> suggestedJourneys = new ArrayList<>();
    private DashboardCustomizationDto dashboardCustomization;
    private UserProfileDto profile;

    public PersonalizationResponseDto() {}

    public boolean isOnboardingCompleted() {
        return onboardingCompleted;
    }

    public void setOnboardingCompleted(boolean onboardingCompleted) {
        this.onboardingCompleted = onboardingCompleted;
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }

    public String getContextualWelcomeTitle() {
        return contextualWelcomeTitle;
    }

    public void setContextualWelcomeTitle(String contextualWelcomeTitle) {
        this.contextualWelcomeTitle = contextualWelcomeTitle;
    }

    public String getContextualWelcomeMessage() {
        return contextualWelcomeMessage;
    }

    public void setContextualWelcomeMessage(String contextualWelcomeMessage) {
        this.contextualWelcomeMessage = contextualWelcomeMessage;
    }

    public List<RelevantModuleDto> getRelevantModules() {
        return relevantModules;
    }

    public void setRelevantModules(List<RelevantModuleDto> relevantModules) {
        this.relevantModules = relevantModules;
    }

    public List<SuggestedJourneyDto> getSuggestedJourneys() {
        return suggestedJourneys;
    }

    public void setSuggestedJourneys(List<SuggestedJourneyDto> suggestedJourneys) {
        this.suggestedJourneys = suggestedJourneys;
    }

    public DashboardCustomizationDto getDashboardCustomization() {
        return dashboardCustomization;
    }

    public void setDashboardCustomization(DashboardCustomizationDto dashboardCustomization) {
        this.dashboardCustomization = dashboardCustomization;
    }

    public UserProfileDto getProfile() {
        return profile;
    }

    public void setProfile(UserProfileDto profile) {
        this.profile = profile;
    }
}

