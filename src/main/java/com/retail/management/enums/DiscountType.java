package com.retail.management.enums;

import lombok.Getter;

@Getter
public enum DiscountType {
    FIXED_AMOUNT("Fixed Amount", "Fixed amount discount (e.g., 50 EGP off)"),
    PERCENTAGE("Percentage", "Percentage discount (e.g., 10% off)"),
    PROMO_CODE("Promo Code", "Discount applied via promotional code"),
    BOGO("Buy One Get One", "Buy one get one free or similar offers"),
    BUNDLE("Bundle Discount", "Discount on bundled products"),
    SEASONAL("Seasonal Discount", "Seasonal or holiday discount"),
    LOYALTY("Loyalty Discount", "Discount for loyalty program members"),
    VOLUME("Volume Discount", "Discount based on quantity purchased");

    private final String displayName;
    private final String description;

    DiscountType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public static DiscountType fromString(String type) {
        for (DiscountType discountType : DiscountType.values()) {
            if (discountType.name().equalsIgnoreCase(type)) {
                return discountType;
            }
        }
        throw new IllegalArgumentException("Invalid discount type: " + type);
    }

    public boolean requiresPromoCode() {
        return this == PROMO_CODE;
    }

    public boolean isPercentageBased() {
        return this == PERCENTAGE || this == VOLUME;
    }
}
