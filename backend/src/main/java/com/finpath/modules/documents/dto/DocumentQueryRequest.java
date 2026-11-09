package com.finpath.modules.documents.dto;

import jakarta.validation.constraints.NotBlank;

public class DocumentQueryRequest {

    @NotBlank(message = "Question cannot be blank")
    private String question;

    public DocumentQueryRequest() {}

    public DocumentQueryRequest(String question) {
        this.question = question;
    }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
}
