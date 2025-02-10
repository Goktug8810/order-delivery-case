package com.goktug.order_delivery.exception;

public abstract class BaseCustomException extends RuntimeException {
    private final String customMessage;
    private final int statusCode;

    public BaseCustomException(String customMessage, int statusCode) {
        super(customMessage);
        this.customMessage = customMessage;
        this.statusCode = statusCode;
    }

    public String getCustomMessage() {
        return customMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

