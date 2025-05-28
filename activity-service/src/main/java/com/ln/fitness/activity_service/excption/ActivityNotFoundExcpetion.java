package com.ln.fitness.activity_service.excption;

public class ActivityNotFoundExcpetion extends RuntimeException{
    public ActivityNotFoundExcpetion(String msg){
        super(msg);
    }
}
