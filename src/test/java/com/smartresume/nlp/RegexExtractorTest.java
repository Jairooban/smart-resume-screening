package com.smartresume.nlp;

import com.smartresume.model.EducationLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RegexExtractorTest {

    private RegexExtractor regexExtractor;

    @BeforeEach
    void setUp() {
        regexExtractor = new RegexExtractor();
    }

    @Test
    void testExtractEmail() {
        String text = "Contact me at alex.morgan_123@gmail.com or 555-123-4567";
        Optional<String> email = regexExtractor.extractEmail(text);
        assertTrue(email.isPresent());
        assertEquals("alex.morgan_123@gmail.com", email.get());
    }

    @Test
    void testExtractPhone() {
        String text = "Alex Morgan | Phone: +1 (555) 234-5678 | San Francisco, CA";
        Optional<String> phone = regexExtractor.extractPhone(text);
        assertTrue(phone.isPresent());
        assertTrue(phone.get().contains("555"));
    }

    @Test
    void testExtractExperienceYears() {
        String text1 = "Senior Engineer with 6.5+ years of experience in distributed systems";
        assertEquals(6.5, regexExtractor.extractExperienceYears(text1), 0.1);

        String text2 = "Software developer with 4 years experience in Java";
        assertEquals(4.0, regexExtractor.extractExperienceYears(text2), 0.1);
    }

    @Test
    void testExtractEducationLevel() {
        String text1 = "Master of Science in Computer Science (M.S.)";
        assertEquals(EducationLevel.MASTERS, regexExtractor.extractEducationLevel(text1));

        String text2 = "PhD in Artificial Intelligence MIT";
        assertEquals(EducationLevel.PHD, regexExtractor.extractEducationLevel(text2));

        String text3 = "Bachelor of Technology in Computer Engineering";
        assertEquals(EducationLevel.BACHELORS, regexExtractor.extractEducationLevel(text3));
    }
}
