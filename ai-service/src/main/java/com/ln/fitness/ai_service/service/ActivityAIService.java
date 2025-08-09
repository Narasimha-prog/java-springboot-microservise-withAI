package com.ln.fitness.ai_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ln.fitness.ai_service.model.Activity;
import com.ln.fitness.ai_service.model.Recommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAIService {

    private final GeminiService geminiService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Recommendation generateRecommendation(Activity activity) throws JsonProcessingException {
        String prompt = createPromptForActivity(activity);
        String aiResponse = geminiService.getAnswer(prompt);
        log.info("Response From AI: {}", aiResponse);

        return processAiResponse(activity, aiResponse);
    }

    private Recommendation processAiResponse(Activity activity, String aiResponse) throws JsonProcessingException {
        JsonNode rootNode = objectMapper.readTree(aiResponse);

        // Defensive parsing of nested AI response
        JsonNode candidatesNode = rootNode.path("candidates");
        if (!candidatesNode.isArray() || candidatesNode.size() == 0) {
            log.error("No candidates found in AI response");
            throw new JsonProcessingException("Invalid AI response format - no candidates") {};
        }

        JsonNode partsNode = candidatesNode.get(0).path("content").path("parts");
        if (!partsNode.isArray() || partsNode.size() == 0) {
            log.error("No content parts found in AI response");
            throw new JsonProcessingException("Invalid AI response format - no content parts") {};
        }

        String rawText = partsNode.get(0).path("text").asText("");
        if (rawText.isEmpty()) {
            log.error("No text found in AI response parts");
            throw new JsonProcessingException("Empty AI response text") {};
        }

        // Clean code block markdown from AI response text
        String jsonContent = rawText.replaceAll("```json\\n", "")
                .replaceAll("\\n```", "")
                .trim();

        log.info("Parsed JSON content: {}", jsonContent);

        // Parse the actual recommendation JSON from the cleaned content
        JsonNode recommendationJson = objectMapper.readTree(jsonContent);

        // Extract and combine analysis sections into a single string (optional)
        JsonNode analysisNode = recommendationJson.path("analysis");
        StringBuilder fullAnalysis = new StringBuilder();
        addAnalysisSection(fullAnalysis, analysisNode, "overall", "Overall:");
        addAnalysisSection(fullAnalysis, analysisNode, "pace", "Pace:");
        addAnalysisSection(fullAnalysis, analysisNode, "heartRate", "HeartRate:");
        addAnalysisSection(fullAnalysis, analysisNode, "caloriesBurned", "CaloriesBurned:");

        // Extract improvements, suggestions, safety as lists of strings
        List<String> improvements = extractImprovements(recommendationJson.path("improvements"));
        List<String> suggestions = extractSuggestions(recommendationJson.path("suggestions"));
        List<String> safety = extractSafetyGuidelines(recommendationJson.path("safety"));

        // Optionally, you can store analysis JSON string separately for front-end parsing if needed
        String analysisJsonString = analysisNode.isMissingNode() ? "" : analysisNode.toString();

        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .activityType(activity.getType())
                .recommendation(fullAnalysis.toString().trim())  // human readable combined analysis
                .improvements(improvements)
                .suggestions(suggestions)
                .safety(safety)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private void addAnalysisSection(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix) {
        if (!analysisNode.path(key).isMissingNode()) {
            fullAnalysis.append(prefix)
                    .append(" ")
                    .append(analysisNode.path(key).asText())
                    .append("\n\n");
        }
    }

    private List<String> extractSafetyGuidelines(JsonNode safetyNode) {
        List<String> safety = new ArrayList<>();
        if (safetyNode.isArray()) {
            safetyNode.forEach(item -> safety.add(item.asText()));
        }
        return safety.isEmpty()
                ? Collections.singletonList("No Specific Safety Guidelines are provided")
                : safety;
    }

    private List<String> extractSuggestions(JsonNode suggestionsNode) {
        List<String> suggestions = new ArrayList<>();
        if (suggestionsNode.isArray()) {
            suggestionsNode.forEach(suggestion -> {
                String workout = suggestion.path("workout").asText(null);
                String description = suggestion.path("description").asText(null);
                if (workout != null && description != null) {
                    suggestions.add(String.format("%s: %s", workout, description));
                } else {
                    // Fallback: just add raw JSON string if missing fields
                    suggestions.add(suggestion.toString());
                }
            });
        }
        return suggestions.isEmpty()
                ? Collections.singletonList("No Specific suggestions are provided")
                : suggestions;
    }

    private List<String> extractImprovements(JsonNode improvementsNode) {
        List<String> improvements = new ArrayList<>();
        if (improvementsNode.isArray()) {
            improvementsNode.forEach(improvement -> {
                String area = improvement.path("area").asText(null);
                String recommendation = improvement.path("recommendation").asText(null);
                if (area != null && recommendation != null) {
                    improvements.add(String.format("%s: %s", area, recommendation));
                } else {
                    // Fallback: raw JSON string
                    improvements.add(improvement.toString());
                }
            });
        }
        return improvements.isEmpty()
                ? Collections.singletonList("No Specific improvements are provided")
                : improvements;
    }

    private String createPromptForActivity(Activity activity) {
        String additionalMetricsJson;
        try {
            additionalMetricsJson = objectMapper.writeValueAsString(activity.getAdditionalMetrics());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize additionalMetrics, using empty JSON object", e);
            additionalMetricsJson = "{}";
        }

        return String.format("""
                Analyze this fitness activity and provide detailed recommendations in the following EXACT JSON format:
                {
                  "analysis": {
                    "overall": "Overall analysis here",
                    "pace": "Pace analysis here",
                    "heartRate": "Heart rate analysis here",
                    "caloriesBurned": "Calories analysis here"
                  },
                  "improvements": [
                    {
                      "area": "Area name",
                      "recommendation": "Detailed recommendation"
                    }
                  ],
                  "suggestions": [
                    {
                      "workout": "Workout name",
                      "description": "Detailed workout description"
                    }
                  ],
                  "safety": [
                    "Safety point 1",
                    "Safety point 2"
                  ]
                }

                Analyze this activity:
                Activity Type: %s
                Duration: %d minutes
                Calories Burned: %d
                Additional Metrics: %s

                Provide detailed analysis focusing on performance, improvements, next workout suggestions, and safety guidelines.
                Ensure the response follows the EXACT JSON format shown above.
                """,
                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                additionalMetricsJson);
    }
}
