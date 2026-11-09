package com.finpath.modules.journey.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

public class CompleteStepRequest {

    @NotBlank(message = "stepKey cannot be blank")
    private String stepKey;

    private Map<String, Object> stepData;

    private boolean userConfirmationApproved = true;

    private Long expectedVersion;

    public CompleteStepRequest() {}

    public CompleteStepRequest(String stepKey, Map<String, Object> stepData) {
        this.stepKey = stepKey;
        this.stepData = stepData;
        this.userConfirmationApproved = true;
    }

    public CompleteStepRequest(String stepKey, Map<String, Object> stepData, boolean userConfirmationApproved, Long expectedVersion) {
        this.stepKey = stepKey;
        this.stepData = stepData;
        this.userConfirmationApproved = userConfirmationApproved;
        this.expectedVersion = expectedVersion;
    }

    public String getStepKey() { return stepKey; }
    public void setStepKey(String stepKey) { this.stepKey = stepKey; }

    public Map<String, Object> getStepData() { return stepData; }
    public void setStepData(Map<String, Object> stepData) { this.stepData = stepData; }

    public boolean isUserConfirmationApproved() { return userConfirmationApproved; }
    public void setUserConfirmationApproved(boolean userConfirmationApproved) { this.userConfirmationApproved = userConfirmationApproved; }

    public Long getExpectedVersion() { return expectedVersion; }
    public void setExpectedVersion(Long expectedVersion) { this.expectedVersion = expectedVersion; }
}
