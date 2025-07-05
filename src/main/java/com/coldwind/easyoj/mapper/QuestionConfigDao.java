package com.coldwind.easyoj.mapper;

import org.springframework.data.jpa.repository.JpaRepository;
import com.coldwind.easyoj.domain.QuestionConfig;

public interface QuestionConfigDao extends JpaRepository<QuestionConfig, Long> {
}