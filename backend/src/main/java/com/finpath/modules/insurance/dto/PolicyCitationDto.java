package com.finpath.modules.insurance.dto;

public class PolicyCitationDto {

    private String documentName;
    private String sectionTitle;
    private Integer pageNumber;
    private String excerpt;
    private double relevanceScore;

    public PolicyCitationDto() {}

    public PolicyCitationDto(String documentName, String sectionTitle, Integer pageNumber, String excerpt, double relevanceScore) {
        this.documentName = documentName;
        this.sectionTitle = sectionTitle;
        this.pageNumber = pageNumber;
        this.excerpt = excerpt;
        this.relevanceScore = relevanceScore;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public double getRelevanceScore() {
        return relevanceScore;
    }

    public void setRelevanceScore(double relevanceScore) {
        this.relevanceScore = relevanceScore;
    }
}

