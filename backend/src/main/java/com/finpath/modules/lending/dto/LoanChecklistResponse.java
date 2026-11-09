package com.finpath.modules.lending.dto;

import java.util.List;

public class LoanChecklistResponse {
    private String loanType;
    private String employmentType;
    private List<ChecklistItem> mandatoryDocuments;
    private List<ChecklistItem> optionalSupportingDocuments;
    private List<String> underwritingCriteria;
    private List<String> documentPreparationTips;

    public static class ChecklistItem {
        private String documentCode;
        private String title;
        private String description;
        private boolean mandatory;
        private String formatGuidance;

        public ChecklistItem() {}

        public ChecklistItem(String documentCode, String title, String description, boolean mandatory, String formatGuidance) {
            this.documentCode = documentCode;
            this.title = title;
            this.description = description;
            this.mandatory = mandatory;
            this.formatGuidance = formatGuidance;
        }

        public String getDocumentCode() {
            return documentCode;
        }

        public void setDocumentCode(String documentCode) {
            this.documentCode = documentCode;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isMandatory() {
            return mandatory;
        }

        public void setMandatory(boolean mandatory) {
            this.mandatory = mandatory;
        }

        public String getFormatGuidance() {
            return formatGuidance;
        }

        public void setFormatGuidance(String formatGuidance) {
            this.formatGuidance = formatGuidance;
        }
    }

    public LoanChecklistResponse() {}

    public LoanChecklistResponse(String loanType, String employmentType, List<ChecklistItem> mandatoryDocuments, List<ChecklistItem> optionalSupportingDocuments, List<String> underwritingCriteria, List<String> documentPreparationTips) {
        this.loanType = loanType;
        this.employmentType = employmentType;
        this.mandatoryDocuments = mandatoryDocuments;
        this.optionalSupportingDocuments = optionalSupportingDocuments;
        this.underwritingCriteria = underwritingCriteria;
        this.documentPreparationTips = documentPreparationTips;
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public List<ChecklistItem> getMandatoryDocuments() {
        return mandatoryDocuments;
    }

    public void setMandatoryDocuments(List<ChecklistItem> mandatoryDocuments) {
        this.mandatoryDocuments = mandatoryDocuments;
    }

    public List<ChecklistItem> getOptionalSupportingDocuments() {
        return optionalSupportingDocuments;
    }

    public void setOptionalSupportingDocuments(List<ChecklistItem> optionalSupportingDocuments) {
        this.optionalSupportingDocuments = optionalSupportingDocuments;
    }

    public List<String> getUnderwritingCriteria() {
        return underwritingCriteria;
    }

    public void setUnderwritingCriteria(List<String> underwritingCriteria) {
        this.underwritingCriteria = underwritingCriteria;
    }

    public List<String> getDocumentPreparationTips() {
        return documentPreparationTips;
    }

    public void setDocumentPreparationTips(List<String> documentPreparationTips) {
        this.documentPreparationTips = documentPreparationTips;
    }
}

