package com.smartresume.nlp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TfIdfCalculatorTest {

    private TfIdfCalculator tfIdfCalculator;

    @BeforeEach
    void setUp() {
        TextTokenizer tokenizer = new TextTokenizer();
        tfIdfCalculator = new TfIdfCalculator(tokenizer);
    }

    @Test
    void testCosineSimilarityIdenticalText() {
        String text = "Senior Java Developer with Spring Boot and PostgreSQL experience";
        double similarity = tfIdfCalculator.calculateCosineSimilarity(text, text);
        assertEquals(100.0, similarity, 0.5, "Identical texts should have ~100% similarity");
    }

    @Test
    void testCosineSimilarityDifferentText() {
        String text1 = "Senior Java Spring Boot Engineer microservices backend PostgreSQL";
        String text2 = "Experienced Java backend developer architecting Spring Boot microservices";
        double similarity = tfIdfCalculator.calculateCosineSimilarity(text1, text2);
        assertTrue(similarity > 40.0, "Related texts should have high cosine similarity");
    }

    @Test
    void testCosineSimilarityUnrelatedText() {
        String text1 = "Senior Java Developer Spring Boot backend";
        String text2 = "Executive pastry chef baking artisan sourdough bread French cuisine";
        double similarity = tfIdfCalculator.calculateCosineSimilarity(text1, text2);
        assertTrue(similarity < 15.0, "Unrelated texts should have very low cosine similarity");
    }
}
