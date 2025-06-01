package com.ln.fitness.ai_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RecommendationControllerAdvice {

    @ExceptionHandler(ActivityRecommendationNotFoundException.class)
    public ResponseEntity<String> handleActivityRecommendationExceptions(ActivityRecommendationNotFoundException ex){

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    @ExceptionHandler(UserRecommendationNotFoundException.class)
    public ResponseEntity<String> handleUserRecommendationExceptions(UserRecommendationNotFoundException ex){

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex){

        return ResponseEntity.internalServerError().body(ex.getMessage());
    }
}
