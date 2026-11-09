package com.finpath.modules.documents.dto;

import jakarta.validation.constraints.NotBlank;

public class CategoryCorrectionRequest {

    @NotBlank(message = "Category cannot be blank")
    private String category;

    private String userNote;

    public CategoryCorrectionRequest() {}

    public CategoryCorrectionRequest(String category) {
        this.category = category;
    }

    public CategoryCorrectionRequest(String category, String userNote) {
        this.category = category;
        this.userNote = userNote;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getUserNote() { return userNote; }
    public void setUserNote(String userNote) { this.userNote = userNote; }
}
