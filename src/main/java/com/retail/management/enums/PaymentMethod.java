package com.retail.management.enums;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    CASH("Cash", "Cash payment"),
    CREDIT_CARD("Credit Card", "Credit card payment"),
    DEBIT_CARD("Debit Card", "Debit card payment"),
    DIGITAL_WALLET("Digital Wallet", "Mobile wallet payment (e.g., Apple Pay, Google Pay)"),
    BANK_TRANSFER("Bank Transfer", "Direct bank transfer"),
    MOBILE_MONEY("Mobile Money", "Mobile money services (e.g., Vodafone Cash, Orange Money)"),
    CHECK("Check", "Payment by check"),
    GIFT_CARD("Gift Card", "Gift card or voucher payment");

    private final String displayName;
    private final String description;

    PaymentMethod(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public static PaymentMethod fromString(String method) {
        for (PaymentMethod paymentMethod : PaymentMethod.values()) {
            if (paymentMethod.name().equalsIgnoreCase(method)) {
                return paymentMethod;
            }
        }
        throw new IllegalArgumentException("Invalid payment method: " + method);
    }

    public boolean isElectronic() {
        return this == CREDIT_CARD || this == DEBIT_CARD ||
                this == DIGITAL_WALLET || this == BANK_TRANSFER ||
                this == MOBILE_MONEY;
    }
}