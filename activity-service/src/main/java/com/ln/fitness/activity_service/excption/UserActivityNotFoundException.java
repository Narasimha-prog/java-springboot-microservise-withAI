package com.ln.fitness.activity_service.excption;

public class UserActivityNotFoundException extends RuntimeException {
    public UserActivityNotFoundException(String msg){
        super(msg);
    }
}
