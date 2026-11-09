package com.finpath.modules.ai.dto;

import java.util.List;
import java.util.Map;

public class AiRecommendationCardDto {
    private String cardType; // INSURANCE_QUOTE, POLICY_COMPARISON, LOAN_ELIGIBILITY, EMI_BREAKDOWN, LENDER_COMPARISON, BUDGET_ANALYSIS, SAVINGS_PLAN, DEBT_BURDEN_REDUCTION
    private String title;
    private String subtitle;
    private String badge;
    private Map<String, String> metrics;
    private List<String> bulletPoints;
    private String primaryActionLabel;
    private String primaryActionUrl;

    public AiRecommendationCardDto() {}

    public AiRecommendationCardDto(String cardType, String title, String subtitle, String badge,
                                  Map<String, String> metrics, List<String> bulletPoints,
                                  String primaryActionLabel, String primaryActionUrl) {
        this.cardType = cardType;
        this.title = title;
        this.subtitle = subtitle;
        this.badge = badge;
        this.metrics = metrics;
        this.bulletPoints = bulletPoints;
        this.primaryActionLabel = primaryActionLabel;
        this.primaryActionUrl = primaryActionUrl;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public Map<String, String> getMetrics() {
        return metrics;
    }

    public void setMetrics(Map<String, String> metrics) {
        this.metrics = metrics;
    }

    public List<String> getBulletPoints() {
        return bulletPoints;
    }

    public void setBulletPoints(List<String> bulletPoints) {
        this.bulletPoints = bulletPoints;
    }

    public String getPrimaryActionLabel() {
        return primaryActionLabel;
    }

    public void setPrimaryActionLabel(String primaryActionLabel) {
        this.primaryActionLabel = primaryActionLabel;
    }

    public String getPrimaryActionUrl() {
        return primaryActionUrl;
    }

    public void setPrimaryActionUrl(String primaryActionUrl) {
        this.primaryActionUrl = primaryActionUrl;
    }
}

