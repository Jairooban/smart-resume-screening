package com.smartresume.strategy;

import com.smartresume.model.JobOpening;
import com.smartresume.model.Resume;
import com.smartresume.model.ScreeningResult;

/**
 * Strategy pattern interface for candidate resume scoring algorithms.
 */
public interface ScoringStrategy {

    /**
     * Compute a comprehensive screening result evaluating the resume against the job opening.
     *
     * @param jobOpening The job requirements and description
     * @param resume The candidate's parsed resume
     * @return Fully populated ScreeningResult entity
     */
    ScreeningResult evaluate(JobOpening jobOpening, Resume resume);
}
