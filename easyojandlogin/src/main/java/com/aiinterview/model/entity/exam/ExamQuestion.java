package com.aiinterview.model.entity.exam;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("exam_question")
public class ExamQuestion {
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
    private String knowledgePoint;
    private String scenario;
    private String difficulty;
    private String trapDetection;
    private Date createdAt;
}
