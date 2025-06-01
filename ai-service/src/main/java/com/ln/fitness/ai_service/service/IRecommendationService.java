package com.ln.fitness.ai_service.service;

import com.ln.fitness.ai_service.model.Recommendation;

import java.util.List;

public interface IRecommendationService {
    List<Recommendation> getUserRecommendations(String userId);

    Recommendation getActivityRecommendations(String activityId);
}
