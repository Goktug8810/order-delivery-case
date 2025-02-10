package com.goktug.order_delivery.exception;

import org.springframework.http.HttpStatus;

public class InvalidOrderDateException extends BaseCustomException {

    public InvalidOrderDateException(String message) {
        super(message, HttpStatus.BAD_REQUEST.value());
    }
}

