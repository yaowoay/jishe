package org.iflyproject.springdemos.mapper;

import org.springframework.data.jpa.repository.JpaRepository;
import org.iflyproject.springdemos.domain.InterviewMode;

public interface InterviewModeDao extends JpaRepository<InterviewMode, Long> {
}