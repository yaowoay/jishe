package com.aiinterview.model.dto.exam;

import lombok.Data;

@Data
public class ExamGenerateRequest {
    private String jobPosition;
    private String skills;
    private String experience;
    private Integer questionCount;
    private String difficultyLevel;
    private String focusArea;
}
