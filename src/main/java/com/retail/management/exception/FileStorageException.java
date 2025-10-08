package com.retail.management.exception;

import org.springframework.http.HttpStatus;

public class FileStorageException extends BaseException {

    public FileStorageException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, "FILE_STORAGE_ERROR");
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause, HttpStatus.INTERNAL_SERVER_ERROR, "FILE_STORAGE_ERROR");
    }
}