package com.smartresume.dto;

import java.util.List;

public class CompareCandidatesResponse {
    private Long jobOpeningId;
    private String jobTitle;
    private List<ScreeningResultResponse> candidates;

    public CompareCandidatesResponse() {
    }

    public CompareCandidatesResponse(Long jobOpeningId, String jobTitle, List<ScreeningResultResponse> candidates) {
        this.jobOpeningId = jobOpeningId;
        this.jobTitle = jobTitle;
        this.candidates = candidates;
    }

    public Long getJobOpeningId() {
        return jobOpeningId;
    }

    public void setJobOpeningId(Long jobOpeningId) {
        this.jobOpeningId = jobOpeningId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public List<ScreeningResultResponse> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<ScreeningResultResponse> candidates) {
        this.candidates = candidates;
    }
}
