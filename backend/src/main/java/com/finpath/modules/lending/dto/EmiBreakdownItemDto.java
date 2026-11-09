package com.finpath.modules.lending.dto;

import java.math.BigDecimal;

public class EmiBreakdownItemDto {
    private int installmentNumber;
    private BigDecimal openingBalance;
    private BigDecimal principalPaid;
    private BigDecimal interestPaid;
    private BigDecimal monthlyPayment;
    private BigDecimal closingBalance;

    public EmiBreakdownItemDto() {}

    public EmiBreakdownItemDto(int installmentNumber, BigDecimal openingBalance, BigDecimal principalPaid, BigDecimal interestPaid, BigDecimal monthlyPayment, BigDecimal closingBalance) {
        this.installmentNumber = installmentNumber;
        this.openingBalance = openingBalance;
        this.principalPaid = principalPaid;
        this.interestPaid = interestPaid;
        this.monthlyPayment = monthlyPayment;
        this.closingBalance = closingBalance;
    }

    public int getInstallmentNumber() {
        return installmentNumber;
    }

    public void setInstallmentNumber(int installmentNumber) {
        this.installmentNumber = installmentNumber;
    }

    public BigDecimal getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(BigDecimal openingBalance) {
        this.openingBalance = openingBalance;
    }

    public BigDecimal getPrincipalPaid() {
        return principalPaid;
    }

    public void setPrincipalPaid(BigDecimal principalPaid) {
        this.principalPaid = principalPaid;
    }

    public BigDecimal getInterestPaid() {
        return interestPaid;
    }

    public void setInterestPaid(BigDecimal interestPaid) {
        this.interestPaid = interestPaid;
    }

    public BigDecimal getMonthlyPayment() {
        return monthlyPayment;
    }

    public void setMonthlyPayment(BigDecimal monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    public BigDecimal getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(BigDecimal closingBalance) {
        this.closingBalance = closingBalance;
    }
}

