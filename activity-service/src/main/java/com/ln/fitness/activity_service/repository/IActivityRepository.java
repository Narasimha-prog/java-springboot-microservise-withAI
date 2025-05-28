package com.ln.fitness.activity_service.repository;

import com.ln.fitness.activity_service.dto.ActivityRequest;
import com.ln.fitness.activity_service.dto.ActivityResponse;
import com.ln.fitness.activity_service.model.Activity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IActivityRepository extends MongoRepository<Activity,String> {

    List<Activity> findByUserId(String userId);
}
