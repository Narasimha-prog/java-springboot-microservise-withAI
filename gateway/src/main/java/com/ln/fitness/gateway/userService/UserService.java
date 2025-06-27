package com.ln.fitness.gateway.userService;


import com.ln.fitness.gateway.exception.UserNotFoundException;
import com.ln.fitness.gateway.exception.UserServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@Retry(name = "userValidationRetry")
    public Mono<UserErrorResponse> validateUser(String userId){
        log.info("Calling User Validation API  for Authentication Using KeyCloakId {}",userId);
            return userServiceWebClient.get()
                    .uri("/api/users/{userId}/validate",userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .map(result -> new UserErrorResponse(true, "User is valid", userId))
                    .onErrorResume(throwable ->
                    {
                        if (throwable instanceof IllegalStateException) {
                            log.error("No instances available for USER-SERVICE", throwable);
                            return Mono.error(new UserServiceUnavailableException("User-Service is currently down or not registered with Eureka.", throwable));
                        }
                        if (throwable instanceof WebClientResponseException ex) {
                            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                                return Mono.error(new UserNotFoundException("User Not Found: " + userId));
                            } else if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                                return Mono.error(new UserNotFoundException("Invalid Request: " + userId));
                            } else if (ex.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
                                return Mono.error(new UserServiceUnavailableException("User service unavailable: " , ex));
                            }
                        }
                        return Mono.error(throwable);

                    });


    }
    @CircuitBreaker(name = "userValidationServiceRegister",fallbackMethod = "fallbackRegisterUser")
    @Retry(name = "userRegisterRetry")
    public Mono<UserResponse> registerUser(RegisterRequest registerRequest) {
        log.info("Calling User RegisterUser API for userId {}",registerRequest.toString());
        return userServiceWebClient.post()
                .uri("/api/users/register")
                .bodyValue(registerRequest)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .onErrorResume( throwable -> {
                    if (throwable instanceof IllegalStateException) {
                        log.error("No instances available for USER-SERVICE", throwable);
                        return Mono.error(new UserServiceUnavailableException("User-Service is currently down or not registered with Eureka.", throwable));
                    }
                    if (throwable instanceof WebClientResponseException ex) {
                        if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                            return Mono.error(new UserNotFoundException("User Not Found: " + registerRequest.getKeyCloakId()));
                        } else if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                            return Mono.error(new UserNotFoundException("Invalid Request: " + registerRequest.getKeyCloakId()));
                        } else if (ex.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
                            return Mono.error(new UserServiceUnavailableException("User service unavailable: " , ex));
                        }
                    }
                    return Mono.error(throwable); // Let other errors (like 503) bubble up
                });



    }
    // Correct fallback for validateUser
    public Mono<UserErrorResponse> fallbackValidateUser(String userId, Throwable throwable) {
        log.error("Fallback triggered - User service unavailable for userId: {}", userId, throwable);

     UserErrorResponse response=new UserErrorResponse();
        response.setSuccess(false);
        response.setUserId(userId);
        response.setMessage("User service is currently unavailable. Please try again later.");

        return Mono.just(response);
    }


    // Correct fallback for registerUser
    public Mono<UserErrorResponse> fallbackRegisterUser(RegisterRequest registerRequest, Throwable throwable) {
        log.error("Fallback triggered - Unable to register user: {}", registerRequest.getKeyCloakId(), throwable);
        UserErrorResponse response=new UserErrorResponse();
        response.setSuccess(false);
        response.setUserId(registerRequest.getKeyCloakId());
        response.setMessage("User service is currently unavailable. Please try again later.");

        return Mono.just(response);
    }
}
