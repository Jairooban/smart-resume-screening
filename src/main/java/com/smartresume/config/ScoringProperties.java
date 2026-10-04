package com.smartresume.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configurable weights and thresholds for resume screening engine.
 */
@ConfigurationProperties(prefix = "screening")
public class ScoringProperties {

    private Weights weights = new Weights();
    private Thresholds thresholds = new Thresholds();

    public static class Weights {
        private double requiredSkills = 40.0;
        private double preferredSkills = 15.0;
        private double semanticSimilarity = 25.0;
        private double experienceFit = 12.0;
        private double educationFit = 8.0;

        public double getRequiredSkills() {
            return requiredSkills;
        }

        public void setRequiredSkills(double requiredSkills) {
            this.requiredSkills = requiredSkills;
        }

        public double getPreferredSkills() {
            return preferredSkills;
        }

        public void setPreferredSkills(double preferredSkills) {
            this.preferredSkills = preferredSkills;
        }

        public double getSemanticSimilarity() {
            return semanticSimilarity;
        }

        public void setSemanticSimilarity(double semanticSimilarity) {
            this.semanticSimilarity = semanticSimilarity;
        }

        public double getExperienceFit() {
            return experienceFit;
        }

        public void setExperienceFit(double experienceFit) {
            this.experienceFit = experienceFit;
        }

        public double getEducationFit() {
            return educationFit;
        }

        public void setEducationFit(double educationFit) {
            this.educationFit = educationFit;
        }
    }

    public static class Thresholds {
        private double shortlistMinScore = 70.0;
        private double reviewMinScore = 45.0;

        public double getShortlistMinScore() {
            return shortlistMinScore;
        }

        public void setShortlistMinScore(double shortlistMinScore) {
            this.shortlistMinScore = shortlistMinScore;
        }

        public double getReviewMinScore() {
            return reviewMinScore;
        }

        public void setReviewMinScore(double reviewMinScore) {
            this.reviewMinScore = reviewMinScore;
        }
    }

    public Weights getWeights() {
        return weights;
    }

    public void setWeights(Weights weights) {
        this.weights = weights;
    }

    public Thresholds getThresholds() {
        return thresholds;
    }

    public void setThresholds(Thresholds thresholds) {
        this.thresholds = thresholds;
    }
}
