package com.retail.management.enums;

import lombok.Getter;

@Getter
public enum AuditAction {
    CREATE("Create", "Entity was created"),
    UPDATE("Update", "Entity was updated"),
    DELETE("Delete", "Entity was deleted"),
    SOFT_DELETE("Soft Delete", "Entity was soft deleted"),
    RESTORE("Restore", "Entity was restored from soft delete"),
    LOGIN("Login", "User logged in"),
    LOGOUT("Logout", "User logged out"),
    PASSWORD_CHANGE("Password Change", "User changed password"),
    PERMISSION_CHANGE("Permission Change", "User permissions were changed"),
    STOCK_ADJUSTMENT("Stock Adjustment", "Stock was adjusted"),
    INVOICE_CREATE("Invoice Create", "Invoice was created"),
    INVOICE_CANCEL("Invoice Cancel", "Invoice was cancelled"),
    PAYMENT_PROCESS("Payment Process", "Payment was processed"),
    RETURN_PROCESS("Return Process", "Return was processed"),
    DISCOUNT_APPLY("Discount Apply", "Discount was applied"),
    PROMO_CODE_USE("Promo Code Use", "Promo code was used");

    private final String displayName;
    private final String description;

    AuditAction(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public static AuditAction fromString(String action) {
        for (AuditAction auditAction : AuditAction.values()) {
            if (auditAction.name().equalsIgnoreCase(action)) {
                return auditAction;
            }
        }
        throw new IllegalArgumentException("Invalid audit action: " + action);
    }

    public boolean isSecurityRelated() {
        return this == LOGIN || this == LOGOUT ||
                this == PASSWORD_CHANGE || this == PERMISSION_CHANGE;
    }

    public boolean isFinancialTransaction() {
        return this == INVOICE_CREATE || this == PAYMENT_PROCESS ||
                this == RETURN_PROCESS || this == DISCOUNT_APPLY;
    }
}

