package com.finpath.modules.fintech.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateTransactionCategoryRequest {

    @NotBlank(message = "Category is required")
    private String category;

    public UpdateTransactionCategoryRequest() {}

    public UpdateTransactionCategoryRequest(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

