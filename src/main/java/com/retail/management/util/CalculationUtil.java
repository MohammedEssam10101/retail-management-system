package com.retail.management.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CalculationUtil {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    /**
     * Calculate tax amount from price and tax rate
     */
    public static BigDecimal calculateTax(BigDecimal price, BigDecimal taxRate) {
        if (price == null || taxRate == null) {
            return BigDecimal.ZERO;
        }

        return price.multiply(taxRate)
                .divide(new BigDecimal("100"), SCALE, ROUNDING_MODE);
    }

    /**
     * Calculate price with tax
     */
    public static BigDecimal calculatePriceWithTax(BigDecimal price, BigDecimal taxRate) {
        if (price == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal taxAmount = calculateTax(price, taxRate);
        return price.add(taxAmount).setScale(SCALE, ROUNDING_MODE);
    }

    /**
     * Calculate fixed discount
     */
    public static BigDecimal calculateFixedDiscount(BigDecimal amount, BigDecimal discountValue) {
        if (amount == null || discountValue == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount = discountValue.min(amount);
        return discount.setScale(SCALE, ROUNDING_MODE);
    }

    /**
     * Calculate percentage discount
     */
    public static BigDecimal calculatePercentageDiscount(BigDecimal amount, BigDecimal percentage) {
        if (amount == null || percentage == null) {
            return BigDecimal.ZERO;
        }

        return amount.multiply(percentage)
                .divide(new BigDecimal("100"), SCALE, ROUNDING_MODE);
    }

    /**
     * Calculate percentage discount with max limit
     */
    public static BigDecimal calculatePercentageDiscountWithMax(
            BigDecimal amount, BigDecimal percentage, BigDecimal maxDiscount) {

        BigDecimal discount = calculatePercentageDiscount(amount, percentage);

        if (maxDiscount != null && discount.compareTo(maxDiscount) > 0) {
            return maxDiscount.setScale(SCALE, ROUNDING_MODE);
        }

        return discount;
    }

    /**
     * Calculate line total (quantity * unit price)
     */
    public static BigDecimal calculateLineTotal(Integer quantity, BigDecimal unitPrice) {
        if (quantity == null || unitPrice == null) {
            return BigDecimal.ZERO;
        }

        return unitPrice.multiply(new BigDecimal(quantity))
                .setScale(SCALE, ROUNDING_MODE);
    }

    /**
     * Calculate line total with tax and discount
     */
    public static BigDecimal calculateLineTotalWithTaxAndDiscount(
            Integer quantity, BigDecimal unitPrice,
            BigDecimal taxRate, BigDecimal discountAmount) {

        BigDecimal lineTotal = calculateLineTotal(quantity, unitPrice);
        BigDecimal taxAmount = calculateTax(lineTotal, taxRate);
        BigDecimal discount = discountAmount != null ? discountAmount : BigDecimal.ZERO;

        return lineTotal.add(taxAmount).subtract(discount)
                .setScale(SCALE, ROUNDING_MODE);
    }

    /**
     * Calculate percentage
     */
    public static BigDecimal calculatePercentage(BigDecimal part, BigDecimal total) {
        if (total == null || total.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return part.multiply(new BigDecimal("100"))
                .divide(total, SCALE, ROUNDING_MODE);
    }

    /**
     * Round to 2 decimal places
     */
    public static BigDecimal round(BigDecimal value) {
        return value != null ? value.setScale(SCALE, ROUNDING_MODE) : BigDecimal.ZERO;
    }
}