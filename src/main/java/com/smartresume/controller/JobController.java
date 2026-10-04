package com.smartresume.controller;

import com.smartresume.dto.ApiResponse;
import com.smartresume.dto.JobRequest;
import com.smartresume.dto.JobResponse;
import com.smartresume.service.JobService;
import com.smartresume.service.ScreeningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@Tag(name = "Job Openings", description = "Endpoints for managing job postings and requirements")
public class JobController {

    private final JobService jobService;
    private final ScreeningService screeningService;

    public JobController(JobService jobService, ScreeningService screeningService) {
        this.jobService = jobService;
        this.screeningService = screeningService;
    }

    @GetMapping
    @Operation(summary = "List all job openings with applicant metrics")
    public ResponseEntity<ApiResponse<List<JobResponse>>> getAllJobs(
            @RequestParam(value = "search", required = false) String search) {
        List<JobResponse> jobs = jobService.getAllJobs(search);
        return ResponseEntity.ok(ApiResponse.ok(jobs));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get single job opening details by ID")
    public ResponseEntity<ApiResponse<JobResponse>> getJobById(@PathVariable Long id) {
        JobResponse job = jobService.getJobById(id);
        return ResponseEntity.ok(ApiResponse.ok(job));
    }

    @PostMapping
    @Operation(summary = "Create a new job opening")
    public ResponseEntity<ApiResponse<JobResponse>> createJob(@Valid @RequestBody JobRequest request) {
        JobResponse created = jobService.createJob(request);
        return new ResponseEntity<>(ApiResponse.ok("Job opening created successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing job opening")
    public ResponseEntity<ApiResponse<JobResponse>> updateJob(@PathVariable Long id, @Valid @RequestBody JobRequest request) {
        JobResponse updated = jobService.updateJob(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Job opening updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a job opening")
    public ResponseEntity<ApiResponse<String>> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return ResponseEntity.ok(ApiResponse.ok("Job opening deleted successfully", "Job " + id + " deleted"));
    }

    @PostMapping("/{id}/rescreen")
    @Operation(summary = "Re-evaluate screening for all resumes against this job")
    public ResponseEntity<ApiResponse<String>> rescreenJob(@PathVariable Long id) {
        screeningService.screenAllResumesForJob(id);
        return ResponseEntity.ok(ApiResponse.ok("Screening re-evaluation completed", "OK"));
    }
}
