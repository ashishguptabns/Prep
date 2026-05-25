package com.example.rackapp.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
        ErrorResponse response = new ErrorResponse(ex.getStatus().value(), ex.getStatus().getReasonPhrase(), ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception ex) {
        ErrorResponse response = new ErrorResponse(500, "Internal Server Error", ex.getMessage());
        return ResponseEntity.internalServerError().body(response);
    }
}
