package com.cts.turfservice.exception;


public class TurfMappingException extends RuntimeException {
    public TurfMappingException(String message,Exception e) {
        super(message,e);
    }
}