package com.coldwind.easyoj.mapper;

import org.springframework.data.jpa.repository.JpaRepository;
import com.coldwind.easyoj.domain.InterviewMode;

public interface InterviewModeDao extends JpaRepository<InterviewMode, Long> {
}