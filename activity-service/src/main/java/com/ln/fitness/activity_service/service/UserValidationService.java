package com.ln.fitness.activity_service.service;

import com.ln.fitness.activity_service.excption.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserValidationService {
    final private WebClient userServiceWebClient;

    public boolean validateUser(String userId){
        log.info("Calling User Validation API for userId {}",userId);
        try {
            return userServiceWebClient.get()
                    .uri("/api/users/{userId}/validate",userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
        } catch (WebClientResponseException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new UserNotFoundException("User Not Found " + userId);
            } else if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new UserNotFoundException("Invalid Request " + userId);
            } else {

                throw new RuntimeException("Error while validating user: " + ex.getMessage(), ex);
            }
        }

    }
}
