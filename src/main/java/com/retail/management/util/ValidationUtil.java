package com.retail.management.util;

import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^(\\+20|0)?1[0-2,5]{1}[0-9]{8}$");

    private static final Pattern ALPHA_NUMERIC_PATTERN =
            Pattern.compile("^[a-zA-Z0-9]+$");

    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        return StringUtils.hasText(email) && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validate phone number
     */
    public static boolean isValidPhone(String phone) {
        if (!StringUtils.hasText(phone)) {
            return false;
        }

        String cleanPhone = phone.replaceAll("[\\s-]", "");
        return PHONE_PATTERN.matcher(cleanPhone).matches();
    }

    /**
     * Validate alphanumeric string
     */
    public static boolean isAlphaNumeric(String str) {
        return StringUtils.hasText(str) && ALPHA_NUMERIC_PATTERN.matcher(str).matches();
    }

    /**
     * Sanitize input string
     */
    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }

        // Remove potentially dangerous characters
        return input.replaceAll("[<>\"']", "");
    }

    /**
     * Check if string contains only letters
     */
    public static boolean isAlpha(String str) {
        return StringUtils.hasText(str) && str.matches("^[a-zA-Z]+$");
    }

    /**
     * Check if string is numeric
     */
    public static boolean isNumeric(String str) {
        return StringUtils.hasText(str) && str.matches("^[0-9]+$");
    }
}