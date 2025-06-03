package com.ln.fitness.ai_service.controller;

import com.ln.fitness.ai_service.model.Recommendation;
import com.ln.fitness.ai_service.service.IRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommendations")
public class RecommendationController {

    final private IRecommendationService recommendationService;


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Recommendation>> getUserRecommendations(@PathVariable String userId){
        return ResponseEntity.ok(recommendationService.getUserRecommendations(userId));
    }

    @GetMapping("/activity/{activityId}")
    public ResponseEntity<Recommendation> getActivityRecommendations(@PathVariable String activityId){
        return ResponseEntity.ok(recommendationService.getActivityRecommendations(activityId));
    }
}
