package com.smartresume.service;

import com.smartresume.dto.ScreeningResultResponse;
import com.smartresume.model.CandidateStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Service to generate CSV exports for shortlisted / ranked candidates.
 */
@Service
public class CsvExportService {

    private final ScreeningService screeningService;

    public CsvExportService(ScreeningService screeningService) {
        this.screeningService = screeningService;
    }

    public ByteArrayInputStream exportJobCandidatesToCsv(Long jobId, CandidateStatus statusFilter) {
        List<ScreeningResultResponse> candidates = screeningService.getRankedCandidatesForJob(
                jobId, null, null, null, null, statusFilter, "score", "desc"
        );

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(out, true, StandardCharsets.UTF_8)) {
            // Write UTF-8 BOM for Excel compatibility
            out.write(0xEF);
            out.write(0xBB);
            out.write(0xBF);

            // Header
            writer.println("Rank,Candidate Name,Email,Phone,Total Match Score (%),Required Skills Match (%),Semantic Similarity (%),Experience (Yrs),Education,Matched Skills,Missing Required Skills,Status,HR Notes");

            int rank = 1;
            for (ScreeningResultResponse c : candidates) {
                String matchedSkills = c.getScoreBreakdown() != null && c.getScoreBreakdown().getMatchedRequiredSkills() != null
                        ? String.join("; ", c.getScoreBreakdown().getMatchedRequiredSkills()) : "";
                String missingSkills = c.getScoreBreakdown() != null && c.getScoreBreakdown().getMissingRequiredSkills() != null
                        ? String.join("; ", c.getScoreBreakdown().getMissingRequiredSkills()) : "";

                writer.printf("%d,\"%s\",\"%s\",\"%s\",%.1f,%.1f,%.1f,%.1f,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                        rank++,
                        escapeCsv(c.getCandidateName()),
                        escapeCsv(c.getEmail()),
                        escapeCsv(c.getPhone()),
                        c.getTotalScore() != null ? c.getTotalScore() : 0.0,
                        c.getScoreBreakdown() != null ? c.getScoreBreakdown().getRequiredSkillsScore() : 0.0,
                        c.getScoreBreakdown() != null ? c.getScoreBreakdown().getSemanticSimilarityScore() : 0.0,
                        c.getExperienceYears() != null ? c.getExperienceYears() : 0.0,
                        c.getEducationLevel() != null ? escapeCsv(c.getEducationLevel().getDisplayName()) : "",
                        escapeCsv(matchedSkills),
                        escapeCsv(missingSkills),
                        c.getStatus() != null ? c.getStatus().name() : "",
                        escapeCsv(c.getNotes() != null ? c.getNotes() : "")
                );
            }
            writer.flush();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate CSV export", e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        return value.replace("\"", "\"\"");
    }
}
