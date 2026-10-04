package com.smartresume.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Resume entity representing parsed candidate information and document metadata.
 */
@Entity
@Table(name = "resumes", indexes = {
    @Index(name = "idx_resume_email", columnList = "email")
})
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "candidate_name", length = 150)
    private String candidateName;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(length = 50)
    private String phone;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "file_type", nullable = false, length = 100)
    private String fileType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Lob
    @Column(name = "raw_text", columnDefinition = "CLOB")
    private String rawText;

    @Lob
    @Column(name = "parsed_skills", columnDefinition = "CLOB")
    private String parsedSkillsText;

    @Column(name = "experience_years")
    private Double experienceYears = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(name = "education_level", length = 30)
    private EducationLevel educationLevel = EducationLevel.BACHELORS;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "file_data")
    private byte[] fileData;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Resume() {
    }

    public Resume(String candidateName, String email, String phone, String fileName,
                  String fileType, Long fileSize, String rawText, Set<String> parsedSkills,
                  Double experienceYears, EducationLevel educationLevel, byte[] fileData) {
        this.candidateName = candidateName;
        this.email = email;
        this.phone = phone;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.rawText = rawText;
        setParsedSkills(parsedSkills);
        this.experienceYears = experienceYears != null ? experienceYears : 0.0;
        this.educationLevel = educationLevel != null ? educationLevel : EducationLevel.BACHELORS;
        this.fileData = fileData;
        this.createdAt = LocalDateTime.now();
    }

    public Set<String> getParsedSkills() {
        if (parsedSkillsText == null || parsedSkillsText.isBlank()) {
            return new HashSet<>();
        }
        return Arrays.stream(parsedSkillsText.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }

    public void setParsedSkills(Set<String> skills) {
        if (skills == null || skills.isEmpty()) {
            this.parsedSkillsText = "";
        } else {
            this.parsedSkillsText = String.join(",", skills);
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public String getParsedSkillsText() {
        return parsedSkillsText;
    }

    public void setParsedSkillsText(String parsedSkillsText) {
        this.parsedSkillsText = parsedSkillsText;
    }

    public Double getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(Double experienceYears) {
        this.experienceYears = experienceYears;
    }

    public EducationLevel getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(EducationLevel educationLevel) {
        this.educationLevel = educationLevel;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
