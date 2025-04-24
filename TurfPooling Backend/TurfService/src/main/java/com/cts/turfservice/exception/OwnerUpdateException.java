package com.cts.turfservice.exception;


public class OwnerUpdateException extends RuntimeException {
    public OwnerUpdateException(String message,Exception e) {
        super(message);
    }
}