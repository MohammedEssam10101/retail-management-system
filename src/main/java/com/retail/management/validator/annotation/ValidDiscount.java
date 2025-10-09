package com.retail.management.validator.annotation;

import com.retail.management.validator.DiscountValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DiscountValidator.class)
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDiscount {
    String message() default "Invalid discount value";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}