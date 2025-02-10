package com.goktug.order_delivery.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseCustomException.class)
    public ResponseEntity<CustomRestError> handleCustomExceptions(BaseCustomException ex, WebRequest request) {
        CustomRestError errorResponse = new CustomRestError(
                ex.getCustomMessage(),
                ex.getStatusCode(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, org.springframework.http.HttpStatus.valueOf(ex.getStatusCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomRestError> handleGenericExceptions(Exception ex, WebRequest request) {
        CustomRestError errorResponse = new CustomRestError(
                "An unexpected error occurred: " + ex.getMessage(),
                500,
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
