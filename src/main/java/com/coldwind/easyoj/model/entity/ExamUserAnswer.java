package com.coldwind.easyoj.model.entity;

import lombok.Data;
import java.util.Date;

@Data
public class ExamUserAnswer {
    private Long id;
    private String taskId;
    private String questionId;
    private String userAnswer;
    private Date createdAt;
}