package com.smartresume;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.smartresume.config.ScoringProperties;

/**
 * Smart Resume Screening System Application.
 * Entry point for the Spring Boot application.
 */
@SpringBootApplication
@EnableConfigurationProperties(ScoringProperties.class)
public class SmartResumeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartResumeApplication.class, args);
    }
}
