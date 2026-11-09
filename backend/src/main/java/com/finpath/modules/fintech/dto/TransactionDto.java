package com.finpath.modules.fintech.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class TransactionDto {
    private UUID id;
    private UUID accountId;
    private String accountName;
    private BigDecimal amount;
    private String currency;
    private LocalDate transactionDate;
    private String merchantName;
    private String category;
    private boolean isRecurring;
    private String status;

    public TransactionDto() {}

    public TransactionDto(UUID id, UUID accountId, String accountName, BigDecimal amount, String currency, LocalDate transactionDate, String merchantName, String category, boolean isRecurring, String status) {
        this.id = id;
        this.accountId = accountId;
        this.accountName = accountName;
        this.amount = amount;
        this.currency = currency;
        this.transactionDate = transactionDate;
        this.merchantName = merchantName;
        this.category = category;
        this.isRecurring = isRecurring;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

