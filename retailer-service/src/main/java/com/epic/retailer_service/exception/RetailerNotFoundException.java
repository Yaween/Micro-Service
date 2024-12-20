package com.epic.retailer_service.exception;

public class RetailerNotFoundException extends RuntimeException{
    public RetailerNotFoundException(String message) {
        super(message);
    }
}
