package com.retail.management.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class InvoiceNumberGenerator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * Generate invoice number
     * Format: INV-YYYYMMDD-BRANCHCODE-SEQUENCE
     * Example: INV-20251008-BR01-00001
     */
    public static String generate(String branchCode, Long sequence) {
        String date = LocalDate.now().format(DATE_FORMAT);
        String sequenceStr = String.format("%05d", sequence);
        return String.format("INV-%s-%s-%s", date, branchCode, sequenceStr);
    }

    /**
     * Generate return number
     * Format: RET-YYYYMMDD-BRANCHCODE-SEQUENCE
     * Example: RET-20251008-BR01-00001
     */
    public static String generateReturnNumber(String branchCode, Long sequence) {
        String date = LocalDate.now().format(DATE_FORMAT);
        String sequenceStr = String.format("%05d", sequence);
        return String.format("RET-%s-%s-%s", date, branchCode, sequenceStr);
    }

    /**
     * Generate customer code
     * Format: CUST-YYYYMMDD-SEQUENCE
     * Example: CUST-20251008-00001
     */
    public static String generateCustomerCode(Long sequence) {
        String date = LocalDate.now().format(DATE_FORMAT);
        String sequenceStr = String.format("%05d", sequence);
        return String.format("CUST-%s-%s", date, sequenceStr);
    }
}