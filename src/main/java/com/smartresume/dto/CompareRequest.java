package com.smartresume.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public class CompareRequest {

    @NotEmpty(message = "Candidate screening IDs list cannot be empty")
    @Size(min = 2, max = 4, message = "Select between 2 and 4 candidates to compare")
    private List<Long> screeningResultIds;

    public CompareRequest() {
    }

    public CompareRequest(List<Long> screeningResultIds) {
        this.screeningResultIds = screeningResultIds;
    }

    public List<Long> getScreeningResultIds() {
        return screeningResultIds;
    }

    public void setScreeningResultIds(List<Long> screeningResultIds) {
        this.screeningResultIds = screeningResultIds;
    }
}
