package com.coldwind.easyoj.mapper;

import org.springframework.data.jpa.repository.JpaRepository;
import com.coldwind.easyoj.domain.InterviewerConfig;

public interface InterviewerConfigDao extends JpaRepository<InterviewerConfig, Long> {
}