package com.ln.fitness.activity_service.service;

import com.ln.fitness.activity_service.dto.ActivityRequest;
import com.ln.fitness.activity_service.dto.ActivityResponse;
import com.ln.fitness.activity_service.excption.ActivityNotFoundExcpetion;
import com.ln.fitness.activity_service.excption.UserActivityNotFoundException;
import com.ln.fitness.activity_service.model.Activity;
import com.ln.fitness.activity_service.repository.IActivityRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityService implements IActivityService{

final private IActivityRepository repository;
    @Override
    public ActivityResponse trackActivity(ActivityRequest request) {
        Activity activity=Activity.builder()
                .userId(request.getUserId())
                .type(request.getType())
                .duration(request.getDuration())
                .caloriesBurned(request.getCaloriesBurned())
                .startedTime(request.getStartedTime())
                .additionalMetrics(request.getAdditionalMetrics())
                .build();

        Activity savedActivity=repository.save(activity);


        return mapEntityToActivityResponse(savedActivity);
    }

    public ActivityResponse mapEntityToActivityResponse(Activity entity){
        return ActivityResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .caloriesBurned(entity.getCaloriesBurned())
                .startedTime(entity.getStartedTime())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .additionalMetrics(entity.getAdditionalMetrics())
                .type(entity.getType())
                .duration(entity.getDuration())
                .build();
    }

    @Override
    public List<ActivityResponse> getUserActivity(String userId) {
       List<Activity> activities= repository.findByUserId(userId);
       if(activities == null || activities.isEmpty()){
           throw new UserActivityNotFoundException("UserActivity Not Found For This ID :"+userId);
       }

        return activities.stream()
                .map(this::mapEntityToActivityResponse)
                .toList();
    }

    @Override
    public ActivityResponse getActivity(String activityId) {
        return repository.findById(activityId)
                .map(this::mapEntityToActivityResponse)
                .orElseThrow(()->new ActivityNotFoundExcpetion("Activity Not Found For this Id :"+activityId));

    }
}
