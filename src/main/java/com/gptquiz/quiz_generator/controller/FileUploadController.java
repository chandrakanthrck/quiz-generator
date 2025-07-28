package com.gptquiz.quiz_generator.controller;

import com.gptquiz.quiz_generator.service.FileProcessingService;
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
}
