package org.iflyproject.springdemos.mapper;

import org.springframework.data.jpa.repository.JpaRepository;
import org.iflyproject.springdemos.domain.InterviewerConfig;

public interface InterviewerConfigDao extends JpaRepository<InterviewerConfig, Long> {
}