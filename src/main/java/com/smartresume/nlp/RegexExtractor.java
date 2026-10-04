package com.smartresume.nlp;

import com.smartresume.model.EducationLevel;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Regex-based information extractor for candidate contact details,
 * years of experience, education levels, and candidate names.
 */
@Component
public class RegexExtractor {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "(?:\\+?\\d{1,3}[-.\\s]?)?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}|(?:\\+?\\d{1,3}[-.\\s]?)?\\d{10}"
    );

    private static final List<Pattern> EXPERIENCE_PATTERNS = Arrays.asList(
            Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*(?:\\+|plus)?\\s*(?:years?|yrs?)(?:\\s+of)?\\s+(?:experience|exp)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(?:experience|exp)(?:\\s*[:\\-])?\\s*(\\d+(?:\\.\\d+)?)\\s*(?:\\+|plus)?\\s*(?:years?|yrs?)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*(?:years?|yrs?)\\s+in\\s+[a-zA-Z]+", Pattern.CASE_INSENSITIVE),
            Pattern.compile("over\\s+(\\d+(?:\\.\\d+)?)\\s*(?:years?|yrs?)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(\\d{4})\\s*(?:-|–|to)\\s*(?:present|current|(\\d{4}))", Pattern.CASE_INSENSITIVE)
    );

    /**
     * Extract primary email address.
     */
    public Optional<String> extractEmail(String text) {
        if (text == null) return Optional.empty();
        Matcher matcher = EMAIL_PATTERN.matcher(text);
        if (matcher.find()) {
            return Optional.of(matcher.group().trim().toLowerCase());
        }
        return Optional.empty();
    }

    /**
     * Extract primary phone number.
     */
    public Optional<String> extractPhone(String text) {
        if (text == null) return Optional.empty();
        Matcher matcher = PHONE_PATTERN.matcher(text);
        if (matcher.find()) {
            String phone = matcher.group().trim();
            // Basic length validation
            String digitsOnly = phone.replaceAll("[^0-9]", "");
            if (digitsOnly.length() >= 7 && digitsOnly.length() <= 15) {
                return Optional.of(phone);
            }
        }
        return Optional.empty();
    }

    /**
     * Extract total estimated years of experience.
     */
    public double extractExperienceYears(String text) {
        if (text == null || text.isBlank()) return 0.0;

        double maxYears = 0.0;

        // Check direct "X years of experience" statements first
        for (int i = 0; i < 4; i++) {
            Pattern pattern = EXPERIENCE_PATTERNS.get(i);
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                try {
                    double yrs = Double.parseDouble(matcher.group(1));
                    if (yrs > 0 && yrs < 45) {
                        maxYears = Math.max(maxYears, yrs);
                    }
                } catch (NumberFormatException ignored) {}
            }
        }

        // If explicit experience wasn't stated, calculate from date ranges (e.g. 2018 - 2024 or 2019 - Present)
        if (maxYears == 0.0) {
            int currentYear = 2025;
            Pattern yearRangePattern = EXPERIENCE_PATTERNS.get(4);
            Matcher matcher = yearRangePattern.matcher(text);
            double totalSpan = 0.0;
            while (matcher.find()) {
                try {
                    int startYear = Integer.parseInt(matcher.group(1));
                    int endYear = (matcher.group(2) != null) ? Integer.parseInt(matcher.group(2)) : currentYear;
                    if (startYear >= 1970 && endYear <= currentYear && endYear >= startYear) {
                        totalSpan += (endYear - startYear);
                    }
                } catch (NumberFormatException ignored) {}
            }
            if (totalSpan > 0) {
                maxYears = Math.min(30.0, totalSpan);
            }
        }

        return Math.round(maxYears * 10.0) / 10.0;
    }

    /**
     * Extract highest education level mentioned in the text.
     */
    public EducationLevel extractEducationLevel(String text) {
        if (text == null || text.isBlank()) return EducationLevel.BACHELORS;
        return EducationLevel.fromString(text);
    }

    /**
     * Heuristic candidate name extraction from top section of resume.
     */
    public String extractCandidateName(String text, String fallbackFileName) {
        if (text == null || text.isBlank()) {
            return cleanFileNameAsName(fallbackFileName);
        }

        String[] lines = text.split("\\r?\\n");
        for (int i = 0; i < Math.min(10, lines.length); i++) {
            String line = lines[i].trim();
            // Skip empty lines or contact headers
            if (line.isEmpty() || line.contains("@") || line.contains("http") || line.contains("www.")
                    || line.toLowerCase().startsWith("resume") || line.toLowerCase().startsWith("curriculum vitae")
                    || line.toLowerCase().startsWith("cv") || line.length() < 3 || line.length() > 50) {
                continue;
            }
            // Check if line consists mostly of alphabetical characters (2 to 4 words)
            if (line.matches("^[A-Z][a-zA-Z.'\\-]+\\s+[A-Z][a-zA-Z.'\\-]+(\\s+[A-Z][a-zA-Z.'\\-]+)?$")) {
                return line;
            }
        }

        // Fallback: use filename
        return cleanFileNameAsName(fallbackFileName);
    }

    private String cleanFileNameAsName(String fileName) {
        if (fileName == null || fileName.isBlank()) return "Unknown Candidate";
        String name = fileName.replaceAll("(?i)\\.(pdf|docx|doc|txt)$", "")
                .replaceAll("[_\\-+]", " ")
                .replaceAll("(?i)resume|cv|profile|document", "")
                .trim();
        if (name.isEmpty()) return "Candidate";
        return capitalizeWords(name);
    }

    private String capitalizeWords(String str) {
        String[] words = str.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (!w.isEmpty()) {
                sb.append(Character.toUpperCase(w.charAt(0)))
                  .append(w.substring(1).toLowerCase())
                  .append(" ");
            }
        }
        return sb.toString().trim();
    }
}
