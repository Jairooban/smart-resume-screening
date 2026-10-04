package com.smartresume.dto;

import com.smartresume.model.CandidateStatus;
import com.smartresume.model.EducationLevel;
import java.time.LocalDateTime;
import java.util.Set;

public class ScreeningResultResponse {
    private Long id;
    private Long jobOpeningId;
    private String jobTitle;
    private Long resumeId;
    private String candidateName;
    private String email;
    private String phone;
    private String fileName;
    private Double experienceYears;
    private EducationLevel educationLevel;
    private Set<String> parsedSkills;

    private Double totalScore;
    private ScoreBreakdownResponse scoreBreakdown;
    private String scoreExplanation;
    private CandidateStatus status;
    private boolean manualOverride;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ScreeningResultResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJobOpeningId() {
        return jobOpeningId;
    }

    public void setJobOpeningId(Long jobOpeningId) {
        this.jobOpeningId = jobOpeningId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Long getResumeId() {
        return resumeId;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Double getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(Double experienceYears) {
        this.experienceYears = experienceYears;
    }

    public EducationLevel getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(EducationLevel educationLevel) {
        this.educationLevel = educationLevel;
    }

    public Set<String> getParsedSkills() {
        return parsedSkills;
    }

    public void setParsedSkills(Set<String> parsedSkills) {
        this.parsedSkills = parsedSkills;
    }

    public Double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Double totalScore) {
        this.totalScore = totalScore;
    }

    public ScoreBreakdownResponse getScoreBreakdown() {
        return scoreBreakdown;
    }

    public void setScoreBreakdown(ScoreBreakdownResponse scoreBreakdown) {
        this.scoreBreakdown = scoreBreakdown;
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
