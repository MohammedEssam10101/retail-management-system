package com.retail.management.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("Administrator", "Full system access with all privileges"),
    MANAGER("Manager", "Branch-level management and reporting access"),
    CASHIER("Cashier", "Point of sale and customer service operations");

    private final String displayName;
    private final String description;

    UserRole(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public static UserRole fromString(String role) {
        for (UserRole userRole : UserRole.values()) {
            if (userRole.name().equalsIgnoreCase(role)) {
                return userRole;
            }
        }
        throw new IllegalArgumentException("Invalid user role: " + role);
    }
}