package com.smartresume.service;

import com.smartresume.exception.DuplicateResourceException;
import com.smartresume.exception.FileProcessingException;
import com.smartresume.model.EducationLevel;
import com.smartresume.model.Resume;
import com.smartresume.nlp.RegexExtractor;
import com.smartresume.nlp.SkillExtractor;
import com.smartresume.repository.ResumeRepository;
import com.smartresume.util.FileValidator;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

/**
 * Service responsible for document text extraction using Apache Tika,
 * candidate information parsing (NLP + Regex), duplicate check, and persistence.
 */
@Service
public class ResumeParserService {

    private static final Logger log = LoggerFactory.getLogger(ResumeParserService.class);

    private final ResumeRepository resumeRepository;
    private final FileValidator fileValidator;
    private final SkillExtractor skillExtractor;
    private final RegexExtractor regexExtractor;
    private final Tika tika = new Tika();

    public ResumeParserService(ResumeRepository resumeRepository,
                               FileValidator fileValidator,
                               SkillExtractor skillExtractor,
                               RegexExtractor regexExtractor) {
        this.resumeRepository = resumeRepository;
        this.fileValidator = fileValidator;
        this.skillExtractor = skillExtractor;
        this.regexExtractor = regexExtractor;
    }

    /**
     * Parse and save a single uploaded resume file.
     */
    @Transactional
    public Resume parseAndSaveResume(MultipartFile file, boolean allowDuplicateUpdate) {
        fileValidator.validate(file);

        String rawText;
        try (InputStream is = file.getInputStream()) {
            rawText = tika.parseToString(is);
        } catch (Exception e) {
            log.error("Failed to parse file: {}", file.getOriginalFilename(), e);
            throw new FileProcessingException("Could not extract text from '" + file.getOriginalFilename() + "': " + e.getMessage(), e);
        }

        if (rawText == null || rawText.trim().isEmpty()) {
            throw new FileProcessingException("No readable text found in file '" + file.getOriginalFilename() + "'.");
        }

        // Extract metadata using NLP and Regex components
        Optional<String> emailOpt = regexExtractor.extractEmail(rawText);
        String email = emailOpt.orElseGet(() -> {
            String fakeEmail = "candidate_" + UUID.randomUUID().toString().substring(0, 8) + "@applicant.com";
            log.warn("No email detected in '{}', assigned placeholder '{}'", file.getOriginalFilename(), fakeEmail);
            return fakeEmail;
        });

        // Duplicate check
        Optional<Resume> existingOpt = resumeRepository.findByEmail(email);
        if (existingOpt.isPresent() && !allowDuplicateUpdate) {
            throw new DuplicateResourceException("A resume with email '" + email + "' already exists in the system (Candidate: " + existingOpt.get().getCandidateName() + ").");
        }

        String phone = regexExtractor.extractPhone(rawText).orElse("Not Provided");
        double experienceYears = regexExtractor.extractExperienceYears(rawText);
        EducationLevel educationLevel = regexExtractor.extractEducationLevel(rawText);
        String candidateName = regexExtractor.extractCandidateName(rawText, file.getOriginalFilename());
        Set<String> skills = skillExtractor.extractSkills(rawText);

        byte[] fileBytes;
        try {
            fileBytes = file.getBytes();
        } catch (Exception e) {
            fileBytes = new byte[0];
        }

        Resume resume;
        if (existingOpt.isPresent()) {
            resume = existingOpt.get();
            resume.setCandidateName(candidateName);
            resume.setPhone(phone);
            resume.setFileName(file.getOriginalFilename());
            resume.setFileType(file.getContentType() != null ? file.getContentType() : "application/octet-stream");
            resume.setFileSize(file.getSize());
            resume.setRawText(rawText);
            resume.setParsedSkills(skills);
            resume.setExperienceYears(experienceYears);
            resume.setEducationLevel(educationLevel);
            resume.setFileData(fileBytes);
        } else {
            resume = new Resume(
                    candidateName,
                    email,
                    phone,
                    file.getOriginalFilename(),
                    file.getContentType() != null ? file.getContentType() : "application/octet-stream",
                    file.getSize(),
                    rawText,
                    skills,
                    experienceYears,
                    educationLevel,
                    fileBytes
            );
        }

        Resume saved = resumeRepository.save(resume);
        log.info("Successfully parsed and saved resume for candidate '{}' ({}) with {} skills extracted.",
                saved.getCandidateName(), saved.getEmail(), saved.getParsedSkills().size());
        return saved;
    }

    /**
     * Parse raw text directly (useful for testing or seeding).
     */
    @Transactional
    public Resume parseAndSaveRawText(String fileName, String rawText, byte[] mockBytes) {
        Optional<String> emailOpt = regexExtractor.extractEmail(rawText);
        String email = emailOpt.orElse("candidate_" + UUID.randomUUID().toString().substring(0, 8) + "@applicant.com");

        Optional<Resume> existing = resumeRepository.findByEmail(email);
        if (existing.isPresent()) {
            return existing.get();
        }

        String phone = regexExtractor.extractPhone(rawText).orElse("+1 (555) 019-2834");
        double experienceYears = regexExtractor.extractExperienceYears(rawText);
        EducationLevel educationLevel = regexExtractor.extractEducationLevel(rawText);
        String candidateName = regexExtractor.extractCandidateName(rawText, fileName);
        Set<String> skills = skillExtractor.extractSkills(rawText);

        Resume resume = new Resume(
                candidateName,
                email,
                phone,
                fileName,
                fileName.endsWith(".pdf") ? "application/pdf" : "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                (long) rawText.length(),
                rawText,
                skills,
                experienceYears,
                educationLevel,
                mockBytes != null ? mockBytes : rawText.getBytes()
        );

        return resumeRepository.save(resume);
    }
}
