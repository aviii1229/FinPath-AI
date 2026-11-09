package com.finpath.modules.fintech.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class CreateTransactionRequest {

    @NotNull(message = "Account ID is required")
    private UUID accountId;

    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    private LocalDate transactionDate;

    @NotBlank(message = "Merchant name is required")
    private String merchantName;

    @NotBlank(message = "Category is required")
    private String category;

    private boolean isRecurring = false;

    public CreateTransactionRequest() {}

    public CreateTransactionRequest(UUID accountId, BigDecimal amount, LocalDate transactionDate, String merchantName, String category, boolean isRecurring) {
        this.accountId = accountId;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.merchantName = merchantName;
        this.category = category;
        this.isRecurring = isRecurring;
    }

    public CreateTransactionRequest(String accountId, BigDecimal amount, LocalDate transactionDate, String merchantName, String category, boolean isRecurring) {
        this(UUID.fromString(accountId), amount, transactionDate, merchantName, category, isRecurring);
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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
}

