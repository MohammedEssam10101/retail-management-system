package com.retail.management.enums;

import lombok.Getter;

@Getter
public enum NotificationType {
    LOW_STOCK("Low Stock Alert", "Product stock is below threshold"),
    OUT_OF_STOCK("Out of Stock", "Product is out of stock"),
    PAYMENT_RECEIVED("Payment Received", "Payment has been received"),
    INVOICE_CREATED("Invoice Created", "New invoice has been created"),
    RETURN_PROCESSED("Return Processed", "Return has been processed"),
    PROMO_CODE_EXPIRING("Promo Code Expiring", "Promo code is about to expire"),
    USER_CREATED("User Created", "New user account created"),
    PASSWORD_RESET("Password Reset", "Password reset requested"),
    SYSTEM_ALERT("System Alert", "System maintenance or alert");

    private final String displayName;
    private final String description;

    NotificationType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public static NotificationType fromString(String type) {
        for (NotificationType notificationType : NotificationType.values()) {
            if (notificationType.name().equalsIgnoreCase(type)) {
                return notificationType;
            }
        }
        throw new IllegalArgumentException("Invalid notification type: " + type);
    }

    public boolean isUrgent() {
        return this == OUT_OF_STOCK || this == SYSTEM_ALERT;
    }
}
