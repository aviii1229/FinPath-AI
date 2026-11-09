package com.finpath.modules.insurance.dto;

import java.util.List;
import java.util.Map;

public class InsuranceComparisonDto {

    private List<InsuranceProductDto> products;
    private List<ComparisonRow> comparisonMatrix;
    private String aiRecommendation;

    public InsuranceComparisonDto() {}

    public InsuranceComparisonDto(List<InsuranceProductDto> products, List<ComparisonRow> comparisonMatrix, String aiRecommendation) {
        this.products = products;
        this.comparisonMatrix = comparisonMatrix;
        this.aiRecommendation = aiRecommendation;
    }

    public static class ComparisonRow {
        private String featureLabel;
        private String category;
        private Map<String, String> valuesByProductId;

        public ComparisonRow() {}

        public ComparisonRow(String featureLabel, String category, Map<String, String> valuesByProductId) {
            this.featureLabel = featureLabel;
            this.category = category;
            this.valuesByProductId = valuesByProductId;
        }

        public String getFeatureLabel() {
            return featureLabel;
        }

        public void setFeatureLabel(String featureLabel) {
            this.featureLabel = featureLabel;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public Map<String, String> getValuesByProductId() {
            return valuesByProductId;
        }

        public void setValuesByProductId(Map<String, String> valuesByProductId) {
            this.valuesByProductId = valuesByProductId;
        }
    }

    public List<InsuranceProductDto> getProducts() {
        return products;
    }

    public void setProducts(List<InsuranceProductDto> products) {
        this.products = products;
    }

    public List<ComparisonRow> getComparisonMatrix() {
        return comparisonMatrix;
    }

    public void setComparisonMatrix(List<ComparisonRow> comparisonMatrix) {
        this.comparisonMatrix = comparisonMatrix;
    }

    public String getAiRecommendation() {
        return aiRecommendation;
    }

    public void setAiRecommendation(String aiRecommendation) {
        this.aiRecommendation = aiRecommendation;
    }
}

