package com.retail.management.enums;

import lombok.Getter;

@Getter
public enum ReturnStatus {
    PENDING("Pending", "Return request is pending review"),
    APPROVED("Approved", "Return request has been approved"),
    REJECTED("Rejected", "Return request has been rejected"),
    PROCESSING("Processing", "Return is being processed"),
    COMPLETED("Completed", "Return has been completed and refunded"),
    CANCELLED("Cancelled", "Return request was cancelled");

    private final String displayName;
    private final String description;

    ReturnStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public static ReturnStatus fromString(String status) {
        for (ReturnStatus returnStatus : ReturnStatus.values()) {
            if (returnStatus.name().equalsIgnoreCase(status)) {
                return returnStatus;
            }
        }
        throw new IllegalArgumentException("Invalid return status: " + status);
    }

    public boolean isFinalState() {
        return this == COMPLETED || this == REJECTED || this == CANCELLED;
    }

    public boolean canBeModified() {
        return this == PENDING;
    }
}
