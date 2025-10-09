package com.retail.management.validator;

import com.retail.management.dto.request.invoice.ApplyDiscountRequest;
import com.retail.management.enums.DiscountType;
import com.retail.management.validator.annotation.ValidDiscount;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

public class DiscountValidator implements ConstraintValidator<ValidDiscount, ApplyDiscountRequest> {

    @Override
    public void initialize(ValidDiscount constraintAnnotation) {
        // Initialization logic if needed
    }

    @Override
    public boolean isValid(ApplyDiscountRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true;
        }

        DiscountType type = request.getDiscountType();
        BigDecimal value = request.getDiscountValue();
        String promoCode = request.getPromoCode();

        // For PROMO_CODE type, code must be provided
        if (type == DiscountType.PROMO_CODE) {
            if (promoCode == null || promoCode.trim().isEmpty()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                                "Promo code is required for PROMO_CODE discount type")
                        .addConstraintViolation();
                return false;
            }
        }

        // For FIXED_AMOUNT and PERCENTAGE, value must be provided
        if (type == DiscountType.FIXED_AMOUNT || type == DiscountType.PERCENTAGE) {
            if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                                "Discount value must be greater than 0")
                        .addConstraintViolation();
                return false;
            }

            // For percentage, value must be <= 100
            if (type == DiscountType.PERCENTAGE && value.compareTo(new BigDecimal("100")) > 0) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                                "Percentage discount cannot exceed 100%")
                        .addConstraintViolation();
                return false;
            }
        }

        return true;
    }
}