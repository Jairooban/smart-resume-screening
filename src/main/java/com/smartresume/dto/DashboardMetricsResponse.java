package com.smartresume.dto;

import java.util.List;
import java.util.Map;

public class DashboardMetricsResponse {
    private long totalJobs;
    private long activeJobs;
    private long totalResumes;
    private long totalScreenings;
    private double averageMatchScore;
    private long shortlistedCount;
    private long reviewCount;
    private long rejectedCount;
    private List<ScreeningResultResponse> topCandidates;
    private List<SkillFrequencyResponse> topSkillsFrequency;
    private ScoreDistributionResponse scoreDistribution;

    public DashboardMetricsResponse() {
    }

    public long getTotalJobs() {
        return totalJobs;
    }

    public void setTotalJobs(long totalJobs) {
        this.totalJobs = totalJobs;
    }

    public long getActiveJobs() {
        return activeJobs;
    }

    public void setActiveJobs(long activeJobs) {
        this.activeJobs = activeJobs;
    }

    public long getTotalResumes() {
        return totalResumes;
    }

    public void setTotalResumes(long totalResumes) {
        this.totalResumes = totalResumes;
    }

    public long getTotalScreenings() {
        return totalScreenings;
    }

    public void setTotalScreenings(long totalScreenings) {
        this.totalScreenings = totalScreenings;
    }

    public double getAverageMatchScore() {
        return averageMatchScore;
    }

    public void setAverageMatchScore(double averageMatchScore) {
        this.averageMatchScore = averageMatchScore;
    }

    public long getShortlistedCount() {
        return shortlistedCount;
    }

    public void setShortlistedCount(long shortlistedCount) {
        this.shortlistedCount = shortlistedCount;
    }

    public long getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(long reviewCount) {
        this.reviewCount = reviewCount;
    }

    public long getRejectedCount() {
        return rejectedCount;
    }

    public void setRejectedCount(long rejectedCount) {
        this.rejectedCount = rejectedCount;
    }

    public List<ScreeningResultResponse> getTopCandidates() {
        return topCandidates;
    }

    public void setTopCandidates(List<ScreeningResultResponse> topCandidates) {
        this.topCandidates = topCandidates;
    }

    public List<SkillFrequencyResponse> getTopSkillsFrequency() {
        return topSkillsFrequency;
    }

    public void setTopSkillsFrequency(List<SkillFrequencyResponse> topSkillsFrequency) {
        this.topSkillsFrequency = topSkillsFrequency;
    }

    public ScoreDistributionResponse getScoreDistribution() {
        return scoreDistribution;
    }

    public void setScoreDistribution(ScoreDistributionResponse scoreDistribution) {
        this.scoreDistribution = scoreDistribution;
    }
}
