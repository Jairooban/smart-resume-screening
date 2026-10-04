package com.smartresume.controller;

import com.smartresume.dto.ApiResponse;
import com.smartresume.dto.CandidateStatusUpdateRequest;
import com.smartresume.dto.CompareCandidatesResponse;
import com.smartresume.dto.CompareRequest;
import com.smartresume.dto.ScreeningResultResponse;
import com.smartresume.model.CandidateStatus;
import com.smartresume.service.CsvExportService;
import com.smartresume.service.ScreeningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/api/screening")
@Tag(name = "Screening Engine", description = "Candidate scoring, ranking, filtering, comparison, and CSV export")
public class ScreeningController {

    private final ScreeningService screeningService;
    private final CsvExportService csvExportService;

    public ScreeningController(ScreeningService screeningService, CsvExportService csvExportService) {
        this.screeningService = screeningService;
        this.csvExportService = csvExportService;
    }

    @GetMapping("/jobs/{jobId}")
    @Operation(summary = "Get ranked candidates for a specific job opening with filtering and sorting")
    public ResponseEntity<ApiResponse<List<ScreeningResultResponse>>> getRankedCandidatesForJob(
            @PathVariable Long jobId,
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "minScore", required = false) Double minScore,
            @RequestParam(value = "skill", required = false) String skill,
            @RequestParam(value = "minExperience", required = false) Double minExperience,
            @RequestParam(value = "status", required = false) CandidateStatus status,
            @RequestParam(value = "sortBy", defaultValue = "score") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {

        List<ScreeningResultResponse> results = screeningService.getRankedCandidatesForJob(
                jobId, query, minScore, skill, minExperience, status, sortBy, sortDir
        );
        return ResponseEntity.ok(ApiResponse.ok(results));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detailed candidate screening result breakdown and explanation")
    public ResponseEntity<ApiResponse<ScreeningResultResponse>> getScreeningResultById(@PathVariable Long id) {
        ScreeningResultResponse result = screeningService.getScreeningResultById(id);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update candidate review status (SHORTLISTED, REVIEW, REJECTED) with optional HR notes")
    public ResponseEntity<ApiResponse<ScreeningResultResponse>> updateCandidateStatus(
            @PathVariable Long id,
            @Valid @RequestBody CandidateStatusUpdateRequest request) {
        ScreeningResultResponse updated = screeningService.updateCandidateStatus(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Candidate status updated successfully", updated));
    }

    @PostMapping("/compare")
    @Operation(summary = "Compare 2 to 4 candidates side-by-side with scoring breakdown")
    public ResponseEntity<ApiResponse<CompareCandidatesResponse>> compareCandidates(
            @Valid @RequestBody CompareRequest request) {
        CompareCandidatesResponse response = screeningService.compareCandidates(request.getScreeningResultIds());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/jobs/{jobId}/export-csv")
    @Operation(summary = "Export ranked candidates / shortlist to CSV spreadsheet")
    public ResponseEntity<Resource> exportShortlistToCsv(
            @PathVariable Long jobId,
            @RequestParam(value = "status", required = false) CandidateStatus status) {

        ByteArrayInputStream stream = csvExportService.exportJobCandidatesToCsv(jobId, status);
        InputStreamResource file = new InputStreamResource(stream);

        String filename = "job_" + jobId + "_candidates_" + (status != null ? status.name() : "all") + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(file);
    }
}
