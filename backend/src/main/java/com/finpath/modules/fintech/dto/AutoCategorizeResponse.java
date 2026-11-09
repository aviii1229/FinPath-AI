package com.finpath.modules.fintech.dto;

public class AutoCategorizeResponse {

    private String rawMerchantName;
    private String cleanMerchantName;
    private String category;
    private double confidenceScore;
    private String reasoning;

    public AutoCategorizeResponse() {}

    public AutoCategorizeResponse(String rawMerchantName, String cleanMerchantName, String category, double confidenceScore, String reasoning) {
        this.rawMerchantName = rawMerchantName;
        this.cleanMerchantName = cleanMerchantName;
        this.category = category;
        this.confidenceScore = confidenceScore;
        this.reasoning = reasoning;
    }

    public String getRawMerchantName() {
        return rawMerchantName;
    }

    public void setRawMerchantName(String rawMerchantName) {
        this.rawMerchantName = rawMerchantName;
    }

    public String getCleanMerchantName() {
        return cleanMerchantName;
    }

    public void setCleanMerchantName(String cleanMerchantName) {
        this.cleanMerchantName = cleanMerchantName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public String getReasoning() {
        return reasoning;
    }

    public void setReasoning(String reasoning) {
        this.reasoning = reasoning;
    }
}

