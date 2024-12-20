package com.epic.retailer_service.exception;

public class OrderRequestNotFoundException extends RuntimeException{
    public OrderRequestNotFoundException(String message){
        super(message);
    }
}
