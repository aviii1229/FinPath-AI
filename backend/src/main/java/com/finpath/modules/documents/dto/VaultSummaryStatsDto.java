package com.finpath.modules.documents.dto;

import java.util.HashMap;
import java.util.Map;

public class VaultSummaryStatsDto {

    private long totalDocuments;
    private long totalProcessed;
    private long totalFailed;
    private long totalSizeBytes;
    private String formattedTotalSize;
    private Map<String, Long> countByCategory = new HashMap<>();
    private int averageCompletenessScore;

    public VaultSummaryStatsDto() {}

    public long getTotalDocuments() { return totalDocuments; }
    public void setTotalDocuments(long totalDocuments) { this.totalDocuments = totalDocuments; }

    public long getTotalProcessed() { return totalProcessed; }
    public void setTotalProcessed(long totalProcessed) { this.totalProcessed = totalProcessed; }

    public long getTotalFailed() { return totalFailed; }
    public void setTotalFailed(long totalFailed) { this.totalFailed = totalFailed; }

    public long getTotalSizeBytes() { return totalSizeBytes; }
    public void setTotalSizeBytes(long totalSizeBytes) {
        this.totalSizeBytes = totalSizeBytes;
        this.formattedTotalSize = DocumentVaultItemDto.formatBytes(totalSizeBytes);
    }

    public String getFormattedTotalSize() { return formattedTotalSize; }
    public void setFormattedTotalSize(String formattedTotalSize) { this.formattedTotalSize = formattedTotalSize; }

    public Map<String, Long> getCountByCategory() { return countByCategory; }
    public void setCountByCategory(Map<String, Long> countByCategory) { this.countByCategory = countByCategory; }

    public int getAverageCompletenessScore() { return averageCompletenessScore; }
    public void setAverageCompletenessScore(int averageCompletenessScore) { this.averageCompletenessScore = averageCompletenessScore; }
}

