package com.smartresume.controller;

import com.smartresume.dto.ApiResponse;
import com.smartresume.dto.ResumeResponse;
import com.smartresume.model.Resume;
import com.smartresume.repository.ResumeRepository;
import com.smartresume.repository.ScreeningResultRepository;
import com.smartresume.service.ResumeParserService;
import com.smartresume.service.ScreeningService;
import com.smartresume.util.ModelMapperUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/resumes")
@Tag(name = "Resumes", description = "Resume upload, text parsing, and storage endpoints")
public class ResumeController {

    private final ResumeParserService resumeParserService;
    private final ResumeRepository resumeRepository;
    private final ScreeningResultRepository screeningResultRepository;
    private final ScreeningService screeningService;
    private final ModelMapperUtil modelMapperUtil;

    public ResumeController(ResumeParserService resumeParserService,
                            ResumeRepository resumeRepository,
                            ScreeningResultRepository screeningResultRepository,
                            ScreeningService screeningService,
                            ModelMapperUtil modelMapperUtil) {
        this.resumeParserService = resumeParserService;
        this.resumeRepository = resumeRepository;
        this.screeningResultRepository = screeningResultRepository;
        this.screeningService = screeningService;
        this.modelMapperUtil = modelMapperUtil;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload one or multiple resumes (PDF/DOCX) for parsing and screening")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadResumes(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "overwriteDuplicates", defaultValue = "false") boolean overwriteDuplicates,
            @RequestParam(value = "jobId", required = false) Long jobId) {

        List<ResumeResponse> uploadedList = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                Resume saved = resumeParserService.parseAndSaveResume(file, overwriteDuplicates);
                // Perform automated screening
                screeningService.screenResumeAgainstAllJobs(saved);
                uploadedList.add(modelMapperUtil.toResumeResponse(saved));
            } catch (Exception e) {
                errors.add("Failed to process '" + file.getOriginalFilename() + "': " + e.getMessage());
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("uploaded", uploadedList);
        result.put("totalUploaded", uploadedList.size());
        result.put("errors", errors);

        String message = uploadedList.size() + " of " + files.size() + " resumes processed successfully.";
        return ResponseEntity.ok(ApiResponse.ok(message, result));
    }

    @GetMapping
    @Operation(summary = "List all uploaded resumes")
    public ResponseEntity<ApiResponse<List<ResumeResponse>>> getAllResumes() {
        List<ResumeResponse> list = resumeRepository.findAll().stream()
                .map(modelMapperUtil::toResumeResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get single resume parsed details")
    public ResponseEntity<ApiResponse<ResumeResponse>> getResumeById(@PathVariable Long id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new com.smartresume.exception.ResourceNotFoundException("Resume not found: " + id));
        return ResponseEntity.ok(ApiResponse.ok(modelMapperUtil.toResumeResponse(resume)));
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "Download raw resume file")
    public ResponseEntity<Resource> downloadResumeFile(@PathVariable Long id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new com.smartresume.exception.ResourceNotFoundException("Resume not found: " + id));

        byte[] data = resume.getFileData();
        if (data == null || data.length == 0) {
            if (resume.getRawText() != null) {
                data = resume.getRawText().getBytes();
            } else {
                data = "Empty file".getBytes();
            }
        }

        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resume.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(resume.getFileType() != null ? resume.getFileType() : MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .contentLength(data.length)
                .body(resource);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a resume and associated screening results")
    public ResponseEntity<ApiResponse<String>> deleteResume(@PathVariable Long id) {
        screeningResultRepository.deleteByResumeId(id);
        resumeRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.ok("Resume deleted successfully", "OK"));
    }
}
