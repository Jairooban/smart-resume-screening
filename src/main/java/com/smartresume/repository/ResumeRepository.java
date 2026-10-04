package com.smartresume.repository;

import com.smartresume.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    Optional<Resume> findByEmail(String email);
    boolean existsByEmail(String email);
}
