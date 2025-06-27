package com.ln.fitness.gateway.exception;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class ActivityControllerAdvice {


    @ExceptionHandler(WebClientResponseException.class)
    public Mono<ResponseEntity<String>> handleWebClientResponseException(WebClientResponseException ex) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());

        String friendlyMessage = switch (status) {
            case SERVICE_UNAVAILABLE -> "User service is currently unavailable. Please try again later.";
            case NOT_FOUND -> "Requested resource was not found.";
            case BAD_REQUEST -> "Invalid request. Please check your input.";
            default -> "Something went wrong. Please try again.";
        };

        return Mono.just(ResponseEntity.status(status).body(friendlyMessage));
    }
    @ExceptionHandler(UserServiceUnavailableException.class)
    public Mono<ResponseEntity<String>> handleUserServiceUnavailableException(UserServiceUnavailableException ex){
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ex.getMessage()));
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<String>> handleIllegalArgument(IllegalArgumentException ex) {
        return Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Bad Request: " + ex.getMessage()));
    }
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<String>> handleAllExceptions(Exception ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unexpected error: " + ex.getMessage()));
    }




    @ExceptionHandler(UserNotFoundException.class)
    public Mono<ResponseEntity<String>> userNotFoundException(UserNotFoundException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage()));
    }


}
