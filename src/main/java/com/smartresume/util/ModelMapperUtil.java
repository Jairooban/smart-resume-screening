package com.smartresume.util;

import com.smartresume.config.ScoringProperties;
import com.smartresume.dto.*;
import com.smartresume.model.*;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper utility converting domain entities to DTO responses.
 */
@Component
public class ModelMapperUtil {

    private final ScoringProperties scoringProperties;

    public ModelMapperUtil(ScoringProperties scoringProperties) {
        this.scoringProperties = scoringProperties;
    }

    public JobResponse toJobResponse(JobOpening job, long applicantCount, Double avgScore) {
        if (job == null) return null;
        JobResponse dto = new JobResponse();
        dto.setId(job.getId());
        dto.setTitle(job.getTitle());
        dto.setDepartment(job.getDepartment());
        dto.setLocation(job.getLocation());
        dto.setDescription(job.getDescription());
        dto.setRequiredSkills(job.getRequiredSkills());
        dto.setPreferredSkills(job.getPreferredSkills());
        dto.setMinExperienceYears(job.getMinExperienceYears());
        dto.setEducationRequirement(job.getEducationRequirement());
        dto.setActive(job.isActive());
        dto.setTotalApplicants(applicantCount);
        dto.setAverageScore(avgScore != null ? Math.round(avgScore * 10.0) / 10.0 : null);
        dto.setCreatedAt(job.getCreatedAt());
        dto.setUpdatedAt(job.getUpdatedAt());
        return dto;
    }

    public ResumeResponse toResumeResponse(Resume resume) {
        if (resume == null) return null;
        ResumeResponse dto = new ResumeResponse();
        dto.setId(resume.getId());
        dto.setCandidateName(resume.getCandidateName());
        dto.setEmail(resume.getEmail());
        dto.setPhone(resume.getPhone());
        dto.setFileName(resume.getFileName());
        dto.setFileType(resume.getFileType());
        dto.setFileSize(resume.getFileSize());
        dto.setParsedSkills(resume.getParsedSkills());
        dto.setExperienceYears(resume.getExperienceYears());
        dto.setEducationLevel(resume.getEducationLevel());
        dto.setCreatedAt(resume.getCreatedAt());
        return dto;
    }

    public ScreeningResultResponse toScreeningResultResponse(ScreeningResult result) {
        if (result == null) return null;
        ScreeningResultResponse dto = new ScreeningResultResponse();
        dto.setId(result.getId());
        dto.setJobOpeningId(result.getJobOpening().getId());
        dto.setJobTitle(result.getJobOpening().getTitle());
        dto.setResumeId(result.getResume().getId());
        dto.setCandidateName(result.getResume().getCandidateName());
        dto.setEmail(result.getResume().getEmail());
        dto.setPhone(result.getResume().getPhone());
        dto.setFileName(result.getResume().getFileName());
        dto.setExperienceYears(result.getResume().getExperienceYears());
        dto.setEducationLevel(result.getResume().getEducationLevel());
        dto.setParsedSkills(result.getResume().getParsedSkills());
        dto.setTotalScore(result.getTotalScore());
        dto.setScoreExplanation(result.getScoreExplanation());
        dto.setStatus(result.getStatus());
        dto.setManualOverride(result.isManualOverride());
        dto.setNotes(result.getNotes());
        dto.setCreatedAt(result.getCreatedAt());
        dto.setUpdatedAt(result.getUpdatedAt());

        ScoreBreakdownResponse breakdown = new ScoreBreakdownResponse();
        breakdown.setTotalScore(result.getTotalScore());
        breakdown.setRequiredSkillsScore(result.getRequiredSkillsScore());
        breakdown.setPreferredSkillsScore(result.getPreferredSkillsScore());
        breakdown.setSemanticSimilarityScore(result.getSemanticSimilarityScore());
        breakdown.setExperienceScore(result.getExperienceScore());
        breakdown.setEducationScore(result.getEducationScore());

        ScoringProperties.Weights w = scoringProperties.getWeights();
        breakdown.setRequiredSkillsWeight(w.getRequiredSkills());
        breakdown.setPreferredSkillsWeight(w.getPreferredSkills());
        breakdown.setSemanticSimilarityWeight(w.getSemanticSimilarity());
        breakdown.setExperienceFitWeight(w.getExperienceFit());
        breakdown.setEducationFitWeight(w.getEducationFit());

        breakdown.setMatchedRequiredSkills(result.getMatchedRequiredSkills());
        breakdown.setMissingRequiredSkills(result.getMissingRequiredSkills());
        breakdown.setMatchedPreferredSkills(result.getMatchedPreferredSkills());
        breakdown.setMissingPreferredSkills(result.getMissingPreferredSkills());

        dto.setScoreBreakdown(breakdown);
        return dto;
    }

    public AuthResponse toAuthResponse(User user, String token) {
        if (user == null) return null;
        return new AuthResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().name(),
                token
        );
    }
}
