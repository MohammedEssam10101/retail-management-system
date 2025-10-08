package com.retail.management.exception;

import org.springframework.http.HttpStatus;

public class InsufficientStockException extends BaseException {

    public InsufficientStockException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INSUFFICIENT_STOCK");
    }

    public InsufficientStockException(String productName, int available, int requested) {
        super(String.format("Insufficient stock for product '%s'. Available: %d, Requested: %d",
                        productName, available, requested),
                HttpStatus.BAD_REQUEST,
                "INSUFFICIENT_STOCK");
    }
}