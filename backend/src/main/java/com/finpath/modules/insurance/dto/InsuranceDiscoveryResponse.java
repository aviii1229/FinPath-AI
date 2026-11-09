package com.finpath.modules.insurance.dto;

import java.util.List;

public class InsuranceDiscoveryResponse {

    private List<InsuranceRecommendationDto> recommendations;
    private String aiAdviceSummary;
    private List<String> detectedCoverageGaps;

    public InsuranceDiscoveryResponse() {}

    public InsuranceDiscoveryResponse(List<InsuranceRecommendationDto> recommendations, String aiAdviceSummary, List<String> detectedCoverageGaps) {
        this.recommendations = recommendations;
        this.aiAdviceSummary = aiAdviceSummary;
        this.detectedCoverageGaps = detectedCoverageGaps;
    }

    public List<InsuranceRecommendationDto> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<InsuranceRecommendationDto> recommendations) {
        this.recommendations = recommendations;
    }

    public String getAiAdviceSummary() {
        return aiAdviceSummary;
    }

    public void setAiAdviceSummary(String aiAdviceSummary) {
        this.aiAdviceSummary = aiAdviceSummary;
    }

    public List<String> getDetectedCoverageGaps() {
        return detectedCoverageGaps;
    }

    public void setDetectedCoverageGaps(List<String> detectedCoverageGaps) {
        this.detectedCoverageGaps = detectedCoverageGaps;
    }
}

