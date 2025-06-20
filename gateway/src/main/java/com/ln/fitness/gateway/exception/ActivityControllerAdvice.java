package com.ln.fitness.gateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ActivityControllerAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> allExceptions(Exception ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }



    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> allExceptions(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }
}
