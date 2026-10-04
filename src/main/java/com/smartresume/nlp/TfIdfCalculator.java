package com.smartresume.nlp;

import org.springframework.stereotype.Component;

import java.util.*;

/**
 * TF-IDF Vectorizer and Cosine Similarity calculator implemented in pure Java.
 * Computes semantic relevance between job description and resume content.
 */
@Component
public class TfIdfCalculator {

    private final TextTokenizer tokenizer;

    public TfIdfCalculator(TextTokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    /**
     * Calculates cosine similarity percentage (0.0 to 100.0) between two text documents.
     */
    public double calculateCosineSimilarity(String text1, String text2) {
        if (text1 == null || text1.isBlank() || text2 == null || text2.isBlank()) {
            return 0.0;
        }

        List<String> tokens1 = tokenizer.tokenize(text1);
        List<String> tokens2 = tokenizer.tokenize(text2);

        if (tokens1.isEmpty() || tokens2.isEmpty()) {
            return 0.0;
        }

        Map<String, Integer> freq1 = tokenizer.getTermFrequencies(text1);
        Map<String, Integer> freq2 = tokenizer.getTermFrequencies(text2);

        // Vocabulary of all unique terms across both documents
        Set<String> vocabulary = new HashSet<>();
        vocabulary.addAll(freq1.keySet());
        vocabulary.addAll(freq2.keySet());

        int totalDocs = 2;
        Map<String, Double> idfMap = new HashMap<>();

        for (String term : vocabulary) {
            int docCount = 0;
            if (freq1.containsKey(term)) docCount++;
            if (freq2.containsKey(term)) docCount++;

            // Smooth IDF calculation: log((N + 1) / (df + 1)) + 1
            double idf = Math.log((double) (totalDocs + 1) / (docCount + 1)) + 1.0;
            idfMap.put(term, idf);
        }

        // Build TF-IDF vectors
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (String term : vocabulary) {
            double tf1 = (double) freq1.getOrDefault(term, 0) / tokens1.size();
            double tf2 = (double) freq2.getOrDefault(term, 0) / tokens2.size();

            double tfidf1 = tf1 * idfMap.get(term);
            double tfidf2 = tf2 * idfMap.get(term);

            dotProduct += tfidf1 * tfidf2;
            normA += tfidf1 * tfidf1;
            normB += tfidf2 * tfidf2;
        }

        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }

        double cosineSimilarity = dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
        // Clamp and convert to 0-100 scale
        double score = Math.max(0.0, Math.min(1.0, cosineSimilarity)) * 100.0;
        return Math.round(score * 100.0) / 100.0;
    }
}
