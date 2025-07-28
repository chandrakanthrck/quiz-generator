package com.gptquiz.quiz_generator.utils;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

import java.io.InputStream;

public class TikaTextExtractor {

    private static final Tika tika = new Tika();

    public static String extractText(InputStream inputStream) throws TikaException {
        try {
            return tika.parseToString(inputStream);
        } catch (Exception e) {
            throw new TikaException("Error extracting text from file: " + e.getMessage(), e);
        }
    }
}
