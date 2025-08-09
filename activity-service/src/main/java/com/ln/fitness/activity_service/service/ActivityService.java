package com.ln.fitness.activity_service.service;

import com.ln.fitness.activity_service.dto.ActivityRequest;
import com.ln.fitness.activity_service.dto.ActivityResponse;
import com.ln.fitness.activity_service.excption.ActivityNotFoundExcpetion;
import com.ln.fitness.activity_service.excption.UserActivityNotFoundException;
import com.ln.fitness.activity_service.excption.UserNotFoundException;
import com.ln.fitness.activity_service.model.Activity;
import com.ln.fitness.activity_service.repository.IActivityRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService implements IActivityService{

final private IActivityRepository repository;
final private UserValidationService userValidationService;
final private RabbitTemplate rabbitTemplate;

@Value("${rabbitmq.exchange.name}")
private  String exchange;
@Value("${rabbitmq.routing.key}")
private  String routingKey;

    @Override
    public ActivityResponse saveActivity(ActivityRequest request) {


        boolean validateUser= userValidationService.validateUser(request.getUserId());
        if(!validateUser){
            throw new UserNotFoundException("Invalid User "+request.getUserId());
        }
        Activity activity=Activity.builder()
                .userId(request.getUserId())
                .type(request.getType())
                .duration(request.getDuration())
                .caloriesBurned(request.getCaloriesBurned())
                .startedTime(request.getStartedTime())
                .additionalMetrics(request.getAdditionalMetrics())
                .build();

        Activity savedActivity=repository.save(activity);
        try{
            rabbitTemplate.convertAndSend(exchange,routingKey,activity);
        } catch (Exception e) {
            log.error("Failed to publish activity to rabbitmq{}",e);

        }

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

    @Override
    public void deleteActivity(String activityId) {
       repository.deleteById(activityId);
    }
}
