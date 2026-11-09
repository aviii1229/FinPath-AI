package com.finpath.modules.insurance.dto;

public class ChecklistItemDto {

    private String documentName;
    private String description;
    private boolean mandatory;
    private String sampleTemplate;
    private String status = "PENDING"; // PENDING, UPLOADED, VERIFIED

    public ChecklistItemDto() {}

    public ChecklistItemDto(String documentName, String description, boolean mandatory, String sampleTemplate) {
        this.documentName = documentName;
        this.description = description;
        this.mandatory = mandatory;
        this.sampleTemplate = sampleTemplate;
        this.status = "PENDING";
    }

    public ChecklistItemDto(String documentName, String description, boolean mandatory, String sampleTemplate, String status) {
        this.documentName = documentName;
        this.description = description;
        this.mandatory = mandatory;
        this.sampleTemplate = sampleTemplate;
        this.status = status;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
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

    public String getSampleTemplate() {
        return sampleTemplate;
    }

    public void setSampleTemplate(String sampleTemplate) {
        this.sampleTemplate = sampleTemplate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

