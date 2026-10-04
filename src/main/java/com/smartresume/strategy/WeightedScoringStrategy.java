package com.smartresume.strategy;

import com.smartresume.config.ScoringProperties;
import com.smartresume.model.CandidateStatus;
import com.smartresume.model.JobOpening;
import com.smartresume.model.Resume;
import com.smartresume.model.ScreeningResult;
import com.smartresume.nlp.SkillExtractor;
import com.smartresume.nlp.TfIdfCalculator;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Weighted Composite Scoring Strategy implementing configurable weights:
 * - Required skills match (40%)
 * - Preferred skills match (15%)
 * - Semantic text similarity (TF-IDF cosine) (25%)
 * - Experience fit (12%)
 * - Education fit (8%)
 */
@Component
public class WeightedScoringStrategy implements ScoringStrategy {

    private final ScoringProperties scoringProperties;
    private final TfIdfCalculator tfIdfCalculator;
    private final SkillExtractor skillExtractor;
    private final ExplanationGenerator explanationGenerator;

    public WeightedScoringStrategy(ScoringProperties scoringProperties,
                                   TfIdfCalculator tfIdfCalculator,
                                   SkillExtractor skillExtractor,
                                   ExplanationGenerator explanationGenerator) {
        this.scoringProperties = scoringProperties;
        this.tfIdfCalculator = tfIdfCalculator;
        this.skillExtractor = skillExtractor;
        this.explanationGenerator = explanationGenerator;
    }

    @Override
    public ScreeningResult evaluate(JobOpening jobOpening, Resume resume) {
        ScoringProperties.Weights weights = scoringProperties.getWeights();
        ScoringProperties.Thresholds thresholds = scoringProperties.getThresholds();

        // 1. Skill Canonicalization
        Set<String> candidateSkills = resume.getParsedSkills().stream()
                .map(skillExtractor::getCanonicalSkillName)
                .collect(Collectors.toSet());

        Set<String> requiredSkills = jobOpening.getRequiredSkills().stream()
                .map(skillExtractor::getCanonicalSkillName)
                .collect(Collectors.toSet());

        Set<String> preferredSkills = jobOpening.getPreferredSkills().stream()
                .map(skillExtractor::getCanonicalSkillName)
                .collect(Collectors.toSet());

        // 2. Required Skills Match
        Set<String> matchedReq = new HashSet<>();
        Set<String> missingReq = new HashSet<>();
        for (String req : requiredSkills) {
            if (candidateSkills.contains(req)) {
                matchedReq.add(req);
            } else {
                missingReq.add(req);
            }
        }
        double reqScore = requiredSkills.isEmpty() ? 100.0 :
                ((double) matchedReq.size() / requiredSkills.size()) * 100.0;

        // 3. Preferred Skills Match
        Set<String> matchedPref = new HashSet<>();
        Set<String> missingPref = new HashSet<>();
        for (String pref : preferredSkills) {
            if (candidateSkills.contains(pref)) {
                matchedPref.add(pref);
            } else {
                missingPref.add(pref);
            }
        }
        double prefScore = preferredSkills.isEmpty() ? 100.0 :
                ((double) matchedPref.size() / preferredSkills.size()) * 100.0;

        // 4. Semantic Similarity (TF-IDF Cosine Similarity)
        double semanticScore = tfIdfCalculator.calculateCosineSimilarity(
                resume.getRawText(),
                jobOpening.getTitle() + " " + jobOpening.getDescription()
        );

        // 5. Experience Fit
        double expScore;
        double minExp = jobOpening.getMinExperienceYears();
        double candExp = resume.getExperienceYears();
        if (minExp <= 0.0) {
            expScore = 100.0;
        } else if (candExp >= minExp) {
            expScore = 100.0;
        } else {
            expScore = (candExp / minExp) * 100.0;
        }

        // 6. Education Fit
        int jobEduRank = jobOpening.getEducationRequirement().getRank();
        int candEduRank = resume.getEducationLevel().getRank();
        double eduScore;
        if (candEduRank >= jobEduRank) {
            eduScore = 100.0;
        } else {
            eduScore = ((double) candEduRank / jobEduRank) * 100.0;
        }

        // 7. Weighted Composite Total Score (out of 100)
        double totalWeight = weights.getRequiredSkills() + weights.getPreferredSkills()
                + weights.getSemanticSimilarity() + weights.getExperienceFit() + weights.getEducationFit();

        double weightedSum = (reqScore * weights.getRequiredSkills())
                + (prefScore * weights.getPreferredSkills())
                + (semanticScore * weights.getSemanticSimilarity())
                + (expScore * weights.getExperienceFit())
                + (eduScore * weights.getEducationFit());

        double finalScore = totalWeight > 0 ? (weightedSum / totalWeight) : 0.0;
        finalScore = Math.round(finalScore * 10.0) / 10.0;

        // 8. Auto-assigned candidate status based on score thresholds
        CandidateStatus status;
        if (finalScore >= thresholds.getShortlistMinScore()) {
            status = CandidateStatus.SHORTLISTED;
        } else if (finalScore >= thresholds.getReviewMinScore()) {
            status = CandidateStatus.REVIEW;
        } else {
            status = CandidateStatus.REJECTED;
        }

        // 9. Generate Natural Language Explanation
        String explanation = explanationGenerator.generateExplanation(
                jobOpening, resume, finalScore,
                reqScore, prefScore, semanticScore, expScore, eduScore,
                matchedReq, missingReq, matchedPref, missingPref, status
        );

        ScreeningResult result = new ScreeningResult();
        result.setJobOpening(jobOpening);
        result.setResume(resume);
        result.setTotalScore(finalScore);
        result.setRequiredSkillsScore(Math.round(reqScore * 10.0) / 10.0);
        result.setPreferredSkillsScore(Math.round(prefScore * 10.0) / 10.0);
        result.setSemanticSimilarityScore(Math.round(semanticScore * 10.0) / 10.0);
        result.setExperienceScore(Math.round(expScore * 10.0) / 10.0);
        result.setEducationScore(Math.round(eduScore * 10.0) / 10.0);
        result.setMatchedRequiredSkills(matchedReq);
        result.setMissingRequiredSkills(missingReq);
        result.setMatchedPreferredSkills(matchedPref);
        result.setMissingPreferredSkills(missingPref);
        result.setScoreExplanation(explanation);
        result.setStatus(status);
        result.setManualOverride(false);
        result.setCreatedAt(LocalDateTime.now());
        result.setUpdatedAt(LocalDateTime.now());

        return result;
    }
}
