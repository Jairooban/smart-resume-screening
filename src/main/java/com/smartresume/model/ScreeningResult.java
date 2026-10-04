package com.smartresume.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Screening Result entity storing calculated match scores, breakdown,
 * matched/missing skills, explanation, and shortlist status.
 */
@Entity
@Table(name = "screening_results", uniqueConstraints = {
    @UniqueConstraint(name = "uk_job_resume", columnNames = {"job_opening_id", "resume_id"})
})
public class ScreeningResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_opening_id", nullable = false)
    private JobOpening jobOpening;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Column(name = "total_score", nullable = false)
    private Double totalScore = 0.0;

    @Column(name = "required_skills_score", nullable = false)
    private Double requiredSkillsScore = 0.0;

    @Column(name = "preferred_skills_score", nullable = false)
    private Double preferredSkillsScore = 0.0;

    @Column(name = "semantic_similarity_score", nullable = false)
    private Double semanticSimilarityScore = 0.0;

    @Column(name = "experience_score", nullable = false)
    private Double experienceScore = 0.0;

    @Column(name = "education_score", nullable = false)
    private Double educationScore = 0.0;

    @Lob
    @Column(name = "matched_required_skills", columnDefinition = "CLOB")
    private String matchedRequiredSkillsText;

    @Lob
    @Column(name = "missing_required_skills", columnDefinition = "CLOB")
    private String missingRequiredSkillsText;

    @Lob
    @Column(name = "matched_preferred_skills", columnDefinition = "CLOB")
    private String matchedPreferredSkillsText;

    @Lob
    @Column(name = "missing_preferred_skills", columnDefinition = "CLOB")
    private String missingPreferredSkillsText;

    @Lob
    @Column(name = "score_explanation", columnDefinition = "CLOB")
    private String scoreExplanation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CandidateStatus status = CandidateStatus.REVIEW;

    @Column(name = "manual_override")
    private boolean manualOverride = false;

    @Column(length = 1000)
    private String notes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public ScreeningResult() {
    }

    public Set<String> getMatchedRequiredSkills() {
        return parseSkills(matchedRequiredSkillsText);
    }

    public void setMatchedRequiredSkills(Set<String> skills) {
        this.matchedRequiredSkillsText = serializeSkills(skills);
    }

    public Set<String> getMissingRequiredSkills() {
        return parseSkills(missingRequiredSkillsText);
    }

    public void setMissingRequiredSkills(Set<String> skills) {
        this.missingRequiredSkillsText = serializeSkills(skills);
    }

    public Set<String> getMatchedPreferredSkills() {
        return parseSkills(matchedPreferredSkillsText);
    }

    public void setMatchedPreferredSkills(Set<String> skills) {
        this.matchedPreferredSkillsText = serializeSkills(skills);
    }

    public Set<String> getMissingPreferredSkills() {
        return parseSkills(missingPreferredSkillsText);
    }

    public void setMissingPreferredSkills(Set<String> skills) {
        this.missingPreferredSkillsText = serializeSkills(skills);
    }

    private Set<String> parseSkills(String text) {
        if (text == null || text.isBlank()) {
            return new HashSet<>();
        }
        return Arrays.stream(text.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }

    private String serializeSkills(Set<String> skills) {
        if (skills == null || skills.isEmpty()) {
            return "";
        }
        return String.join(",", skills);
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JobOpening getJobOpening() {
        return jobOpening;
    }

    public void setJobOpening(JobOpening jobOpening) {
        this.jobOpening = jobOpening;
    }

    public Resume getResume() {
        return resume;
    }

    public void setResume(Resume resume) {
        this.resume = resume;
    }

    public Double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Double totalScore) {
        this.totalScore = totalScore;
    }

    public Double getRequiredSkillsScore() {
        return requiredSkillsScore;
    }

    public void setRequiredSkillsScore(Double requiredSkillsScore) {
        this.requiredSkillsScore = requiredSkillsScore;
    }

    public Double getPreferredSkillsScore() {
        return preferredSkillsScore;
    }

    public void setPreferredSkillsScore(Double preferredSkillsScore) {
        this.preferredSkillsScore = preferredSkillsScore;
    }

    public Double getSemanticSimilarityScore() {
        return semanticSimilarityScore;
    }

    public void setSemanticSimilarityScore(Double semanticSimilarityScore) {
        this.semanticSimilarityScore = semanticSimilarityScore;
    }

    public Double getExperienceScore() {
        return experienceScore;
    }

    public void setExperienceScore(Double experienceScore) {
        this.experienceScore = experienceScore;
    }

    public Double getEducationScore() {
        return educationScore;
    }

    public void setEducationScore(Double educationScore) {
        this.educationScore = educationScore;
    }

    public String getMatchedRequiredSkillsText() {
        return matchedRequiredSkillsText;
    }

    public void setMatchedRequiredSkillsText(String matchedRequiredSkillsText) {
        this.matchedRequiredSkillsText = matchedRequiredSkillsText;
    }

    public String getMissingRequiredSkillsText() {
        return missingRequiredSkillsText;
    }

    public void setMissingRequiredSkillsText(String missingRequiredSkillsText) {
        this.missingRequiredSkillsText = missingRequiredSkillsText;
    }

    public String getMatchedPreferredSkillsText() {
        return matchedPreferredSkillsText;
    }

    public void setMatchedPreferredSkillsText(String matchedPreferredSkillsText) {
        this.matchedPreferredSkillsText = matchedPreferredSkillsText;
    }

    public String getMissingPreferredSkillsText() {
        return missingPreferredSkillsText;
    }

    public void setMissingPreferredSkillsText(String missingPreferredSkillsText) {
        this.missingPreferredSkillsText = missingPreferredSkillsText;
    }

    public String getScoreExplanation() {
        return scoreExplanation;
    }

    public void setScoreExplanation(String scoreExplanation) {
        this.scoreExplanation = scoreExplanation;
    }

    public CandidateStatus getStatus() {
        return status;
    }

    public void setStatus(CandidateStatus status) {
        this.status = status;
    }

    public boolean isManualOverride() {
        return manualOverride;
    }

    public void setManualOverride(boolean manualOverride) {
        this.manualOverride = manualOverride;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
