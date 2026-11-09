package com.finpath.modules.documents.dto;

public class DocumentCitationDto {

    private int pageNumber;
    private String sectionTitle;
    private String quotedSnippet;
    private double relevanceScore;

    public DocumentCitationDto() {}

    public DocumentCitationDto(int pageNumber, String sectionTitle, String quotedSnippet, double relevanceScore) {
        this.pageNumber = pageNumber;
        this.sectionTitle = sectionTitle;
        this.quotedSnippet = quotedSnippet;
        this.relevanceScore = relevanceScore;
    }

    public int getPageNumber() { return pageNumber; }
    public void setPageNumber(int pageNumber) { this.pageNumber = pageNumber; }

    public String getSectionTitle() { return sectionTitle; }
    public void setSectionTitle(String sectionTitle) { this.sectionTitle = sectionTitle; }

    public String getQuotedSnippet() { return quotedSnippet; }
    public void setQuotedSnippet(String quotedSnippet) { this.quotedSnippet = quotedSnippet; }

    public double getRelevanceScore() { return relevanceScore; }
    public void setRelevanceScore(double relevanceScore) { this.relevanceScore = relevanceScore; }
}

