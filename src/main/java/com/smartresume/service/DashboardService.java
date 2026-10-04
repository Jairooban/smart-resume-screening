package com.smartresume.service;

import com.smartresume.dto.DashboardMetricsResponse;
import com.smartresume.dto.ScoreDistributionResponse;
import com.smartresume.dto.SkillFrequencyResponse;
import com.smartresume.model.CandidateStatus;
import com.smartresume.model.Resume;
import com.smartresume.model.ScreeningResult;
import com.smartresume.repository.JobOpeningRepository;
import com.smartresume.repository.ResumeRepository;
import com.smartresume.repository.ScreeningResultRepository;
import com.smartresume.util.ModelMapperUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service aggregating high-level analytics, KPI metrics, skill distributions,
 * and score graphs for the dashboard.
 */
@Service
public class DashboardService {

    private final JobOpeningRepository jobOpeningRepository;
    private final ResumeRepository resumeRepository;
    private final ScreeningResultRepository screeningResultRepository;
    private final ModelMapperUtil modelMapperUtil;

    public DashboardService(JobOpeningRepository jobOpeningRepository,
                            ResumeRepository resumeRepository,
                            ScreeningResultRepository screeningResultRepository,
                            ModelMapperUtil modelMapperUtil) {
        this.jobOpeningRepository = jobOpeningRepository;
        this.resumeRepository = resumeRepository;
        this.screeningResultRepository = screeningResultRepository;
        this.modelMapperUtil = modelMapperUtil;
    }

    @Transactional(readOnly = true)
    public DashboardMetricsResponse getDashboardMetrics() {
        DashboardMetricsResponse metrics = new DashboardMetricsResponse();

        long totalJobs = jobOpeningRepository.count();
        long activeJobs = jobOpeningRepository.findByActiveTrueOrderByCreatedAtDesc().size();
        long totalResumes = resumeRepository.count();
        long totalScreenings = screeningResultRepository.count();

        Double avgScore = screeningResultRepository.getAverageMatchScore();
        double avg = avgScore != null ? Math.round(avgScore * 10.0) / 10.0 : 0.0;

        List<ScreeningResult> allResults = screeningResultRepository.findAll();
        long shortlisted = allResults.stream().filter(r -> r.getStatus() == CandidateStatus.SHORTLISTED).count();
        long review = allResults.stream().filter(r -> r.getStatus() == CandidateStatus.REVIEW).count();
        long rejected = allResults.stream().filter(r -> r.getStatus() == CandidateStatus.REJECTED).count();

        // Top 5 Candidates
        List<ScreeningResult> topList = screeningResultRepository.findTopCandidates().stream()
                .limit(5)
                .collect(Collectors.toList());

        // Skills frequency calculation
        List<Resume> resumes = resumeRepository.findAll();
        Map<String, Long> skillCountMap = new HashMap<>();
        for (Resume r : resumes) {
            for (String skill : r.getParsedSkills()) {
                if (!skill.isBlank()) {
                    skillCountMap.put(skill, skillCountMap.getOrDefault(skill, 0L) + 1);
                }
            }
        }

        List<SkillFrequencyResponse> topSkills = skillCountMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .map(entry -> {
                    double pct = totalResumes > 0 ? ((double) entry.getValue() / totalResumes) * 100.0 : 0.0;
                    return new SkillFrequencyResponse(entry.getKey(), entry.getValue(), Math.round(pct * 10.0) / 10.0);
                })
                .collect(Collectors.toList());

        // Score Distribution across 5 tiers
        long b1 = 0, b2 = 0, b3 = 0, b4 = 0, b5 = 0;
        for (ScreeningResult r : allResults) {
            double score = r.getTotalScore();
            if (score <= 20.0) b1++;
            else if (score <= 40.0) b2++;
            else if (score <= 60.0) b3++;
            else if (score <= 80.0) b4++;
            else b5++;
        }

        ScoreDistributionResponse dist = new ScoreDistributionResponse(
                Arrays.asList("0-20%", "21-40%", "41-60%", "61-80%", "81-100%"),
                Arrays.asList(b1, b2, b3, b4, b5)
        );

        metrics.setTotalJobs(totalJobs);
        metrics.setActiveJobs(activeJobs);
        metrics.setTotalResumes(totalResumes);
        metrics.setTotalScreenings(totalScreenings);
        metrics.setAverageMatchScore(avg);
        metrics.setShortlistedCount(shortlisted);
        metrics.setReviewCount(review);
        metrics.setRejectedCount(rejected);
        metrics.setTopCandidates(topList.stream().map(modelMapperUtil::toScreeningResultResponse).collect(Collectors.toList()));
        metrics.setTopSkillsFrequency(topSkills);
        metrics.setScoreDistribution(dist);

        return metrics;
    }
}
