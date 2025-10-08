package com.retail.management.exception;

import org.springframework.http.HttpStatus;

public class ConcurrencyException extends BaseException {

    public ConcurrencyException(String message) {
        super(message, HttpStatus.CONFLICT, "CONCURRENCY_CONFLICT");
    }
}