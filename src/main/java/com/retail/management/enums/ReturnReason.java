package com.retail.management.enums;

import lombok.Getter;

@Getter
public enum ReturnReason {
    DEFECTIVE("Defective Product", "Product is defective or damaged"),
    WRONG_ITEM("Wrong Item", "Wrong item was delivered or purchased"),
    NOT_AS_DESCRIBED("Not as Described", "Product does not match description"),
    EXPIRED("Expired", "Product is expired or near expiry"),
    CUSTOMER_REQUEST("Customer Request", "Customer changed their mind"),
    SIZE_ISSUE("Size Issue", "Size does not fit"),
    COLOR_ISSUE("Color Issue", "Color does not match expectation"),
    QUALITY_ISSUE("Quality Issue", "Quality is not satisfactory"),
    DUPLICATE_ORDER("Duplicate Order", "Duplicate order placed by mistake"),
    OTHER("Other", "Other reason");

    private final String displayName;
    private final String description;

    ReturnReason(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public static ReturnReason fromString(String reason) {
        for (ReturnReason returnReason : ReturnReason.values()) {
            if (returnReason.name().equalsIgnoreCase(reason)) {
                return returnReason;
            }
        }
        throw new IllegalArgumentException("Invalid return reason: " + reason);
    }

    public boolean isProductIssue() {
        return this == DEFECTIVE || this == WRONG_ITEM ||
                this == NOT_AS_DESCRIBED || this == EXPIRED ||
                this == QUALITY_ISSUE;
    }
}