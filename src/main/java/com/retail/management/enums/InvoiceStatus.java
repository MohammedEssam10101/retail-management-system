package com.retail.management.enums;

import lombok.Getter;

@Getter
public enum InvoiceStatus {
    DRAFT("Draft", "Invoice is in draft state"),
    PENDING("Pending", "Invoice is pending payment"),
    PARTIALLY_PAID("Partially Paid", "Invoice is partially paid"),
    PAID("Paid", "Invoice is fully paid"),
    OVERDUE("Overdue", "Invoice payment is overdue"),
    CANCELLED("Cancelled", "Invoice was cancelled"),
    REFUNDED("Refunded", "Invoice was fully refunded"),
    VOID("Void", "Invoice is void");

    private final String displayName;
    private final String description;

    InvoiceStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public static InvoiceStatus fromString(String status) {
        for (InvoiceStatus invoiceStatus : InvoiceStatus.values()) {
            if (invoiceStatus.name().equalsIgnoreCase(status)) {
                return invoiceStatus;
            }
        }
        throw new IllegalArgumentException("Invalid invoice status: " + status);
    }

    public boolean canBeModified() {
        return this == DRAFT || this == PENDING;
    }

    public boolean canBeCancelled() {
        return this == DRAFT || this == PENDING || this == PARTIALLY_PAID;
    }

    public boolean isPaid() {
        return this == PAID || this == REFUNDED;
    }
}