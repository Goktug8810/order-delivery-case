package com.goktug.order_delivery.exception;

import java.time.LocalDateTime;

public class CustomRestError {
    private LocalDateTime timestamp;
    private String message;
    private int statusCode;
    private String path;

    public CustomRestError(String message, int statusCode, String path) {
        this.timestamp = LocalDateTime.now();
        this.message = message;
        this.statusCode = statusCode;
        this.path = path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getPath() {
        return path;
    }
}

