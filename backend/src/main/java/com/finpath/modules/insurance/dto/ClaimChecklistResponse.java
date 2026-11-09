package com.finpath.modules.insurance.dto;

import java.util.List;

public class ClaimChecklistResponse {

    private String category;
    private String incidentType;
    private List<ChecklistItemDto> requiredDocuments;
    private List<String> commonPitfalls;
    private List<String> submissionDeadlines;
    private String adjudicationCriteria;
    private int estimatedProcessingDays;

    public ClaimChecklistResponse() {}

    public ClaimChecklistResponse(String category, String incidentType, List<ChecklistItemDto> requiredDocuments, List<String> commonPitfalls, List<String> submissionDeadlines, String adjudicationCriteria, int estimatedProcessingDays) {
        this.category = category;
        this.incidentType = incidentType;
        this.requiredDocuments = requiredDocuments;
        this.commonPitfalls = commonPitfalls;
        this.submissionDeadlines = submissionDeadlines;
        this.adjudicationCriteria = adjudicationCriteria;
        this.estimatedProcessingDays = estimatedProcessingDays;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIncidentType() {
        return incidentType;
    }

    public void setIncidentType(String incidentType) {
        this.incidentType = incidentType;
    }

    public List<ChecklistItemDto> getRequiredDocuments() {
        return requiredDocuments;
    }

    public void setRequiredDocuments(List<ChecklistItemDto> requiredDocuments) {
        this.requiredDocuments = requiredDocuments;
    }

    public List<String> getCommonPitfalls() {
        return commonPitfalls;
    }

    public void setCommonPitfalls(List<String> commonPitfalls) {
        this.commonPitfalls = commonPitfalls;
    }

    public List<String> getSubmissionDeadlines() {
        return submissionDeadlines;
    }

    public void setSubmissionDeadlines(List<String> submissionDeadlines) {
        this.submissionDeadlines = submissionDeadlines;
    }

    public String getAdjudicationCriteria() {
        return adjudicationCriteria;
    }

    public void setAdjudicationCriteria(String adjudicationCriteria) {
        this.adjudicationCriteria = adjudicationCriteria;
    }

    public int getEstimatedProcessingDays() {
        return estimatedProcessingDays;
    }

    public void setEstimatedProcessingDays(int estimatedProcessingDays) {
        this.estimatedProcessingDays = estimatedProcessingDays;
    }
}

