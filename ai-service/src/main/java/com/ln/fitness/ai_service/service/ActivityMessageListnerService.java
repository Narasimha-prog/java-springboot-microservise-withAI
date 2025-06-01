package com.ln.fitness.ai_service.service;

import com.ln.fitness.ai_service.model.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityMessageListnerService {


    @RabbitListener(queues = "activity.queue")
    public void processActivity(Activity activity){
        log.info("Received The Activity for Processing purpose {}",activity.getId());

    }

}
