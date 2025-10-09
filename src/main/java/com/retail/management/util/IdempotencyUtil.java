package com.retail.management.util;

import java.util.UUID;

public class IdempotencyUtil {

    /**
     * Generate idempotency key
     */
    public static String generateKey() {
        return UUID.randomUUID().toString();
    }

    /**
     * Generate idempotency key with prefix
     */
    public static String generateKey(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString();
    }

    /**
     * Validate idempotency key format
     */
    public static boolean isValidKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            return false;
        }

        try {
            // Check if it's a valid UUID
            String uuidPart = key.contains("-") ?
                    key.substring(key.lastIndexOf("-") + 1) : key;
            UUID.fromString(uuidPart);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}