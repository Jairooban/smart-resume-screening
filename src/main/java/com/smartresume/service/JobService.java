package com.smartresume.service;

import com.smartresume.dto.JobRequest;
import com.smartresume.dto.JobResponse;
import com.smartresume.exception.ResourceNotFoundException;
import com.smartresume.model.JobOpening;
import com.smartresume.repository.JobOpeningRepository;
import com.smartresume.repository.ScreeningResultRepository;
import com.smartresume.util.ModelMapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service managing Job Openings and requirements.
 */
@Service
public class JobService {

    private static final Logger log = LoggerFactory.getLogger(JobService.class);

    private final JobOpeningRepository jobOpeningRepository;
    private final ScreeningResultRepository screeningResultRepository;
    private final ModelMapperUtil modelMapperUtil;
    private final ScreeningService screeningService;

    public JobService(JobOpeningRepository jobOpeningRepository,
                      ScreeningResultRepository screeningResultRepository,
                      ModelMapperUtil modelMapperUtil,
                      @Lazy ScreeningService screeningService) {
        this.jobOpeningRepository = jobOpeningRepository;
        this.screeningResultRepository = screeningResultRepository;
        this.modelMapperUtil = modelMapperUtil;
        this.screeningService = screeningService;
    }

    @Transactional(readOnly = true)
    public List<JobResponse> getAllJobs(String keyword) {
        List<JobOpening> jobs;
        if (keyword != null && !keyword.trim().isEmpty()) {
            jobs = jobOpeningRepository.searchJobs(keyword.trim());
        } else {
            jobs = jobOpeningRepository.findByActiveTrueOrderByCreatedAtDesc();
        }

        return jobs.stream().map(job -> {
            long count = screeningResultRepository.countByJobOpeningId(job.getId());
            Double avg = screeningResultRepository.getAverageMatchScoreByJob(job.getId());
            return modelMapperUtil.toJobResponse(job, count, avg);
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public JobOpening getJobEntity(Long id) {
        return jobOpeningRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job opening not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public JobResponse getJobById(Long id) {
        JobOpening job = getJobEntity(id);
        long count = screeningResultRepository.countByJobOpeningId(job.getId());
        Double avg = screeningResultRepository.getAverageMatchScoreByJob(job.getId());
        return modelMapperUtil.toJobResponse(job, count, avg);
    }

    @Transactional
    public JobResponse createJob(JobRequest request) {
        JobOpening job = new JobOpening(
                request.getTitle(),
                request.getDepartment(),
                request.getLocation(),
                request.getDescription(),
                request.getRequiredSkills(),
                request.getPreferredSkills(),
                request.getMinExperienceYears(),
                request.getEducationRequirement()
        );
        job.setActive(request.isActive());

        JobOpening saved = jobOpeningRepository.save(job);
        log.info("Created new Job Opening: '{}' (ID: {})", saved.getTitle(), saved.getId());

        // Automatically screen all existing resumes against the new job opening
        screeningService.screenAllResumesForJob(saved.getId());

        return modelMapperUtil.toJobResponse(saved, 0, null);
    }

    @Transactional
    public JobResponse updateJob(Long id, JobRequest request) {
        JobOpening job = getJobEntity(id);
        job.setTitle(request.getTitle());
        job.setDepartment(request.getDepartment());
        job.setLocation(request.getLocation());
        job.setDescription(request.getDescription());
        job.setRequiredSkills(request.getRequiredSkills());
        job.setPreferredSkills(request.getPreferredSkills());
        job.setMinExperienceYears(request.getMinExperienceYears());
        job.setEducationRequirement(request.getEducationRequirement());
        job.setActive(request.isActive());

        JobOpening updated = jobOpeningRepository.save(job);
        log.info("Updated Job Opening: '{}' (ID: {})", updated.getTitle(), updated.getId());

        // Re-screen existing candidates with updated criteria
        screeningService.screenAllResumesForJob(updated.getId());

        long count = screeningResultRepository.countByJobOpeningId(updated.getId());
        Double avg = screeningResultRepository.getAverageMatchScoreByJob(updated.getId());
        return modelMapperUtil.toJobResponse(updated, count, avg);
    }

    @Transactional
    public void deleteJob(Long id) {
        JobOpening job = getJobEntity(id);
        screeningResultRepository.deleteByJobOpeningId(id);
        jobOpeningRepository.delete(job);
        log.info("Deleted Job Opening with ID: {}", id);
    }
}
