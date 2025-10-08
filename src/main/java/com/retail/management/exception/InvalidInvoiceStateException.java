package com.retail.management.exception;

import org.springframework.http.HttpStatus;

public class InvalidInvoiceStateException extends BaseException {

    public InvalidInvoiceStateException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INVALID_INVOICE_STATE");
    }
}