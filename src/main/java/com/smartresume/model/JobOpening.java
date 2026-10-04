package com.smartresume.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Job Opening entity defining job requirements, description, and skill criteria.
 */
@Entity
@Table(name = "job_openings")
public class JobOpening {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 100)
    private String department;

    @Column(length = 100)
    private String location;

    @Lob
    @Column(nullable = false, columnDefinition = "CLOB")
    private String description;

    @Lob
    @Column(name = "required_skills", columnDefinition = "CLOB")
    private String requiredSkillsText; // Stored as comma-separated or JSON

    @Lob
    @Column(name = "preferred_skills", columnDefinition = "CLOB")
    private String preferredSkillsText;

    @Column(name = "min_experience_years", nullable = false)
    private Double minExperienceYears = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(name = "education_requirement", nullable = false, length = 30)
    private EducationLevel educationRequirement = EducationLevel.BACHELORS;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public JobOpening() {
    }

    public JobOpening(String title, String department, String location, String description,
                      Set<String> requiredSkills, Set<String> preferredSkills,
                      Double minExperienceYears, EducationLevel educationRequirement) {
        this.title = title;
        this.department = department;
        this.location = location;
        this.description = description;
        setRequiredSkills(requiredSkills);
        setPreferredSkills(preferredSkills);
        this.minExperienceYears = minExperienceYears != null ? minExperienceYears : 0.0;
        this.educationRequirement = educationRequirement != null ? educationRequirement : EducationLevel.BACHELORS;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Set<String> getRequiredSkills() {
        if (requiredSkillsText == null || requiredSkillsText.isBlank()) {
            return new HashSet<>();
        }
        return Arrays.stream(requiredSkillsText.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }

    public void setRequiredSkills(Set<String> skills) {
        if (skills == null || skills.isEmpty()) {
            this.requiredSkillsText = "";
        } else {
            this.requiredSkillsText = String.join(",", skills);
        }
    }

    public Set<String> getPreferredSkills() {
        if (preferredSkillsText == null || preferredSkillsText.isBlank()) {
            return new HashSet<>();
        }
        return Arrays.stream(preferredSkillsText.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }

    public void setPreferredSkills(Set<String> skills) {
        if (skills == null || skills.isEmpty()) {
            this.preferredSkillsText = "";
        } else {
            this.preferredSkillsText = String.join(",", skills);
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getRequiredSkillsText() {
        return requiredSkillsText;
    }

    public void setRequiredSkillsText(String requiredSkillsText) {
        this.requiredSkillsText = requiredSkillsText;
    }

    public String getPreferredSkillsText() {
        return preferredSkillsText;
    }

    public void setPreferredSkillsText(String preferredSkillsText) {
        this.preferredSkillsText = preferredSkillsText;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
