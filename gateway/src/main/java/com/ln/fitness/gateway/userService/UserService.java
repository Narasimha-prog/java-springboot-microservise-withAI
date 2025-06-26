package com.ln.fitness.gateway.userService;


import com.ln.fitness.gateway.exception.UserNotFoundException;
import com.ln.fitness.gateway.exception.UserServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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


@CircuitBreaker(name = "userValidationService",fallbackMethod = "fallbackValidateUser")
    public Mono<Boolean> validateUser(String userId){
        log.info("Calling User Validation API  for Authentication Using KeyCloakId {}",userId);
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

                            return Mono.error(ex);
                        }
                    });


    }
    @CircuitBreaker(name = "userValidationService",fallbackMethod = "fallbackRegisterUser")
    public Mono<UserResponse> registerUser(RegisterRequest registerRequest) {
        log.info("Calling User RegisterUser API for userId {}",registerRequest.toString());
        return userServiceWebClient.post()
                .uri("/api/users/register")
                .bodyValue(registerRequest)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .onErrorResume(WebClientResponseException.class, e -> {
                    if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                        return Mono.error(new UserNotFoundException("User Not Found " + registerRequest.getKeyCloakId()));
                    } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                        return Mono.error(new UserNotFoundException("Invalid Request " + registerRequest.getKeyCloakId()));
                    }
                    return Mono.error(e); // Let other errors (like 503) bubble up
                });



    }
    // Correct fallback for validateUser
    public Mono<Boolean> fallbackValidateUser(String userId, Throwable throwable) {
        log.error("Fallback triggered - User service unavailable for userId: {}", userId, throwable);
        return Mono.error(new UserServiceUnavailableException("User service is currently unavailable. Please try again later.", throwable));
    }

    // Correct fallback for registerUser
    public Mono<UserResponse> fallbackRegisterUser(RegisterRequest registerRequest, Throwable throwable) {
        log.error("Fallback triggered - Unable to register user: {}", registerRequest.getKeyCloakId(), throwable);
        return Mono.error(new UserServiceUnavailableException("User service is currently unavailable. Please try again later.", throwable));
    }
}
