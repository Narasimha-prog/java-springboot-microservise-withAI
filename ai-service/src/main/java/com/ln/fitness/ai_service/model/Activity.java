package com.ln.fitness.ai_service.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;


@Data
public class Activity {
    private String id;
    private String userId;
    private String type;
    private Integer duration;
    private Integer caloriesBurned;
    private LocalDateTime startedTime;
    private Map<String,Object> additionalMetrics;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
