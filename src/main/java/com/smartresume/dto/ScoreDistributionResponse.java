package com.smartresume.dto;

import java.util.List;

public class ScoreDistributionResponse {
    private List<String> ranges; // e.g. ["0-20%", "21-40%", "41-60%", "61-80%", "81-100%"]
    private List<Long> counts;

    public ScoreDistributionResponse() {
    }

    public ScoreDistributionResponse(List<String> ranges, List<Long> counts) {
        this.ranges = ranges;
        this.counts = counts;
    }

    public List<String> getRanges() {
        return ranges;
    }

    public void setRanges(List<String> ranges) {
        this.ranges = ranges;
    }

    public List<Long> getCounts() {
        return counts;
    }

    public void setCounts(List<Long> counts) {
        this.counts = counts;
    }
}
