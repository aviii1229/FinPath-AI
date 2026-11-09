package com.finpath.modules.fintech.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public class AutoCategorizeRequest {

    @NotBlank(message = "Merchant name is required")
    private String merchantName;

    private BigDecimal amount;

    public AutoCategorizeRequest() {}

    public AutoCategorizeRequest(String merchantName, BigDecimal amount) {
        this.merchantName = merchantName;
        this.amount = amount;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}

