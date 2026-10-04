package com.smartresume.repository;

import com.smartresume.model.CandidateStatus;
import com.smartresume.model.ScreeningResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScreeningResultRepository extends JpaRepository<ScreeningResult, Long> {

    List<ScreeningResult> findByJobOpeningIdOrderByTotalScoreDesc(Long jobOpeningId);

    Optional<ScreeningResult> findByJobOpeningIdAndResumeId(Long jobOpeningId, Long resumeId);

    List<ScreeningResult> findByJobOpeningIdAndStatusOrderByTotalScoreDesc(Long jobOpeningId, CandidateStatus status);

    List<ScreeningResult> findByResumeId(Long resumeId);

    long countByJobOpeningId(Long jobOpeningId);

    @Query("SELECT AVG(sr.totalScore) FROM ScreeningResult sr")
    Double getAverageMatchScore();

    @Query("SELECT AVG(sr.totalScore) FROM ScreeningResult sr WHERE sr.jobOpening.id = :jobId")
    Double getAverageMatchScoreByJob(@Param("jobId") Long jobId);

    @Query("SELECT sr FROM ScreeningResult sr ORDER BY sr.totalScore DESC")
    List<ScreeningResult> findTopCandidates();

    void deleteByJobOpeningId(Long jobOpeningId);

    void deleteByResumeId(Long resumeId);
}
