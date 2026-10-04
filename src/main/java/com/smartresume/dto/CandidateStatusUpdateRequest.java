package com.smartresume.dto;

import com.smartresume.model.CandidateStatus;
import jakarta.validation.constraints.NotNull;

public class CandidateStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private CandidateStatus status;

    private String notes;

    public CandidateStatusUpdateRequest() {
    }

    public CandidateStatusUpdateRequest(CandidateStatus status, String notes) {
        this.status = status;
        this.notes = notes;
    }

    public CandidateStatus getStatus() {
        return status;
    }

    public void setStatus(CandidateStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
