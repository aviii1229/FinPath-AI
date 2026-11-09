package com.finpath.modules.insurance.dto;

import java.util.List;

public class PolicyRagQueryResponse {

    private String question;
    private String answer;
    private List<PolicyCitationDto> citations;
    private boolean grounded;
    private String matchedPolicyName;

    public PolicyRagQueryResponse() {}

    public PolicyRagQueryResponse(String question, String answer, List<PolicyCitationDto> citations, boolean grounded, String matchedPolicyName) {
        this.question = question;
        this.answer = answer;
        this.citations = citations;
        this.grounded = grounded;
        this.matchedPolicyName = matchedPolicyName;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<PolicyCitationDto> getCitations() {
        return citations;
    }

    public void setCitations(List<PolicyCitationDto> citations) {
        this.citations = citations;
    }

    public boolean isGrounded() {
        return grounded;
    }

    public void setGrounded(boolean grounded) {
        this.grounded = grounded;
    }

    public String getMatchedPolicyName() {
        return matchedPolicyName;
    }

    public void setMatchedPolicyName(String matchedPolicyName) {
        this.matchedPolicyName = matchedPolicyName;
    }
}

