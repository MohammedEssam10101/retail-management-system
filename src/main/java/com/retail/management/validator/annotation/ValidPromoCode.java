package com.retail.management.validator.annotation;

import com.retail.management.validator.PromoCodeValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PromoCodeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPromoCode {
    String message() default "Invalid promo code";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}