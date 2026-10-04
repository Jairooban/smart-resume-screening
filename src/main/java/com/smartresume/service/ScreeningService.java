package com.smartresume.service;

import com.smartresume.dto.CandidateStatusUpdateRequest;
import com.smartresume.dto.CompareCandidatesResponse;
import com.smartresume.dto.ScreeningResultResponse;
import com.smartresume.exception.ResourceNotFoundException;
import com.smartresume.model.*;
import com.smartresume.repository.JobOpeningRepository;
import com.smartresume.repository.ResumeRepository;
import com.smartresume.repository.ScreeningResultRepository;
import com.smartresume.strategy.ScoringStrategy;
import com.smartresume.util.ModelMapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service managing candidate screening, scoring evaluation, ranking,
 * status transitions, and comparative analysis.
 */
@Service
public class ScreeningService {

    private static final Logger log = LoggerFactory.getLogger(ScreeningService.class);

    private final ScreeningResultRepository screeningResultRepository;
    private final JobOpeningRepository jobOpeningRepository;
    private final ResumeRepository resumeRepository;
    private final ScoringStrategy scoringStrategy;
    private final ModelMapperUtil modelMapperUtil;

    public ScreeningService(ScreeningResultRepository screeningResultRepository,
                            JobOpeningRepository jobOpeningRepository,
                            ResumeRepository resumeRepository,
                            ScoringStrategy scoringStrategy,
                            ModelMapperUtil modelMapperUtil) {
        this.screeningResultRepository = screeningResultRepository;
        this.jobOpeningRepository = jobOpeningRepository;
        this.resumeRepository = resumeRepository;
        this.scoringStrategy = scoringStrategy;
        this.modelMapperUtil = modelMapperUtil;
    }

    /**
     * Screen a specific resume against a specific job opening.
     */
    @Transactional
    public ScreeningResult screenResumeForJob(JobOpening job, Resume resume) {
        ScreeningResult evaluated = scoringStrategy.evaluate(job, resume);

        Optional<ScreeningResult> existingOpt = screeningResultRepository.findByJobOpeningIdAndResumeId(job.getId(), resume.getId());
        if (existingOpt.isPresent()) {
            ScreeningResult existing = existingOpt.get();
            // Preserve manual status override if already set by HR
            if (existing.isManualOverride()) {
                evaluated.setStatus(existing.getStatus());
                evaluated.setManualOverride(true);
                evaluated.setNotes(existing.getNotes());
            }
            evaluated.setId(existing.getId());
        }

        return screeningResultRepository.save(evaluated);
    }

    /**
     * Screen a newly uploaded resume against all currently active job openings.
     */
    @Transactional
    public List<ScreeningResult> screenResumeAgainstAllJobs(Resume resume) {
        List<JobOpening> activeJobs = jobOpeningRepository.findByActiveTrueOrderByCreatedAtDesc();
        List<ScreeningResult> results = new ArrayList<>();
        for (JobOpening job : activeJobs) {
            results.add(screenResumeForJob(job, resume));
        }
        return results;
    }

    /**
     * Screen all resumes against a given job opening.
     */
    @Transactional
    public List<ScreeningResult> screenAllResumesForJob(Long jobOpeningId) {
        JobOpening job = jobOpeningRepository.findById(jobOpeningId)
                .orElseThrow(() -> new ResourceNotFoundException("Job opening not found: " + jobOpeningId));

        List<Resume> allResumes = resumeRepository.findAll();
        List<ScreeningResult> results = new ArrayList<>();
        for (Resume resume : allResumes) {
            results.add(screenResumeForJob(job, resume));
        }
        log.info("Screened {} resumes against job opening '{}' (ID: {})",
                results.size(), job.getTitle(), job.getId());
        return results;
    }

