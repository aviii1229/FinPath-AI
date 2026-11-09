package com.finpath.modules.ai.dto;

import java.util.ArrayList;
import java.util.List;

public class AiExplainabilityDto {
    private String recommendationSummary;
    private String relevanceRationale;
    private List<String> dataSourcesUsed = new ArrayList<>();
    private List<String> underlyingAssumptions = new ArrayList<>();
    private List<String> constraintsAndLimitations = new ArrayList<>();

    public AiExplainabilityDto() {}

    public AiExplainabilityDto(String recommendationSummary,
                               String relevanceRationale,
                               List<String> dataSourcesUsed,
                               List<String> underlyingAssumptions,
                               List<String> constraintsAndLimitations) {
        this.recommendationSummary = recommendationSummary;
        this.relevanceRationale = relevanceRationale;
        this.dataSourcesUsed = dataSourcesUsed != null ? dataSourcesUsed : new ArrayList<>();
        this.underlyingAssumptions = underlyingAssumptions != null ? underlyingAssumptions : new ArrayList<>();
        this.constraintsAndLimitations = constraintsAndLimitations != null ? constraintsAndLimitations : new ArrayList<>();
    }

    public String getRecommendationSummary() {
        return recommendationSummary;
    }

    public void setRecommendationSummary(String recommendationSummary) {
        this.recommendationSummary = recommendationSummary;
    }

    public String getRelevanceRationale() {
        return relevanceRationale;
    }

    public void setRelevanceRationale(String relevanceRationale) {
        this.relevanceRationale = relevanceRationale;
    }

    public List<String> getDataSourcesUsed() {
        return dataSourcesUsed;
    }

    public void setDataSourcesUsed(List<String> dataSourcesUsed) {
        this.dataSourcesUsed = dataSourcesUsed;
    }

    public List<String> getUnderlyingAssumptions() {
        return underlyingAssumptions;
    }

    public void setUnderlyingAssumptions(List<String> underlyingAssumptions) {
        this.underlyingAssumptions = underlyingAssumptions;
    }

    public List<String> getConstraintsAndLimitations() {
        return constraintsAndLimitations;
    }

    public void setConstraintsAndLimitations(List<String> constraintsAndLimitations) {
        this.constraintsAndLimitations = constraintsAndLimitations;
    }
}

