package com.web.messanger.controller;

import java.time.LocalTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.persistence.EntityNotFoundException;

@ControllerAdvice
public class MyExceptionHandler {
    
    @ExceptionHandler(exception = EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException exception) {
        ErrorResponse errorResponse = new ErrorResponseException(HttpStatus.NOT_FOUND, exception);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
