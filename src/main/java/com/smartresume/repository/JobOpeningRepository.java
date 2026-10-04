package com.smartresume.repository;

import com.smartresume.model.JobOpening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobOpeningRepository extends JpaRepository<JobOpening, Long> {
    List<JobOpening> findByActiveTrueOrderByCreatedAtDesc();

    @Query("SELECT j FROM JobOpening j WHERE LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(j.department) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(j.location) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "ORDER BY j.createdAt DESC")
    List<JobOpening> searchJobs(@Param("keyword") String keyword);
}
