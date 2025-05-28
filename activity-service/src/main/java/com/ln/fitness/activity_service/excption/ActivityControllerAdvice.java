package com.ln.fitness.activity_service.excption;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ActivityControllerAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> allExceptions(Exception ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(UserActivityNotFoundException.class)
    public ResponseEntity<String> allExceptions(UserActivityNotFoundException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
