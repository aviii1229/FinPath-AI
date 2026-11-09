package com.finpath.modules.lending.dto;

import java.math.BigDecimal;
import java.util.List;

public class LoanEligibilityResponse {

    private boolean isIllustrativelyEligible;
    private BigDecimal requestedAmount;
    private BigDecimal maxEligibleAmount;
    private BigDecimal calculatedFoirPercentage; // Fixed Obligation to Income Ratio
    private BigDecimal proposedEmi;
    private BigDecimal maxPermissibleEmi;
    private BigDecimal estimatedApr;
    private String disclaimer = "ILLUSTRATIVE ESTIMATE ONLY — This calculation is simulated based on unverified preliminary inputs and institutional risk models. It DOES NOT constitute a formal loan approval, commitment, or binding offer by any lending institution.";
    private List<String> eligibilityFactors;
    private List<String> improvementSuggestions;
    private List<LoanRecommendationDto> matchingProducts;

    public LoanEligibilityResponse() {}

    public LoanEligibilityResponse(boolean isIllustrativelyEligible, BigDecimal requestedAmount, BigDecimal maxEligibleAmount, BigDecimal calculatedFoirPercentage, BigDecimal proposedEmi, BigDecimal maxPermissibleEmi, BigDecimal estimatedApr, List<String> eligibilityFactors, List<String> improvementSuggestions, List<LoanRecommendationDto> matchingProducts) {
        this.isIllustrativelyEligible = isIllustrativelyEligible;
        this.requestedAmount = requestedAmount;
        this.maxEligibleAmount = maxEligibleAmount;
        this.calculatedFoirPercentage = calculatedFoirPercentage;
        this.proposedEmi = proposedEmi;
        this.maxPermissibleEmi = maxPermissibleEmi;
        this.estimatedApr = estimatedApr;
        this.eligibilityFactors = eligibilityFactors;
        this.improvementSuggestions = improvementSuggestions;
        this.matchingProducts = matchingProducts;
    }

    public boolean isIllustrativelyEligible() {
        return isIllustrativelyEligible;
    }

    public void setIllustrativelyEligible(boolean illustrativelyEligible) {
        isIllustrativelyEligible = illustrativelyEligible;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(BigDecimal requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public BigDecimal getMaxEligibleAmount() {
        return maxEligibleAmount;
    }

    public void setMaxEligibleAmount(BigDecimal maxEligibleAmount) {
        this.maxEligibleAmount = maxEligibleAmount;
    }

    public BigDecimal getCalculatedFoirPercentage() {
        return calculatedFoirPercentage;
    }

    public void setCalculatedFoirPercentage(BigDecimal calculatedFoirPercentage) {
        this.calculatedFoirPercentage = calculatedFoirPercentage;
    }

    public BigDecimal getProposedEmi() {
        return proposedEmi;
    }

    public void setProposedEmi(BigDecimal proposedEmi) {
        this.proposedEmi = proposedEmi;
    }

    public BigDecimal getMaxPermissibleEmi() {
        return maxPermissibleEmi;
    }

    public void setMaxPermissibleEmi(BigDecimal maxPermissibleEmi) {
        this.maxPermissibleEmi = maxPermissibleEmi;
    }

    public BigDecimal getEstimatedApr() {
        return estimatedApr;
    }

    public void setEstimatedApr(BigDecimal estimatedApr) {
        this.estimatedApr = estimatedApr;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }

    public List<String> getEligibilityFactors() {
        return eligibilityFactors;
    }

    public void setEligibilityFactors(List<String> eligibilityFactors) {
        this.eligibilityFactors = eligibilityFactors;
    }

    public List<String> getImprovementSuggestions() {
        return improvementSuggestions;
    }

    public void setImprovementSuggestions(List<String> improvementSuggestions) {
        this.improvementSuggestions = improvementSuggestions;
    }

    public List<LoanRecommendationDto> getMatchingProducts() {
        return matchingProducts;
    }

    public void setMatchingProducts(List<LoanRecommendationDto> matchingProducts) {
        this.matchingProducts = matchingProducts;
    }
}

