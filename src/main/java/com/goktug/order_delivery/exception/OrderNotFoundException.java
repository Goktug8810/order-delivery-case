package com.goktug.order_delivery.exception;

import org.springframework.http.HttpStatus;

public class OrderNotFoundException extends BaseCustomException {

    public OrderNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND.value());
    }
}
