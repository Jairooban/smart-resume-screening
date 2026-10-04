package com.smartresume.dto;

public class SkillFrequencyResponse {
    private String skill;
    private long count;
    private double percentage;

    public SkillFrequencyResponse() {
    }

    public SkillFrequencyResponse(String skill, long count, double percentage) {
        this.skill = skill;
        this.count = count;
        this.percentage = percentage;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
}
