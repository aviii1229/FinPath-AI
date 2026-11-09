package com.finpath.modules.fintech.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class DuplicateConfirmationRequest {

    @NotNull(message = "Duplicate transaction ID is required")
    private UUID duplicateTransactionId;

    private boolean confirmed; // true to confirm duplicate, false to dismiss as legitimate distinct charge

    public DuplicateConfirmationRequest() {}

    public DuplicateConfirmationRequest(UUID duplicateTransactionId, boolean confirmed) {
        this.duplicateTransactionId = duplicateTransactionId;
        this.confirmed = confirmed;
    }

    public UUID getDuplicateTransactionId() {
        return duplicateTransactionId;
    }

    public void setDuplicateTransactionId(UUID duplicateTransactionId) {
        this.duplicateTransactionId = duplicateTransactionId;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }
}

