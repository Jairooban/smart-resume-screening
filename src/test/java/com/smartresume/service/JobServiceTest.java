package com.smartresume.service;

import com.smartresume.dto.JobRequest;
import com.smartresume.dto.JobResponse;
import com.smartresume.model.EducationLevel;
import com.smartresume.model.JobOpening;
import com.smartresume.repository.JobOpeningRepository;
import com.smartresume.repository.ScreeningResultRepository;
import com.smartresume.util.ModelMapperUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobOpeningRepository jobOpeningRepository;

    @Mock
    private ScreeningResultRepository screeningResultRepository;

    @Mock
    private ModelMapperUtil modelMapperUtil;

    @Mock
    private ScreeningService screeningService;

    @InjectMocks
    private JobService jobService;

    @Test
    void testCreateJob() {
        JobRequest req = new JobRequest();
        req.setTitle("Software Engineer");
        req.setDescription("Building Java backends");
        req.setRequiredSkills(new HashSet<>());
        req.setMinExperienceYears(2.0);
        req.setEducationRequirement(EducationLevel.BACHELORS);
        req.setActive(true);

        JobOpening savedJob = new JobOpening();
        savedJob.setId(10L);
        savedJob.setTitle("Software Engineer");

        JobResponse responseDto = new JobResponse();
        responseDto.setId(10L);
        responseDto.setTitle("Software Engineer");

        when(jobOpeningRepository.save(any(JobOpening.class))).thenReturn(savedJob);
        when(modelMapperUtil.toJobResponse(eq(savedJob), eq(0L), isNull())).thenReturn(responseDto);

        JobResponse result = jobService.createJob(req);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Software Engineer", result.getTitle());
        verify(jobOpeningRepository, times(1)).save(any(JobOpening.class));
        verify(screeningService, times(1)).screenAllResumesForJob(10L);
    }

    @Test
    void testGetJobById() {
        JobOpening job = new JobOpening();
        job.setId(5L);
        job.setTitle("DevOps Lead");

        JobResponse responseDto = new JobResponse();
        responseDto.setId(5L);
        responseDto.setTitle("DevOps Lead");

        when(jobOpeningRepository.findById(5L)).thenReturn(Optional.of(job));
        when(screeningResultRepository.countByJobOpeningId(5L)).thenReturn(3L);
        when(screeningResultRepository.getAverageMatchScoreByJob(5L)).thenReturn(85.0);
        when(modelMapperUtil.toJobResponse(job, 3L, 85.0)).thenReturn(responseDto);

        JobResponse result = jobService.getJobById(5L);

        assertNotNull(result);
        assertEquals(5L, result.getId());
        assertEquals("DevOps Lead", result.getTitle());
    }
}
