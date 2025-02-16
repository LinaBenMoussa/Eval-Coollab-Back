package com.example.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // Code HTTP 400
    public Map<String, String> handleApplicationException(ApplicationException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Error");
        errorResponse.put("message", ex.getMessage());
        return errorResponse;
    }}
