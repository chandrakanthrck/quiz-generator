package com.gptquiz.quiz_generator.service;

import com.gptquiz.quiz_generator.model.Flashcard;
import com.gptquiz.quiz_generator.repository.FlashcardRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OpenAiService {

    private final WebClient webClient;
    private final FlashcardRepository flashcardRepository;

    public OpenAiService(
            @Value("${openai.api.key}") String openAiApiKey,
            FlashcardRepository flashcardRepository
    ) {
        this.flashcardRepository = flashcardRepository;
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1/chat/completions")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + openAiApiKey)
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
                    var choices = (List<Map<String, Object>>) response.get("choices");
                    if (!choices.isEmpty()) {
                        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                        return (String) message.get("content");
                    }
                    return "No content generated.";
                })
                .onErrorReturn("Error calling OpenAI.")
                .block();
    }

    public List<Flashcard> generateAndSaveFlashcards(String rawText, String sourceFileName) {
        String result = generateFlashcards(rawText);
        List<Flashcard> flashcards = parseFlashcards(result, sourceFileName);
        return flashcardRepository.saveAll(flashcards);
    }

    private List<Flashcard> parseFlashcards(String raw, String sourceFileName) {
        List<Flashcard> flashcards = new ArrayList<>();
        String[] lines = raw.split("\n");

        String question = null;
        for (String line : lines) {
            if (line.startsWith("Q:")) {
                question = line.replace("Q:", "").trim();
            } else if (line.startsWith("A:") && question != null) {
                String answer = line.replace("A:", "").trim();
                flashcards.add(Flashcard.builder()
                        .question(question)
                        .answer(answer)
                        .sourceFileName(sourceFileName)
                        .build());
                question = null;
            }
        }
        return flashcards;
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
