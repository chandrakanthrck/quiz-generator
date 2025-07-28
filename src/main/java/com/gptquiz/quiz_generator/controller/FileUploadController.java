package com.gptquiz.quiz_generator.controller;

import com.gptquiz.quiz_generator.model.Flashcard;
import com.gptquiz.quiz_generator.repository.FlashcardRepository;
import com.gptquiz.quiz_generator.service.FileProcessingService;
import com.gptquiz.quiz_generator.service.OpenAiService;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @Autowired
    private FileProcessingService fileProcessingService;

    @Autowired
    private OpenAiService openAiService;

    @Autowired
    private FlashcardRepository flashcardRepository;

    // Just extract and return text
    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String extractedText = fileProcessingService.extractTextFromFile(file);
            return ResponseEntity.ok(extractedText);
        } catch (IOException | TikaException e) {
            return ResponseEntity.status(500).body("Error processing file: " + e.getMessage());
        }
    }

    // Generate + Save flashcards from file
    @PostMapping("/flashcards")
    public ResponseEntity<?> uploadAndSaveFlashcards(@RequestParam("file") MultipartFile file) {
        try {
            String text = fileProcessingService.extractTextFromFile(file);
            String fileName = file.getOriginalFilename();
            List<Flashcard> flashcards = openAiService.generateAndSaveFlashcards(text, fileName);
            return ResponseEntity.ok(flashcards);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    // Fetch all saved flashcards
    @GetMapping("/flashcards")
    public ResponseEntity<List<Flashcard>> getAllFlashcards() {
        return ResponseEntity.ok(flashcardRepository.findAll());
    }
}
