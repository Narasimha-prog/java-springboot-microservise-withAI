package com.ln.fitness.gateway.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String userNotFound) {
        super(userNotFound);
    }
}
