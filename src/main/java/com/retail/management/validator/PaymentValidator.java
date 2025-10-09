package com.retail.management.validator;

import com.retail.management.entity.Invoice;
import com.retail.management.enums.InvoiceStatus;
import com.retail.management.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaymentValidator {

    /**
     * Validate if payment can be processed for an invoice
     */
    public void validatePayment(Invoice invoice, BigDecimal paymentAmount) {
        // Check if invoice can receive payments
        if (invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new BusinessException("Cannot process payment for cancelled invoice");
        }

        if (invoice.getStatus() == InvoiceStatus.REFUNDED) {
            throw new BusinessException("Cannot process payment for refunded invoice");
        }

        // Check payment amount
        if (paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Payment amount must be greater than 0");
        }

        // Check if payment exceeds outstanding balance
        if (paymentAmount.compareTo(invoice.getOutstandingBalance()) > 0) {
            throw new BusinessException(
                    String.format("Payment amount (%.2f) exceeds outstanding balance (%.2f)",
                            paymentAmount, invoice.getOutstandingBalance())
            );
        }
    }
}