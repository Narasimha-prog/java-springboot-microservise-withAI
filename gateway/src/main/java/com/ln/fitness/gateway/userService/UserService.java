package com.ln.fitness.gateway.userService;


import com.ln.fitness.gateway.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    final private WebClient userServiceWebClient;

    public Mono<Boolean> validateUser(String userId){
        log.info("Calling User Validation API for userId {}",userId);
            return userServiceWebClient.get()
                    .uri("/api/users/{userId}/validate",userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .onErrorResume(WebClientResponseException.class,ex ->
                    {
                        if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                            return Mono.error(new UserNotFoundException("User Not Found " + userId));
                        } else if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                            return Mono.error(new UserNotFoundException("Invalid Request " + userId));
                        } else {

                            return Mono.error( new RuntimeException("Error while validating user: " + ex.getMessage()));
                        }
                    });


    }
}
