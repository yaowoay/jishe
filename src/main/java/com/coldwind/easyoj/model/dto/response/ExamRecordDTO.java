package com.coldwind.easyoj.model.dto.response;

import lombok.Data;
import java.util.Date;
import java.util.List;

/**
 * 笔试记录DTO
 */
@Data
public class ExamRecordDTO {
    /**
     * 考试任务ID
     */
    private String taskId;
    
    /**
     * 考试时间
     */
    private Date examTime;
    
    /**
     * 总题目数
     */
    private Integer totalQuestions;
    
    /**
     * 正确题目数
     */
    private Integer correctCount;
    
    /**
     * 得分（百分制）
     */
    private Double score;
    
    /**
     * 题目详情列表
     */
    private List<ExamQuestionDetailDTO> questions;
} 