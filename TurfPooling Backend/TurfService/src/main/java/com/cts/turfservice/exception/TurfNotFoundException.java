package com.cts.turfservice.exception;


public class TurfNotFoundException extends RuntimeException {
    public TurfNotFoundException(String message) {
        super(message);
    }
}