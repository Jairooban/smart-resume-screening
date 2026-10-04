package com.smartresume.dto;

import com.smartresume.model.EducationLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

public class JobRequest {

    @NotBlank(message = "Job title is required")
    private String title;

    private String department;
    private String location;

    @NotBlank(message = "Job description is required")
    private String description;

    private Set<String> requiredSkills;
    private Set<String> preferredSkills;

    @NotNull(message = "Minimum experience is required")
    private Double minExperienceYears = 0.0;

    private EducationLevel educationRequirement = EducationLevel.BACHELORS;

    private boolean active = true;

    public JobRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<String> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(Set<String> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public Set<String> getPreferredSkills() {
        return preferredSkills;
    }

    public void setPreferredSkills(Set<String> preferredSkills) {
        this.preferredSkills = preferredSkills;
    }

    public Double getMinExperienceYears() {
        return minExperienceYears;
    }

    public void setMinExperienceYears(Double minExperienceYears) {
        this.minExperienceYears = minExperienceYears;
    }

    public EducationLevel getEducationRequirement() {
        return educationRequirement;
    }

    public void setEducationRequirement(EducationLevel educationRequirement) {
        this.educationRequirement = educationRequirement;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
