package com.finpath.modules.lending.dto;

import java.util.List;

public class LoanAssistantQueryResponse {

    private String topic;
    private String answer;
    private String plainEnglishDefinition;
    private List<String> practicalImpacts;
    private List<String> relatedTerms;
    private List<String> recommendedNextSteps;

    public LoanAssistantQueryResponse() {}

    public LoanAssistantQueryResponse(String topic, String answer, String plainEnglishDefinition, List<String> practicalImpacts, List<String> relatedTerms, List<String> recommendedNextSteps) {
        this.topic = topic;
        this.answer = answer;
        this.plainEnglishDefinition = plainEnglishDefinition;
        this.practicalImpacts = practicalImpacts;
        this.relatedTerms = relatedTerms;
        this.recommendedNextSteps = recommendedNextSteps;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getPlainEnglishDefinition() {
        return plainEnglishDefinition;
    }

    public void setPlainEnglishDefinition(String plainEnglishDefinition) {
        this.plainEnglishDefinition = plainEnglishDefinition;
    }

    public List<String> getPracticalImpacts() {
        return practicalImpacts;
    }

    public void setPracticalImpacts(List<String> practicalImpacts) {
        this.practicalImpacts = practicalImpacts;
    }

    public List<String> getRelatedTerms() {
        return relatedTerms;
    }

    public void setRelatedTerms(List<String> relatedTerms) {
        this.relatedTerms = relatedTerms;
    }

    public List<String> getRecommendedNextSteps() {
        return recommendedNextSteps;
    }

    public void setRecommendedNextSteps(List<String> recommendedNextSteps) {
        this.recommendedNextSteps = recommendedNextSteps;
    }
}

