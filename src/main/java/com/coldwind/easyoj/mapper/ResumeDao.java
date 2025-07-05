package com.coldwind.easyoj.mapper;

import org.springframework.data.jpa.repository.JpaRepository;
import com.coldwind.easyoj.domain.Resume;

public interface ResumeDao extends JpaRepository<Resume, Long> {
}