package com.coldwind.easyoj.model.dto.response;

import lombok.Data;

/**
 * 笔试题目详情DTO
 */
@Data
public class ExamQuestionDetailDTO {
    /**
     * 题目ID
     */
    private String questionId;
    
    /**
     * 题目类型（choice/true_false）
     */
    private String type;
    
    /**
     * 题目内容
     */
    private String content;
    
    /**
     * 选项A
     */
    private String optionA;
    
    /**
     * 选项B
     */
    private String optionB;
    
    /**
     * 选项C
     */
    private String optionC;
    
    /**
     * 选项D
     */
    private String optionD;
    
    /**
     * 正确答案
     */
    private String correctAnswer;
    
    /**
     * 用户答案
     */
    private String userAnswer;
    
    /**
     * 是否答对
     */
    private Boolean isCorrect;
    
    /**
     * 题目解析
     */
    private String explanation;
    
    /**
     * 错误原因（如果答错）
     */
    private String errorReason;
} 