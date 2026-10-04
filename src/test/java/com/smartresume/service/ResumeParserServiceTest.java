package com.smartresume.service;

import com.smartresume.model.EducationLevel;
import com.smartresume.model.Resume;
import com.smartresume.nlp.RegexExtractor;
import com.smartresume.nlp.SkillExtractor;
import com.smartresume.repository.ResumeRepository;
import com.smartresume.util.FileValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResumeParserServiceTest {

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private FileValidator fileValidator;

    @Mock
    private SkillExtractor skillExtractor;

    @Mock
    private RegexExtractor regexExtractor;

    @InjectMocks
    private ResumeParserService resumeParserService;

    @Test
    void testParseAndSaveRawText() {
        String rawText = "John Doe | john@example.com | 555-0100 | 4 years experience in Java and Spring Boot";
        when(regexExtractor.extractEmail(rawText)).thenReturn(Optional.of("john@example.com"));
        when(resumeRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(regexExtractor.extractPhone(rawText)).thenReturn(Optional.of("555-0100"));
        when(regexExtractor.extractExperienceYears(rawText)).thenReturn(4.0);
        when(regexExtractor.extractEducationLevel(rawText)).thenReturn(EducationLevel.BACHELORS);
        when(regexExtractor.extractCandidateName(rawText, "resume.pdf")).thenReturn("John Doe");

        Set<String> skills = new HashSet<>();
        skills.add("Java");
        skills.add("Spring Boot");
        when(skillExtractor.extractSkills(rawText)).thenReturn(skills);

        when(resumeRepository.save(any(Resume.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Resume result = resumeParserService.parseAndSaveRawText("resume.pdf", rawText, new byte[0]);

        assertNotNull(result);
        assertEquals("John Doe", result.getCandidateName());
        assertEquals("john@example.com", result.getEmail());
        assertEquals(4.0, result.getExperienceYears());
        assertTrue(result.getParsedSkills().contains("Java"));
        verify(resumeRepository, times(1)).save(any(Resume.class));
    }
}
