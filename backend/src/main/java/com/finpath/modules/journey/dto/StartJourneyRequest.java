package com.finpath.modules.journey.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

public class StartJourneyRequest {

    @NotBlank(message = "journeyType cannot be blank")
    private String journeyType;

    private Map<String, Object> initialData;

    public StartJourneyRequest() {}

    public StartJourneyRequest(String journeyType) {
        this.journeyType = journeyType;
    }

    public StartJourneyRequest(String journeyType, Map<String, Object> initialData) {
        this.journeyType = journeyType;
        this.initialData = initialData;
    }

    public String getJourneyType() { return journeyType; }
    public void setJourneyType(String journeyType) { this.journeyType = journeyType; }

    public Map<String, Object> getInitialData() { return initialData; }
    public void setInitialData(Map<String, Object> initialData) { this.initialData = initialData; }
}
