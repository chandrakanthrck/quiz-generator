package com.gptquiz.quiz_generator.controller;

import com.gptquiz.quiz_generator.service.FileProcessingService;
import com.gptquiz.quiz_generator.service.OpenAiService;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @Autowired
    private FileProcessingService fileProcessingService;

    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String extractedText = fileProcessingService.extractTextFromFile(file);
            return ResponseEntity.ok(extractedText);
        } catch (IOException | TikaException e) {
            return ResponseEntity.status(500).body("Error processing file: " + e.getMessage());
        }
    }

    @Autowired
    private OpenAiService openAiService;

    @PostMapping("/flashcards")
    public ResponseEntity<?> uploadAndGenerateFlashcards(@RequestParam("file") MultipartFile file) {
        try {
            String text = fileProcessingService.extractTextFromFile(file);
            String flashcards = openAiService.generateFlashcards(text);
            return ResponseEntity.ok(flashcards);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

}
