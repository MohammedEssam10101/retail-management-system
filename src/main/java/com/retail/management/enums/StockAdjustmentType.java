package com.retail.management.enums;

import lombok.Getter;

@Getter
public enum StockAdjustmentType {
    RESTOCK("Restock", "Adding new stock", true),
    SALE("Sale", "Stock sold through invoice", false),
    DAMAGE("Damage", "Damaged stock removal", false),
    THEFT("Theft", "Stock lost due to theft", false),
    RETURN("Return", "Stock returned by customer", true),
    TRANSFER_IN("Transfer In", "Stock transferred from another branch", true),
    TRANSFER_OUT("Transfer Out", "Stock transferred to another branch", false),
    EXPIRED("Expired", "Expired stock removal", false),
    ADJUSTMENT("Adjustment", "Manual stock adjustment", true), // Can be +/-
    INITIAL_STOCK("Initial Stock", "Initial stock entry", true);

    private final String displayName;
    private final String description;
    private final boolean increasesStock;

    StockAdjustmentType(String displayName, String description, boolean increasesStock) {
        this.displayName = displayName;
        this.description = description;
        this.increasesStock = increasesStock;
    }

    public static StockAdjustmentType fromString(String type) {
        for (StockAdjustmentType adjustmentType : StockAdjustmentType.values()) {
            if (adjustmentType.name().equalsIgnoreCase(type)) {
                return adjustmentType;
            }
        }
        throw new IllegalArgumentException("Invalid stock adjustment type: " + type);
    }

    public boolean decreasesStock() {
        return !increasesStock;
    }

    public boolean requiresApproval() {
        return this == DAMAGE || this == THEFT || this == EXPIRED || this == ADJUSTMENT;
    }
}