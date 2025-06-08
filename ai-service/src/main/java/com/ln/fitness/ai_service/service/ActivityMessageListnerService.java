package com.ln.fitness.ai_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ln.fitness.ai_service.model.Activity;
import com.ln.fitness.ai_service.model.Recommendation;
import com.ln.fitness.ai_service.repository.IRecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityMessageListnerService {

   private final ActivityAIService activityAIService;
    private final IRecommendationRepository recommendationRepository;

    @RabbitListener(queues = "activity.queue")
    public void processActivity(Activity activity) throws JsonProcessingException {
        log.info("Received The Activity for Processing purpose {}",activity.getId());
        //log.info("Generated Recommendation : {}",activityAIService.generateRecommendation(activity));
     Recommendation recommendation= activityAIService.generateRecommendation(activity);
     recommendationRepository.save(recommendation);
    }

}
