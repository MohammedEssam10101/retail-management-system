package com.retail.management.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends BaseException {

    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN, "FORBIDDEN");
    }

    public ForbiddenException() {
        super("You don't have permission to access this resource",
                HttpStatus.FORBIDDEN,
                "FORBIDDEN");
    }
}