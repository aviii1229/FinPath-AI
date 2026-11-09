package com.finpath.modules.documents.dto;

public class ExtractedEntityDto {

    private String fieldName; // POLICY_NUMBER, SANCTION_AMOUNT, PREMIUM_AMOUNT, COVERAGE_AMOUNT, INTEREST_RATE, REPAYMENT_PERIOD, STATEMENT_PERIOD, DOCUMENT_DATE, EXPIRY_DATE
    private String fieldLabel;
    private String extractedValue;
    private String normalizedValue;
    private double confidence;
    private int pageNumber;
    private String sourceSnippet;

    public ExtractedEntityDto() {}

    public ExtractedEntityDto(String fieldName, String fieldLabel, String extractedValue, String normalizedValue, double confidence, int pageNumber, String sourceSnippet) {
        this.fieldName = fieldName;
        this.fieldLabel = fieldLabel;
        this.extractedValue = extractedValue;
        this.normalizedValue = normalizedValue;
        this.confidence = confidence;
        this.pageNumber = pageNumber;
        this.sourceSnippet = sourceSnippet;
    }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public String getFieldLabel() { return fieldLabel; }
    public void setFieldLabel(String fieldLabel) { this.fieldLabel = fieldLabel; }

    public String getExtractedValue() { return extractedValue; }
    public void setExtractedValue(String extractedValue) { this.extractedValue = extractedValue; }

    public String getNormalizedValue() { return normalizedValue; }
    public void setNormalizedValue(String normalizedValue) { this.normalizedValue = normalizedValue; }

    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }

    public int getPageNumber() { return pageNumber; }
    public void setPageNumber(int pageNumber) { this.pageNumber = pageNumber; }

    public String getSourceSnippet() { return sourceSnippet; }
    public void setSourceSnippet(String sourceSnippet) { this.sourceSnippet = sourceSnippet; }
}

