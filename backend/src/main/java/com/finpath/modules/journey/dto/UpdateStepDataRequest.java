package com.finpath.modules.journey.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

public class UpdateStepDataRequest {

    @NotBlank(message = "stepKey cannot be blank")
    private String stepKey;

    private Map<String, Object> stepData;

    private Long expectedVersion;

    public UpdateStepDataRequest() {}

    public UpdateStepDataRequest(String stepKey, Map<String, Object> stepData) {
        this.stepKey = stepKey;
        this.stepData = stepData;
    }

    public UpdateStepDataRequest(String stepKey, Map<String, Object> stepData, Long expectedVersion) {
        this.stepKey = stepKey;
        this.stepData = stepData;
        this.expectedVersion = expectedVersion;
    }

    public String getStepKey() { return stepKey; }
    public void setStepKey(String stepKey) { this.stepKey = stepKey; }

    public Map<String, Object> getStepData() { return stepData; }
    public void setStepData(Map<String, Object> stepData) { this.stepData = stepData; }

    public Long getExpectedVersion() { return expectedVersion; }
    public void setExpectedVersion(Long expectedVersion) { this.expectedVersion = expectedVersion; }
}
