package com.coldwind.easyoj.model.entity;

import lombok.Data;
import java.util.Date;

@Data
public class ExamJudgmentDetail {
    private Long id;
    private String taskId;
    private String questionId;
    private String userAnswer;
    private String correctAnswer;
    private Boolean isCorrect;
    private String explanation;
    private Date createdAt;
}