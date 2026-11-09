package com.finpath.modules.fintech.dto;

import java.util.ArrayList;
import java.util.List;

public class FinancialHealthScoreDto {

    private int overallScore; // 0 - 100
    private String healthTier; // EXCELLENT (80-100), GOOD (65-79), FAIR (50-64), NEEDS_ATTENTION (<50)
    private String summaryText;
    private List<ScoringFactorDto> scoringFactors = new ArrayList<>();
    private List<String> improvementAreas = new ArrayList<>();
    private String disclaimer = "This financial health score is an illustrative personal finance evaluation for informational and planning purposes only. It DOES NOT constitute a credit score (e.g., CIBIL/Experian), official underwriting rating, or formal creditworthiness assessment.";

    public FinancialHealthScoreDto() {}

    public FinancialHealthScoreDto(int overallScore, String healthTier, String summaryText, List<ScoringFactorDto> scoringFactors, List<String> improvementAreas) {
        this.overallScore = overallScore;
        this.healthTier = healthTier;
        this.summaryText = summaryText;
        this.scoringFactors = scoringFactors != null ? scoringFactors : new ArrayList<>();
        this.improvementAreas = improvementAreas != null ? improvementAreas : new ArrayList<>();
    }

    public static class ScoringFactorDto {
        private String factorName;
        private int scoreAwarded;
        private int maxScore;
        private String currentMetricValue;
        private String benchmark;
        private String status; // OPTIMAL, GOOD, FAIR, VULNERABLE
        private String explanation;

        public ScoringFactorDto() {}

        public ScoringFactorDto(String factorName, int scoreAwarded, int maxScore, String currentMetricValue, String benchmark, String status, String explanation) {
            this.factorName = factorName;
            this.scoreAwarded = scoreAwarded;
            this.maxScore = maxScore;
            this.currentMetricValue = currentMetricValue;
            this.benchmark = benchmark;
            this.status = status;
            this.explanation = explanation;
        }

        public String getFactorName() { return factorName; }
        public void setFactorName(String factorName) { this.factorName = factorName; }

        public int getScoreAwarded() { return scoreAwarded; }
        public void setScoreAwarded(int scoreAwarded) { this.scoreAwarded = scoreAwarded; }

        public int getMaxScore() { return maxScore; }
        public void setMaxScore(int maxScore) { this.maxScore = maxScore; }

        public String getCurrentMetricValue() { return currentMetricValue; }
        public void setCurrentMetricValue(String currentMetricValue) { this.currentMetricValue = currentMetricValue; }

        public String getBenchmark() { return benchmark; }
        public void setBenchmark(String benchmark) { this.benchmark = benchmark; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getExplanation() { return explanation; }
        public void setExplanation(String explanation) { this.explanation = explanation; }
    }

    public int getOverallScore() { return overallScore; }
    public void setOverallScore(int overallScore) { this.overallScore = overallScore; }

    public String getHealthTier() { return healthTier; }
    public void setHealthTier(String healthTier) { this.healthTier = healthTier; }

    public String getSummaryText() { return summaryText; }
    public void setSummaryText(String summaryText) { this.summaryText = summaryText; }

    public List<ScoringFactorDto> getScoringFactors() { return scoringFactors; }
    public void setScoringFactors(List<ScoringFactorDto> scoringFactors) { this.scoringFactors = scoringFactors; }

    public List<String> getImprovementAreas() { return improvementAreas; }
    public void setImprovementAreas(List<String> improvementAreas) { this.improvementAreas = improvementAreas; }

    public String getDisclaimer() { return disclaimer; }
    public void setDisclaimer(String disclaimer) { this.disclaimer = disclaimer; }
}

