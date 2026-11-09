package com.finpath.modules.documents.dto;

import java.math.BigDecimal;
import java.util.List;

public class DocumentClassificationResultDto {

    private String category;
    private String categoryLabel;
    private BigDecimal confidence;
    private String reasoning;
    private List<String> detectedKeywords;

    public DocumentClassificationResultDto() {}

    public DocumentClassificationResultDto(String category, BigDecimal confidence, String reasoning, List<String> detectedKeywords) {
        this.category = category;
        this.categoryLabel = DocumentVaultItemDto.getHumanLabel(category);
        this.confidence = confidence;
        this.reasoning = reasoning;
        this.detectedKeywords = detectedKeywords;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) {
        this.category = category;
        this.categoryLabel = DocumentVaultItemDto.getHumanLabel(category);
    }

    public String getCategoryLabel() { return categoryLabel; }
    public void setCategoryLabel(String categoryLabel) { this.categoryLabel = categoryLabel; }

    public BigDecimal getConfidence() { return confidence; }
    public void setConfidence(BigDecimal confidence) { this.confidence = confidence; }

    public String getReasoning() { return reasoning; }
    public void setReasoning(String reasoning) { this.reasoning = reasoning; }

    public List<String> getDetectedKeywords() { return detectedKeywords; }
    public void setDetectedKeywords(List<String> detectedKeywords) { this.detectedKeywords = detectedKeywords; }
}
