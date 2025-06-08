package com.ln.fitness.ai_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class GeminiService {


    private final WebClient webClient;
    @Value("${gemini.api.url}")
    private String geminiUrl;
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public GeminiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String getAnswer(String question){
        Map<String,Object> requestBody=Map.of(
                "contents",new Object[]{
                        Map.of("parts",new Object[]{
                                Map.of("text",question)
                        })
                }
        );
       return webClient.post()
               .uri(geminiUrl+geminiApiKey)
               .contentType(MediaType.APPLICATION_JSON)
               .bodyValue(requestBody)
               .retrieve()
               .bodyToMono(String.class)
               .block();
    }
}