package com.gptquiz.quiz_generator.service;

import com.gptquiz.quiz_generator.utils.TikaTextExtractor;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FileProcessingService {

    public String extractTextFromFile(MultipartFile file) throws IOException, TikaException {
        return TikaTextExtractor.extractText(file.getInputStream());
    }
}
