package com.web.messanger.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {

  @ExceptionHandler(exception = EntityNotFoundException.class)
  public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException exception) {
    ErrorResponse errorResponse = new ErrorResponseException(HttpStatus.NOT_FOUND, exception);
    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(exception = IllegalArgumentException.class)
  public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException exception) {
    ErrorResponse errorResponse = new ErrorResponseException(HttpStatus.BAD_REQUEST, exception);
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(exception = AccessDeniedException.class)
  public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException exception) {
    ErrorResponse errorResponse = new ErrorResponseException(HttpStatus.UNAUTHORIZED, exception);
    return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(exception = IllegalStateException.class)
  public ResponseEntity<?> handleIllegalStateException(IllegalStateException exception) {
    ErrorResponse errorResponse = new ErrorResponseException(HttpStatus.BAD_REQUEST, exception);
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(exception = UnsupportedOperationException.class)
  public ResponseEntity<?> handleIllegalStateException(UnsupportedOperationException exception) {
    ErrorResponse errorResponse = new ErrorResponseException(HttpStatus.BAD_REQUEST, exception);
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }
}
