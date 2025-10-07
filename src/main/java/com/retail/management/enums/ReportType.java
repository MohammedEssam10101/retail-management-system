package com.retail.management.enums;

import lombok.Getter;

@Getter
public enum ReportType {
    DAILY_SALES("Daily Sales", "Daily sales report"),
    WEEKLY_SALES("Weekly Sales", "Weekly sales report"),
    MONTHLY_SALES("Monthly Sales", "Monthly sales report"),
    YEARLY_SALES("Yearly Sales", "Yearly sales report"),
    PRODUCT_PERFORMANCE("Product Performance", "Top selling products report"),
    BRANCH_PERFORMANCE("Branch Performance", "Branch comparison report"),
    CASHIER_PERFORMANCE("Cashier Performance", "Cashier performance report"),
    TAX_SUMMARY("Tax Summary", "Tax collection summary"),
    DISCOUNT_SUMMARY("Discount Summary", "Discount usage summary"),
    PAYMENT_METHODS("Payment Methods", "Payment methods breakdown"),
    STOCK_LEVEL("Stock Level", "Current stock levels"),
    LOW_STOCK("Low Stock", "Low stock alert report"),
    CUSTOMER_PURCHASE("Customer Purchase", "Customer purchase history"),
    RETURNS("Returns", "Returns and refunds report"),
    PROFIT_LOSS("Profit & Loss", "Profit and loss statement");

    private final String displayName;
    private final String description;

    ReportType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public static ReportType fromString(String type) {
        for (ReportType reportType : ReportType.values()) {
            if (reportType.name().equalsIgnoreCase(type)) {
                return reportType;
            }
        }
        throw new IllegalArgumentException("Invalid report type: " + type);
    }

    public boolean isTimeBased() {
        return this == DAILY_SALES || this == WEEKLY_SALES ||
                this == MONTHLY_SALES || this == YEARLY_SALES;
    }
}
