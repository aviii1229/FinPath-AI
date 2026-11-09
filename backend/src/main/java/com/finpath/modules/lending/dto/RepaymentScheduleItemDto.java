package com.finpath.modules.lending.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RepaymentScheduleItemDto {
    private int installmentNumber;
    private LocalDate dueDate;
    private BigDecimal principalDue;
    private BigDecimal interestDue;
    private BigDecimal totalPaymentDue;
    private BigDecimal remainingBalance;
    private String status;

    public RepaymentScheduleItemDto() {}

    public RepaymentScheduleItemDto(int installmentNumber, LocalDate dueDate, BigDecimal principalDue, BigDecimal interestDue, BigDecimal totalPaymentDue, BigDecimal remainingBalance, String status) {
        this.installmentNumber = installmentNumber;
        this.dueDate = dueDate;
        this.principalDue = principalDue;
        this.interestDue = interestDue;
        this.totalPaymentDue = totalPaymentDue;
        this.remainingBalance = remainingBalance;
        this.status = status;
    }

    public int getInstallmentNumber() {
        return installmentNumber;
    }

    public void setInstallmentNumber(int installmentNumber) {
        this.installmentNumber = installmentNumber;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getPrincipalDue() {
        return principalDue;
    }

    public void setPrincipalDue(BigDecimal principalDue) {
        this.principalDue = principalDue;
    }

    public BigDecimal getInterestDue() {
        return interestDue;
    }

    public void setInterestDue(BigDecimal interestDue) {
        this.interestDue = interestDue;
    }

    public BigDecimal getTotalPaymentDue() {
        return totalPaymentDue;
    }

    public void setTotalPaymentDue(BigDecimal totalPaymentDue) {
        this.totalPaymentDue = totalPaymentDue;
    }

    public BigDecimal getRemainingBalance() {
        return remainingBalance;
    }

    public void setRemainingBalance(BigDecimal remainingBalance) {
        this.remainingBalance = remainingBalance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

