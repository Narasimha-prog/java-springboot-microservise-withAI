package com.ln.fitness.user_service.exception;

public class EmailAlreadyExistedException extends RuntimeException{
    public EmailAlreadyExistedException(String message) {
        super(message);
    }

}
