package com.gptquiz.quiz_generator.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class OpenAiService {

    @Value("${openai.api.key}")
    private String openAiApiKey;

    private final WebClient webClient;

    public OpenAiService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1/chat/completions")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + System.getenv("OPENAI_API_KEY"))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public String generateFlashcards(String inputText) {
        String prompt = buildPrompt(inputText);

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", new Object[]{
                        Map.of("role", "system", "content", "You are an expert flashcard generator for educational content."),
                        Map.of("role", "user", "content", prompt)
                },
                "temperature", 0.7
        );

        return webClient.post()
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    var choices = (java.util.List<Map<String, Object>>) response.get("choices");
                    if (!choices.isEmpty()) {
                        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                        return (String) message.get("content");
                    }
                    return "No content generated.";
                })
                .onErrorReturn("Error calling OpenAI.")
                .block();
    }

    private String buildPrompt(String text) {
        return """
                Generate flashcards (Q&A format) from the following educational content:
                
                %s
                
                Respond ONLY with flashcards in this format:
                Q: What is X?
                A: It is Y.
                
                Do not include headings, explanations, or introductions.
                """.formatted(text);
    }
}
