package com.smartresume.strategy;

import com.smartresume.model.CandidateStatus;
import com.smartresume.model.JobOpening;
import com.smartresume.model.Resume;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Generates insightful, human-readable explanations detailing why a candidate
 * received their match score and recommended action.
 */
@Component
public class ExplanationGenerator {

    public String generateExplanation(JobOpening job, Resume resume,
                                       double totalScore,
                                       double reqScore, double prefScore,
                                       double semScore, double expScore, double eduScore,
                                       Set<String> matchedReq, Set<String> missingReq,
                                       Set<String> matchedPref, Set<String> missingPref,
                                       CandidateStatus status) {

        StringBuilder sb = new StringBuilder();

        // 1. Overall assessment
        if (status == CandidateStatus.SHORTLISTED) {
            sb.append("Strong Fit (").append(String.format("%.1f", totalScore)).append("%): Candidate demonstrates strong alignment with core job requirements and technical background. ");
        } else if (status == CandidateStatus.REVIEW) {
            sb.append("Moderate Fit (").append(String.format("%.1f", totalScore)).append("%): Candidate meets several criteria but has a few technical or experience gaps worth reviewing. ");
        } else {
            sb.append("Low Fit (").append(String.format("%.1f", totalScore)).append("%): Candidate lacks critical required skills or qualifications for this position. ");
        }

        // 2. Required Skills Breakdown
        if (missingReq.isEmpty()) {
            sb.append("Candidate matches 100% of mandatory required skills (")
              .append(String.join(", ", matchedReq))
              .append("). ");
        } else {
            sb.append("Matched ").append(matchedReq.size()).append(" of ")
              .append(matchedReq.size() + missingReq.size()).append(" required skills (Matched: ")
              .append(matchedReq.isEmpty() ? "None" : String.join(", ", matchedReq))
              .append("; Missing: ").append(String.join(", ", missingReq)).append("). ");
        }

        // 3. Preferred Skills Breakdown
        if (!job.getPreferredSkills().isEmpty()) {
            if (!matchedPref.isEmpty()) {
                sb.append("Bonus skills found: ").append(String.join(", ", matchedPref)).append(". ");
            } else {
                sb.append("No optional preferred skills identified. ");
            }
        }

        // 4. Experience assessment
        if (resume.getExperienceYears() >= job.getMinExperienceYears()) {
            sb.append("Experience requirement met with ")
              .append(resume.getExperienceYears()).append(" years (minimum required: ")
              .append(job.getMinExperienceYears()).append(" years). ");
        } else {
            sb.append("Experience is below target (Candidate: ")
              .append(resume.getExperienceYears()).append(" yrs vs Required: ")
              .append(job.getMinExperienceYears()).append(" yrs). ");
        }

        // 5. Education assessment
        if (resume.getEducationLevel().getRank() >= job.getEducationRequirement().getRank()) {
            sb.append("Holds ").append(resume.getEducationLevel().getDisplayName())
              .append(", satisfying the education criterion (")
              .append(job.getEducationRequirement().getDisplayName()).append("). ");
        } else {
            sb.append("Education level (").append(resume.getEducationLevel().getDisplayName())
              .append(") is below the preferred target of ")
              .append(job.getEducationRequirement().getDisplayName()).append(". ");
        }

        // 6. Semantic relevance summary
        if (semScore >= 60.0) {
            sb.append("Text semantic similarity is high (").append(String.format("%.1f", semScore)).append("%), indicating relevant domain experience.");
        } else if (semScore >= 30.0) {
            sb.append("Text semantic similarity is moderate (").append(String.format("%.1f", semScore)).append("%).");
        } else {
            sb.append("Text semantic similarity is relatively low (").append(String.format("%.1f", semScore)).append("%).");
        }

        return sb.toString();
    }
}
