package com.cts.turfservice.exception;


public class TurfAlreadyExistsException extends RuntimeException {
    public TurfAlreadyExistsException(String message) {
        super(message);
    }
}