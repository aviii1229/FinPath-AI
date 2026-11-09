package com.finpath.modules.documents.dto;

import java.util.ArrayList;
import java.util.List;

public class JourneyCompletenessDto {

    private String journeyType; // HOME_LOAN, PERSONAL_LOAN, HEALTH_INSURANCE, TERM_LIFE, INSURANCE_CLAIM
    private String journeyTitle;
    private int totalRequired;
    private int totalFulfilled;
    private int completionPercentage;
    private boolean readyToSubmit;
    private List<DocumentRequirementItemDto> requirements = new ArrayList<>();
    private List<String> missingDocuments = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();
    private String regulatoryDisclaimer = "AI-based completeness evaluation does not constitute official regulatory verification or final institutional approval. All uploads undergo bank or insurer compliance checks.";

    public JourneyCompletenessDto() {}

    public JourneyCompletenessDto(String journeyType, String journeyTitle, int totalRequired, int totalFulfilled, int completionPercentage, boolean readyToSubmit, List<DocumentRequirementItemDto> requirements, List<String> missingDocuments, List<String> warnings) {
        this.journeyType = journeyType;
        this.journeyTitle = journeyTitle;
        this.totalRequired = totalRequired;
        this.totalFulfilled = totalFulfilled;
        this.completionPercentage = completionPercentage;
        this.readyToSubmit = readyToSubmit;
        this.requirements = requirements != null ? requirements : new ArrayList<>();
        this.missingDocuments = missingDocuments != null ? missingDocuments : new ArrayList<>();
        this.warnings = warnings != null ? warnings : new ArrayList<>();
    }

    public String getJourneyType() { return journeyType; }
    public void setJourneyType(String journeyType) { this.journeyType = journeyType; }

    public String getJourneyTitle() { return journeyTitle; }
    public void setJourneyTitle(String journeyTitle) { this.journeyTitle = journeyTitle; }

    public int getTotalRequired() { return totalRequired; }
    public void setTotalRequired(int totalRequired) { this.totalRequired = totalRequired; }

    public int getTotalFulfilled() { return totalFulfilled; }
    public void setTotalFulfilled(int totalFulfilled) { this.totalFulfilled = totalFulfilled; }

    public int getCompletionPercentage() { return completionPercentage; }
    public void setCompletionPercentage(int completionPercentage) { this.completionPercentage = completionPercentage; }

    public boolean isReadyToSubmit() { return readyToSubmit; }
    public void setReadyToSubmit(boolean readyToSubmit) { this.readyToSubmit = readyToSubmit; }

    public List<DocumentRequirementItemDto> getRequirements() { return requirements; }
    public void setRequirements(List<DocumentRequirementItemDto> requirements) { this.requirements = requirements; }

    public List<String> getMissingDocuments() { return missingDocuments; }
    public void setMissingDocuments(List<String> missingDocuments) { this.missingDocuments = missingDocuments; }

    public List<String> getWarnings() { return warnings; }
    public void setWarnings(List<String> warnings) { this.warnings = warnings; }

    public String getRegulatoryDisclaimer() { return regulatoryDisclaimer; }
    public void setRegulatoryDisclaimer(String regulatoryDisclaimer) { this.regulatoryDisclaimer = regulatoryDisclaimer; }
}

