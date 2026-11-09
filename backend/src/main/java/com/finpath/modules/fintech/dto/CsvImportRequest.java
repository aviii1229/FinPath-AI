package com.finpath.modules.fintech.dto;

import java.util.List;

public class CsvImportRequest {

    private String csvContent;
    private String defaultAccountId;

    public CsvImportRequest() {}

    public CsvImportRequest(String csvContent, String defaultAccountId) {
        this.csvContent = csvContent;
        this.defaultAccountId = defaultAccountId;
    }

    public String getCsvContent() {
        return csvContent;
    }

    public void setCsvContent(String csvContent) {
        this.csvContent = csvContent;
    }

    public String getDefaultAccountId() {
        return defaultAccountId;
    }

    public void setDefaultAccountId(String defaultAccountId) {
        this.defaultAccountId = defaultAccountId;
    }
}

