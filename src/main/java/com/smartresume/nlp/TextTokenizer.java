package com.smartresume.nlp;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Text tokenizer and normalizer with stopword removal and n-gram extraction.
 */
@Component
public class TextTokenizer {

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are", "aren't",
            "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by",
            "can", "can't", "cannot", "could", "couldn't", "did", "didn't", "do", "does", "doesn't", "doing",
            "don't", "down", "during", "each", "few", "for", "from", "further", "had", "hadn't", "has", "hasn't",
            "have", "haven't", "having", "he", "he'd", "he'll", "he's", "her", "here", "here's", "hers", "herself",
            "him", "himself", "his", "how", "how's", "i", "i'd", "i'll", "i'm", "i've", "if", "in", "into", "is",
            "isn't", "it", "it's", "its", "itself", "let's", "me", "more", "most", "mustn't", "my", "myself",
            "no", "nor", "not", "of", "off", "on", "once", "only", "or", "other", "ought", "our", "ours",
            "ourselves", "out", "over", "own", "same", "shan't", "she", "she'd", "she'll", "she's", "should",
            "shouldn't", "so", "some", "such", "than", "that", "that's", "the", "their", "theirs", "them",
            "themselves", "then", "there", "there's", "these", "they", "they'd", "they'll", "they're", "they've",
            "this", "those", "through", "to", "too", "under", "until", "up", "very", "was", "wasn't", "we", "we'd",
            "we'll", "we're", "we've", "were", "weren't", "what", "what's", "when", "when's", "where", "where's",
            "which", "while", "who", "who's", "whom", "why", "why's", "with", "won't", "would", "wouldn't",
            "you", "you'd", "you'll", "you're", "you've", "your", "yours", "yourself", "yourselves"
    ));

    private static final Pattern WORD_PATTERN = Pattern.compile("[a-zA-Z0-9+#.]+");

    /**
     * Tokenize text into a list of normalized words, removing stopwords and short tokens.
     */
    public List<String> tokenize(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        List<String> tokens = new ArrayList<>();
        var matcher = WORD_PATTERN.matcher(text.toLowerCase());
        while (matcher.find()) {
            String word = matcher.group();
            // clean leading/trailing dots
            word = word.replaceAll("^\\.+|\\.+$", "");
            if (word.length() > 1 && !STOP_WORDS.contains(word)) {
                tokens.add(word);
            }
        }
        return tokens;
    }

    /**
     * Compute term frequency map for a document.
     */
    public Map<String, Integer> getTermFrequencies(String text) {
        List<String> tokens = tokenize(text);
        Map<String, Integer> freqMap = new HashMap<>();
        for (String token : tokens) {
            freqMap.put(token, freqMap.getOrDefault(token, 0) + 1);
        }
        return freqMap;
    }

    /**
     * Clean text for regex or synonym matching (normalized spaces and lowercase).
     */
    public String normalizeForMatching(String text) {
        if (text == null) return "";
        return text.toLowerCase()
                .replaceAll("[\\r\\n\\t]+", " ")
                .replaceAll("[^a-z0-9+#.\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}
