package com.retail.management.validator;

import com.retail.management.validator.annotation.ValidPhone;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PhoneValidator implements ConstraintValidator<ValidPhone, String> {

    // Egyptian phone number pattern: starts with +20 or 0, followed by 10 digits
    private static final String PHONE_PATTERN = "^(\\+20|0)?1[0-2,5]{1}[0-9]{8}$";
    private static final Pattern pattern = Pattern.compile(PHONE_PATTERN);

    @Override
    public void initialize(ValidPhone constraintAnnotation) {
        // Initialization logic if needed
    }

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        if (phone == null || phone.isEmpty()) {
            return true; // Use @NotBlank for null/empty check
        }

        // Remove spaces and dashes
        String cleanPhone = phone.replaceAll("[\\s-]", "");

        return pattern.matcher(cleanPhone).matches();
    }
}