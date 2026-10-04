package com.smartresume.dto;

import java.util.Set;

public class ScoreBreakdownResponse {
    private double requiredSkillsScore;
    private double preferredSkillsScore;
    private double semanticSimilarityScore;
    private double experienceScore;
    private double educationScore;
    private double totalScore;

    // Weights for UI visualization
    private double requiredSkillsWeight;
    private double preferredSkillsWeight;
    private double semanticSimilarityWeight;
    private double experienceFitWeight;
    private double educationFitWeight;

    private Set<String> matchedRequiredSkills;
    private Set<String> missingRequiredSkills;
    private Set<String> matchedPreferredSkills;
    private Set<String> missingPreferredSkills;

    public ScoreBreakdownResponse() {
    }

    public double getRequiredSkillsScore() {
        return requiredSkillsScore;
    }

    public void setRequiredSkillsScore(double requiredSkillsScore) {
        this.requiredSkillsScore = requiredSkillsScore;
    }

    public double getPreferredSkillsScore() {
        return preferredSkillsScore;
    }

    public void setPreferredSkillsScore(double preferredSkillsScore) {
        this.preferredSkillsScore = preferredSkillsScore;
    }

    public double getSemanticSimilarityScore() {
        return semanticSimilarityScore;
    }

    public void setSemanticSimilarityScore(double semanticSimilarityScore) {
        this.semanticSimilarityScore = semanticSimilarityScore;
    }

    public double getExperienceScore() {
        return experienceScore;
    }

    public void setExperienceScore(double experienceScore) {
        this.experienceScore = experienceScore;
    }

    public double getEducationScore() {
        return educationScore;
    }

    public void setEducationScore(double educationScore) {
        this.educationScore = educationScore;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public double getRequiredSkillsWeight() {
        return requiredSkillsWeight;
    }

    public void setRequiredSkillsWeight(double requiredSkillsWeight) {
        this.requiredSkillsWeight = requiredSkillsWeight;
    }

    public double getPreferredSkillsWeight() {
        return preferredSkillsWeight;
    }

    public void setPreferredSkillsWeight(double preferredSkillsWeight) {
        this.preferredSkillsWeight = preferredSkillsWeight;
    }

    public double getSemanticSimilarityWeight() {
        return semanticSimilarityWeight;
    }

    public void setSemanticSimilarityWeight(double semanticSimilarityWeight) {
        this.semanticSimilarityWeight = semanticSimilarityWeight;
    }

    public double getExperienceFitWeight() {
        return experienceFitWeight;
    }

    public void setExperienceFitWeight(double experienceFitWeight) {
        this.experienceFitWeight = experienceFitWeight;
    }

    public double getEducationFitWeight() {
        return educationFitWeight;
    }

    public void setEducationFitWeight(double educationFitWeight) {
        this.educationFitWeight = educationFitWeight;
    }

    public Set<String> getMatchedRequiredSkills() {
        return matchedRequiredSkills;
    }

    public void setMatchedRequiredSkills(Set<String> matchedRequiredSkills) {
        this.matchedRequiredSkills = matchedRequiredSkills;
    }

    public Set<String> getMissingRequiredSkills() {
        return missingRequiredSkills;
    }

    public void setMissingRequiredSkills(Set<String> missingRequiredSkills) {
        this.missingRequiredSkills = missingRequiredSkills;
    }

    public Set<String> getMatchedPreferredSkills() {
        return matchedPreferredSkills;
    }

    public void setMatchedPreferredSkills(Set<String> matchedPreferredSkills) {
        this.matchedPreferredSkills = matchedPreferredSkills;
    }

    public Set<String> getMissingPreferredSkills() {
        return missingPreferredSkills;
    }

    public void setMissingPreferredSkills(Set<String> missingPreferredSkills) {
        this.missingPreferredSkills = missingPreferredSkills;
    }
}
