package com.smartresume.model;

/**
 * Standardized education levels and weights for screening.
 */
public enum EducationLevel {
    HIGH_SCHOOL("High School", 1),
    ASSOCIATE("Associate Degree / Diploma", 2),
    BACHELORS("Bachelor's Degree", 3),
    MASTERS("Master's Degree", 4),
    PHD("PhD / Doctorate", 5);

    private final String displayName;
    private final int rank;

    EducationLevel(String displayName, int rank) {
        this.displayName = displayName;
        this.rank = rank;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getRank() {
        return rank;
    }

    /**
     * Parse text string into closest EducationLevel.
     */
    public static EducationLevel fromString(String text) {
        if (text == null || text.isBlank()) {
            return BACHELORS; // sensible default
        }
        String lower = text.toLowerCase();
        if (lower.contains("phd") || lower.contains("ph.d") || lower.contains("doctor") || lower.contains("doctorate")) {
            return PHD;
        }
        if (lower.contains("master") || lower.contains("m.s") || lower.contains("m.tech") || lower.contains("mca") || lower.contains("m.sc") || lower.contains("mba")) {
            return MASTERS;
        }
        if (lower.contains("bachelor") || lower.contains("b.s") || lower.contains("b.tech") || lower.contains("bca") || lower.contains("b.sc") || lower.contains("b.e") || lower.contains("degree") || lower.contains("undergraduate")) {
            return BACHELORS;
        }
        if (lower.contains("associate") || lower.contains("diploma")) {
            return ASSOCIATE;
        }
        if (lower.contains("high school") || lower.contains("secondary") || lower.contains("12th") || lower.contains("ged")) {
            return HIGH_SCHOOL;
        }
        return BACHELORS;
    }
}
