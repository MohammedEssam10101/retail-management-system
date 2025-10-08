package com.retail.management.exception;

import org.springframework.http.HttpStatus;

public class PaymentProcessingException extends BaseException {

    public PaymentProcessingException(String message) {
        super(message, HttpStatus.PAYMENT_REQUIRED, "PAYMENT_PROCESSING_ERROR");
    }

    public PaymentProcessingException(String message, Throwable cause) {
        super(message, cause, HttpStatus.PAYMENT_REQUIRED, "PAYMENT_PROCESSING_ERROR");
    }
}