package com.finpath.modules.insurance.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public class PolicyRagQueryRequest {

    private UUID documentId;
    private String policyCategory;

    @NotBlank(message = "Question cannot be blank")
    private String question;

    public PolicyRagQueryRequest() {}

    public PolicyRagQueryRequest(UUID documentId, String policyCategory, String question) {
        this.documentId = documentId;
        this.policyCategory = policyCategory;
        this.question = question;
    }

    public UUID getDocumentId() {
        return documentId;
    }

    public void setDocumentId(UUID documentId) {
        this.documentId = documentId;
    }

    public String getPolicyCategory() {
        return policyCategory;
    }

    public void setPolicyCategory(String policyCategory) {
        this.policyCategory = policyCategory;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}

