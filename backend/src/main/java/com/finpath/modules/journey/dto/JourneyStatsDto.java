package com.finpath.modules.journey.dto;

import java.util.HashMap;
import java.util.Map;

public class JourneyStatsDto {

    private long totalJourneys;
    private long activeJourneys;
    private long completedJourneys;
    private long pausedJourneys;
    private long cancelledJourneys;
    private int averageProgressPercentage;
    private Map<String, Long> domainDistribution = new HashMap<>();

    public JourneyStatsDto() {}

    public long getTotalJourneys() { return totalJourneys; }
    public void setTotalJourneys(long totalJourneys) { this.totalJourneys = totalJourneys; }

    public long getActiveJourneys() { return activeJourneys; }
    public void setActiveJourneys(long activeJourneys) { this.activeJourneys = activeJourneys; }

    public long getCompletedJourneys() { return completedJourneys; }
    public void setCompletedJourneys(long completedJourneys) { this.completedJourneys = completedJourneys; }

    public long getPausedJourneys() { return pausedJourneys; }
    public void setPausedJourneys(long pausedJourneys) { this.pausedJourneys = pausedJourneys; }

    public long getCancelledJourneys() { return cancelledJourneys; }
    public void setCancelledJourneys(long cancelledJourneys) { this.cancelledJourneys = cancelledJourneys; }

    public int getAverageProgressPercentage() { return averageProgressPercentage; }
    public void setAverageProgressPercentage(int averageProgressPercentage) { this.averageProgressPercentage = averageProgressPercentage; }

    public Map<String, Long> getDomainDistribution() { return domainDistribution; }
    public void setDomainDistribution(Map<String, Long> domainDistribution) { this.domainDistribution = domainDistribution; }
}
