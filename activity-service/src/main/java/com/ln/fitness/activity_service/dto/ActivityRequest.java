package com.ln.fitness.activity_service.dto;

import com.ln.fitness.activity_service.model.ActivityType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ActivityRequest {
    private String userId;
    private ActivityType type;
    private Integer duration;
    private Integer caloriesBurned;
    private LocalDateTime startedTime;
    private Map<String,Object> additionalMetrics;
}
