package com.ln.fitness.activity_service.service;

import com.ln.fitness.activity_service.dto.ActivityRequest;
import com.ln.fitness.activity_service.dto.ActivityResponse;
import com.ln.fitness.activity_service.model.Activity;

import java.util.List;

public interface IActivityService {
    ActivityResponse saveActivity(ActivityRequest request);

     ActivityResponse mapEntityToActivityResponse(Activity entity);

    List<ActivityResponse> getUserActivity(String userId);

    ActivityResponse getActivity(String activityId);

    void deleteActivity(String activityId);
}
