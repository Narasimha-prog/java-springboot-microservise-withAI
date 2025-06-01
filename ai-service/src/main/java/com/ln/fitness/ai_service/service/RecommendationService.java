package com.ln.fitness.ai_service.service;

import com.ln.fitness.ai_service.exception.ActivityRecommendationNotFoundException;
import com.ln.fitness.ai_service.exception.UserRecommendationNotFoundException;
import com.ln.fitness.ai_service.model.Recommendation;
import com.ln.fitness.ai_service.repository.IRecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService implements IRecommendationService{
    final private IRecommendationRepository recommendationRepository;

    @Override
    public List<Recommendation> getUserRecommendations(String userId) {
        List<Recommendation> recommendations = recommendationRepository.findByUserId(userId);

        if (recommendations == null || recommendations.isEmpty()) {
            throw new UserRecommendationNotFoundException("User Recommendations Not found for userId: " + userId);
        }

        return recommendations;
    }

    @Override
    public Recommendation getActivityRecommendations(String activityId) {


        return recommendationRepository.findByActivityId(activityId).orElseThrow(()->new ActivityRecommendationNotFoundException("Activity Not Found For This ActivityId :"+activityId));
    }
}
