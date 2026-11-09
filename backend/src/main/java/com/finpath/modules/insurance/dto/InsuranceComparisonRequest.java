package com.finpath.modules.insurance.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;

public class InsuranceComparisonRequest {

    @NotEmpty(message = "At least two product IDs are required for comparison")
    private List<UUID> productIds;

    public InsuranceComparisonRequest() {}

    public InsuranceComparisonRequest(List<UUID> productIds) {
        this.productIds = productIds;
    }

    public List<UUID> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<UUID> productIds) {
        this.productIds = productIds;
    }
}

