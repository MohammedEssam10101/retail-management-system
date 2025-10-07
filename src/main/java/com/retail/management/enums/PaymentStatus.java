package com.retail.management.enums;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    PENDING("Pending", "Payment is pending confirmation"),
    PROCESSING("Processing", "Payment is being processed"),
    COMPLETED("Completed", "Payment completed successfully"),
    FAILED("Failed", "Payment failed"),
    CANCELLED("Cancelled", "Payment was cancelled"),
    REFUNDED("Refunded", "Payment was refunded"),
    PARTIALLY_REFUNDED("Partially Refunded", "Payment was partially refunded");

    private final String displayName;
    private final String description;

    PaymentStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public static PaymentStatus fromString(String status) {
        for (PaymentStatus paymentStatus : PaymentStatus.values()) {
            if (paymentStatus.name().equalsIgnoreCase(status)) {
                return paymentStatus;
            }
        }
        throw new IllegalArgumentException("Invalid payment status: " + status);
    }

    public boolean isFinalState() {
        return this == COMPLETED || this == FAILED ||
                this == CANCELLED || this == REFUNDED;
    }
}