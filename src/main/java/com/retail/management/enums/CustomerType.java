package com.retail.management.enums;

import lombok.Getter;

@Getter
public enum CustomerType {
    REGISTERED("Registered", "Registered customer with account"),
    WALK_IN("Walk-in", "Walk-in customer without account"),
    WHOLESALE("Wholesale", "Wholesale customer"),
    CORPORATE("Corporate", "Corporate customer");

    private final String displayName;
    private final String description;

    CustomerType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public static CustomerType fromString(String type) {
        for (CustomerType customerType : CustomerType.values()) {
            if (customerType.name().equalsIgnoreCase(type)) {
                return customerType;
            }
        }
        throw new IllegalArgumentException("Invalid customer type: " + type);
    }

    public boolean hasLoyaltyProgram() {
        return this == REGISTERED || this == WHOLESALE || this == CORPORATE;
    }
}
