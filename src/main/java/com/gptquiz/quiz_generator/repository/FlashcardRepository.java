package com.gptquiz.quiz_generator.repository;

import com.gptquiz.quiz_generator.model.Flashcard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlashcardRepository extends JpaRepository<Flashcard, Long> {
}