    /**
     * Get ranked and filtered candidates for a job.
     */
    @Transactional(readOnly = true)
    public List<ScreeningResultResponse> getRankedCandidatesForJob(Long jobOpeningId,
                                                                   String query,
                                                                   Double minScore,
                                                                   String skillFilter,
                                                                   Double minExperience,
                                                                   CandidateStatus statusFilter,
                                                                   String sortBy,
                                                                   String sortDir) {
        // Ensure job exists
        if (!jobOpeningRepository.existsById(jobOpeningId)) {
            throw new ResourceNotFoundException("Job opening not found: " + jobOpeningId);
        }

        List<ScreeningResult> list = screeningResultRepository.findByJobOpeningIdOrderByTotalScoreDesc(jobOpeningId);

        // Apply filters in stream
        var stream = list.stream();

        if (query != null && !query.trim().isEmpty()) {
            String q = query.trim().toLowerCase();
            stream = stream.filter(r ->
                    (r.getResume().getCandidateName() != null && r.getResume().getCandidateName().toLowerCase().contains(q))
                    || (r.getResume().getEmail() != null && r.getResume().getEmail().toLowerCase().contains(q))
                    || (r.getResume().getParsedSkillsText() != null && r.getResume().getParsedSkillsText().toLowerCase().contains(q))
            );
        }

        if (minScore != null && minScore > 0) {
            stream = stream.filter(r -> r.getTotalScore() >= minScore);
        }

        if (skillFilter != null && !skillFilter.trim().isEmpty()) {
            String skillLower = skillFilter.trim().toLowerCase();
            stream = stream.filter(r -> r.getResume().getParsedSkills().stream()
                    .anyMatch(s -> s.toLowerCase().contains(skillLower)));
        }

        if (minExperience != null && minExperience > 0) {
            stream = stream.filter(r -> r.getResume().getExperienceYears() >= minExperience);
        }

        if (statusFilter != null) {
            stream = stream.filter(r -> r.getStatus() == statusFilter);
        }

        List<ScreeningResult> filtered = stream.collect(Collectors.toList());

        // Sorting
        Comparator<ScreeningResult> comparator;
        if ("experience".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(r -> r.getResume().getExperienceYears());
        } else if ("name".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(r -> r.getResume().getCandidateName() != null ? r.getResume().getCandidateName() : "");
        } else if ("date".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(ScreeningResult::getCreatedAt);
        } else {
            comparator = Comparator.comparing(ScreeningResult::getTotalScore);
        }

        if (!"asc".equalsIgnoreCase(sortDir)) {
            comparator = comparator.reversed();
        }

        filtered.sort(comparator);

        return filtered.stream()
                .map(modelMapperUtil::toScreeningResultResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ScreeningResultResponse getScreeningResultById(Long id) {
        ScreeningResult result = screeningResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Screening result not found: " + id));
        return modelMapperUtil.toScreeningResultResponse(result);
    }

    @Transactional
    public ScreeningResultResponse updateCandidateStatus(Long id, CandidateStatusUpdateRequest request) {
        ScreeningResult result = screeningResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Screening result not found: " + id));

        result.setStatus(request.getStatus());
        result.setManualOverride(true);
        if (request.getNotes() != null) {
            result.setNotes(request.getNotes());
        }

        ScreeningResult saved = screeningResultRepository.save(result);
        log.info("Updated candidate status for result ID {} to {}", id, request.getStatus());
        return modelMapperUtil.toScreeningResultResponse(saved);
    }

    @Transactional(readOnly = true)
    public CompareCandidatesResponse compareCandidates(List<Long> resultIds) {
        if (resultIds == null || resultIds.size() < 2) {
            throw new IllegalArgumentException("At least 2 candidates must be selected for comparison.");
        }

        List<ScreeningResult> results = screeningResultRepository.findAllById(resultIds);
        if (results.size() < resultIds.size()) {
            throw new ResourceNotFoundException("One or more candidates could not be found.");
        }

        JobOpening job = results.get(0).getJobOpening();
        List<ScreeningResultResponse> dtos = results.stream()
                .map(modelMapperUtil::toScreeningResultResponse)
                .collect(Collectors.toList());

        return new CompareCandidatesResponse(job.getId(), job.getTitle(), dtos);
    }
}
