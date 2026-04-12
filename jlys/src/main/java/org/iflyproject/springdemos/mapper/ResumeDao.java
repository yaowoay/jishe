package org.iflyproject.springdemos.mapper;

import org.springframework.data.jpa.repository.JpaRepository;
import org.iflyproject.springdemos.domain.Resume;

public interface ResumeDao extends JpaRepository<Resume, Long> {
}