package com.retail.management.util;

import org.slf4j.MDC;

import java.util.UUID;

public class CorrelationIdUtil {

    private static final String CORRELATION_ID_KEY = "correlationId";

    /**
     * Generate and set correlation ID
     */
    public static String generateAndSet() {
        String correlationId = UUID.randomUUID().toString();
        MDC.put(CORRELATION_ID_KEY, correlationId);
        return correlationId;
    }

    /**
     * Get current correlation ID
     */
    public static String get() {
        String correlationId = MDC.get(CORRELATION_ID_KEY);
        if (correlationId == null) {
            correlationId = generateAndSet();
        }
        return correlationId;
    }

    /**
     * Set correlation ID
     */
    public static void set(String correlationId) {
        MDC.put(CORRELATION_ID_KEY, correlationId);
    }

    /**
     * Clear correlation ID
     */
    public static void clear() {
        MDC.remove(CORRELATION_ID_KEY);
    }
}