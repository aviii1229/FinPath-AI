package com.finpath.modules.lending.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateApplicationStatusRequest {

    @NotBlank(message = "New status is required")
    private String status; // DRAFT, SUBMITTED, UNDER_REVIEW, DOCUMENTS_REQUIRED, APPROVED, DECLINED, DISBURSED

    private String notes;
    private String missingDocuments;

    public UpdateApplicationStatusRequest() {}

    public UpdateApplicationStatusRequest(String status, String notes, String missingDocuments) {
        this.status = status;
        this.notes = notes;
        this.missingDocuments = missingDocuments;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getMissingDocuments() {
        return missingDocuments;
    }

    public void setMissingDocuments(String missingDocuments) {
        this.missingDocuments = missingDocuments;
    }
}

