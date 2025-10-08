package com.retail.management.exception;

import org.springframework.http.HttpStatus;

public class InvalidPromoCodeException extends BaseException {

    public InvalidPromoCodeException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INVALID_PROMO_CODE");
    }
}