package com.retail.management.enums;

import lombok.Getter;

@Getter
public enum PromoCodeStatus {
    ACTIVE("Active", "Promo code is active and can be used"),
    EXPIRED("Expired", "Promo code has expired"),
    USED_UP("Used Up", "Promo code usage limit has been reached"),
    DISABLED("Disabled", "Promo code has been manually disabled"),
    SCHEDULED("Scheduled", "Promo code is scheduled for future activation");

    private final String displayName;
    private final String description;

    PromoCodeStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public static PromoCodeStatus fromString(String status) {
        for (PromoCodeStatus promoStatus : PromoCodeStatus.values()) {
            if (promoStatus.name().equalsIgnoreCase(status)) {
                return promoStatus;
            }
        }
        throw new IllegalArgumentException("Invalid promo code status: " + status);
    }

    public boolean canBeUsed() {
        return this == ACTIVE;
    }
}
