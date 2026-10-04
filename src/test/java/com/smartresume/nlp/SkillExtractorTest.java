package com.smartresume.nlp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SkillExtractorTest {

    private SkillExtractor skillExtractor;

    @BeforeEach
    void setUp() {
        skillExtractor = new SkillExtractor();
        skillExtractor.init();
    }

    @Test
    void testExtractStandardSkills() {
        String resumeText = "Experienced engineer with Java, Python, Spring Boot, and PostgreSQL database knowledge.";
        Set<String> skills = skillExtractor.extractSkills(resumeText);

        assertTrue(skills.contains("Java"));
        assertTrue(skills.contains("Python"));
        assertTrue(skills.contains("Spring Boot"));
        assertTrue(skills.contains("PostgreSQL"));
    }

    @Test
    void testExtractSynonymsAndSymbols() {
        String resumeText = "Built microservices with k8s, docker, and modern c++ on AWS. Also used js and tf.";
        Set<String> skills = skillExtractor.extractSkills(resumeText);

        assertTrue(skills.contains("Kubernetes"), "k8s should match Kubernetes");
        assertTrue(skills.contains("Docker"));
        assertTrue(skills.contains("C++"), "c++ should match C++");
        assertTrue(skills.contains("AWS"));
        assertTrue(skills.contains("JavaScript"), "js should match JavaScript");
        assertTrue(skills.contains("TensorFlow"), "tf should match TensorFlow");
    }

    @Test
    void testCanonicalization() {
        assertEquals("JavaScript", skillExtractor.getCanonicalSkillName("js"));
        assertEquals("Kubernetes", skillExtractor.getCanonicalSkillName("k8s"));
        assertEquals("PostgreSQL", skillExtractor.getCanonicalSkillName("postgres"));
    }
}
