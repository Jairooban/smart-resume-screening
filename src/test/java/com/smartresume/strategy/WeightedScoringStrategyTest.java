package com.smartresume.strategy;

import com.smartresume.config.ScoringProperties;
import com.smartresume.model.*;
import com.smartresume.nlp.SkillExtractor;
import com.smartresume.nlp.TextTokenizer;
import com.smartresume.nlp.TfIdfCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class WeightedScoringStrategyTest {

    private WeightedScoringStrategy scoringStrategy;

    @BeforeEach
    void setUp() {
        ScoringProperties properties = new ScoringProperties();
        SkillExtractor skillExtractor = new SkillExtractor();
        skillExtractor.init();
        TfIdfCalculator tfIdfCalculator = new TfIdfCalculator(new TextTokenizer());
        ExplanationGenerator explanationGenerator = new ExplanationGenerator();

        scoringStrategy = new WeightedScoringStrategy(
                properties,
                tfIdfCalculator,
                skillExtractor,
                explanationGenerator
        );
    }

    @Test
    void testHighFitCandidateScoring() {
        JobOpening job = new JobOpening(
                "Senior Java Developer",
                "Engineering",
                "San Francisco",
                "Looking for a senior Java developer with Spring Boot, PostgreSQL, and Docker experience.",
                new HashSet<>(Arrays.asList("Java", "Spring Boot", "PostgreSQL", "Docker")),
                new HashSet<>(Arrays.asList("Kubernetes", "AWS")),
                3.0,
                EducationLevel.BACHELORS
        );

        Resume resume = new Resume(
                "Alex",
                "alex@tech.com",
                "555-0199",
                "resume.pdf",
                "application/pdf",
                1024L,
                "Senior Java developer with 5 years experience with Spring Boot, PostgreSQL, Docker, AWS, and Kubernetes.",
                new HashSet<>(Arrays.asList("Java", "Spring Boot", "PostgreSQL", "Docker", "AWS", "Kubernetes")),
                5.0,
                EducationLevel.MASTERS,
                new byte[0]
        );

        ScreeningResult result = scoringStrategy.evaluate(job, resume);

        assertNotNull(result);
        assertEquals(100.0, result.getRequiredSkillsScore(), 0.1);
        assertEquals(100.0, result.getPreferredSkillsScore(), 0.1);
        assertEquals(100.0, result.getExperienceScore(), 0.1);
        assertEquals(100.0, result.getEducationScore(), 0.1);
        assertTrue(result.getTotalScore() >= 85.0);
        assertEquals(CandidateStatus.SHORTLISTED, result.getStatus());
        assertNotNull(result.getScoreExplanation());
    }

    @Test
    void testLowFitCandidateScoring() {
        JobOpening job = new JobOpening(
                "Senior Java Developer",
                "Engineering",
                "San Francisco",
                "Looking for a senior Java developer with Spring Boot and PostgreSQL.",
                new HashSet<>(Arrays.asList("Java", "Spring Boot", "PostgreSQL")),
                new HashSet<>(Arrays.asList("Kubernetes")),
                5.0,
                EducationLevel.MASTERS
        );

        Resume resume = new Resume(
                "Chef",
                "chef@food.com",
                "555-0199",
                "resume.pdf",
                "application/pdf",
                1024L,
                "Pastry chef with experience in French pastries and baking sourdough bread.",
                new HashSet<>(),
                1.0,
                EducationLevel.HIGH_SCHOOL,
                new byte[0]
        );

        ScreeningResult result = scoringStrategy.evaluate(job, resume);

        assertNotNull(result);
        assertEquals(0.0, result.getRequiredSkillsScore(), 0.1);
        assertTrue(result.getTotalScore() < 40.0);
        assertEquals(CandidateStatus.REJECTED, result.getStatus());
    }
}
