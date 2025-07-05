package com.coldwind.easyoj.model.entity;

import lombok.Data;
import java.util.Date;

@Data
public class ExamQuestionDetail {
    private Long id;
    private String taskId;
    private String questionId;
    private String type;
    private String content;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String answer;
    private String explanation;
    private Date createdAt;
}