package com.finpath.modules.journey.dto;

public class CancelJourneyRequest {

    private String reason;
    private Long expectedVersion;

    public CancelJourneyRequest() {}

    public CancelJourneyRequest(String reason) {
        this.reason = reason;
    }

    public CancelJourneyRequest(String reason, Long expectedVersion) {
        this.reason = reason;
        this.expectedVersion = expectedVersion;
    }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Long getExpectedVersion() { return expectedVersion; }
    public void setExpectedVersion(Long expectedVersion) { this.expectedVersion = expectedVersion; }
}
