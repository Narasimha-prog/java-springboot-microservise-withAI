package com.ln.fitness.gateway.Controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
public class FallbackController {

    @RequestMapping("/fallback/user-service")
    public Mono<ResponseEntity<Map<String, Object>>> userServiceFallback() {
        Map<String, Object> body = new HashMap<>();
        body.put("message", "User Service is unavailable. Please try again later.");
        body.put("status", 503);
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body));
    }

    @RequestMapping("/fallback/activity-service")
    public Mono<ResponseEntity<Map<String, Object>>> activityServiceFallback() {
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Activity Service is unavailable. Please try again later.");
        body.put("status", 503);
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body));
    }

    @RequestMapping("/fallback/ai-service")
    public Mono<ResponseEntity<Map<String, Object>>> aiServiceFallback() {
        Map<String, Object> body = new HashMap<>();
        body.put("message", "AI Service is unavailable. Please try again later.");
        body.put("status", 503);
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body));
    }
}
