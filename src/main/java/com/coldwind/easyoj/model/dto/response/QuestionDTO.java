package com.coldwind.easyoj.model.dto.response;

//package org.iflyproject.springdemos.dto.response;

import lombok.Data;
import java.util.Map;

@Data
public class QuestionDTO {
    private String questionId;
    private String type; // "choice" 或 "true_false"
    private String questionContent;

    // 选择题字段
    private Map<String, String> options;
    private String answer;

    // 判断题字段
    private Boolean answerBool;
    private String explanation;

    // 共用字段
    private String difficulty;
    private String knowledgePoint;//知识点
    private String scenario;//场景说明
    private String trapDetection;//陷阱
}