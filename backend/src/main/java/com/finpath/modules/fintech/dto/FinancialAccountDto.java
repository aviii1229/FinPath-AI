package com.finpath.modules.fintech.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class FinancialAccountDto {
    private UUID id;
    private String institutionName;
    private String accountType;
    private String accountMask;
    private String currency;
    private BigDecimal currentBalance;
    private BigDecimal availableBalance;
    private boolean isConnected;
    private Instant lastSyncedAt;

    public FinancialAccountDto() {}

    public FinancialAccountDto(UUID id, String institutionName, String accountType, String accountMask, String currency, BigDecimal currentBalance, BigDecimal availableBalance, boolean isConnected, Instant lastSyncedAt) {
        this.id = id;
        this.institutionName = institutionName;
        this.accountType = accountType;
        this.accountMask = accountMask;
        this.currency = currency;
        this.currentBalance = currentBalance;
        this.availableBalance = availableBalance;
        this.isConnected = isConnected;
        this.lastSyncedAt = lastSyncedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccountMask() {
        return accountMask;
    }

    public void setAccountMask(String accountMask) {
        this.accountMask = accountMask;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(BigDecimal availableBalance) {
        this.availableBalance = availableBalance;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public Instant getLastSyncedAt() {
        return lastSyncedAt;
    }

    public void setLastSyncedAt(Instant lastSyncedAt) {
        this.lastSyncedAt = lastSyncedAt;
    }
}

