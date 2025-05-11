package org.iflyproject.springdemos.mapper;

import org.springframework.data.jpa.repository.JpaRepository;
import org.iflyproject.springdemos.domain.QuestionConfig;

public interface QuestionConfigDao extends JpaRepository<QuestionConfig, Long> {
}