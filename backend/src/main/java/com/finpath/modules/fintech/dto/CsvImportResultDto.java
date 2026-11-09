package com.finpath.modules.fintech.dto;

import java.util.List;

public class CsvImportResultDto {

    private int importedCount;
    private int skippedCount;
    private List<String> parsingNotes;
    private List<TransactionDto> importedTransactions;

    public CsvImportResultDto() {}

    public CsvImportResultDto(int importedCount, int skippedCount, List<String> parsingNotes, List<TransactionDto> importedTransactions) {
        this.importedCount = importedCount;
        this.skippedCount = skippedCount;
        this.parsingNotes = parsingNotes;
        this.importedTransactions = importedTransactions;
    }

    public int getImportedCount() {
        return importedCount;
    }

    public void setImportedCount(int importedCount) {
        this.importedCount = importedCount;
    }

    public int getSkippedCount() {
        return skippedCount;
    }

    public void setSkippedCount(int skippedCount) {
        this.skippedCount = skippedCount;
    }

    public List<String> getParsingNotes() {
        return parsingNotes;
    }

    public void setParsingNotes(List<String> parsingNotes) {
        this.parsingNotes = parsingNotes;
    }

    public List<TransactionDto> getImportedTransactions() {
        return importedTransactions;
    }

    public void setImportedTransactions(List<TransactionDto> importedTransactions) {
        this.importedTransactions = importedTransactions;
    }
}

