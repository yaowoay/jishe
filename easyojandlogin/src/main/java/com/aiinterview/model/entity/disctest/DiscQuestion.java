package com.aiinterview.model.entity.disctest;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * DISC测试题目实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("disc_questions")
public class DiscQuestion {
    
    @TableId(value = "question_id", type = IdType.AUTO)
    private Integer questionId;
    
    /**
     * 题目组号(1-40)
     */
    private Integer questionGroup;
    
    /**
     * 选项字母(A/B/C/D)
     */
    private String optionLetter;
    
    /**
     * 选项内容
     */
    private String optionText;
    
    /**
     * DISC类型(D/I/S/C)
     */
    private String discType;
    
    /**
     * 是否为正面题目
     */
    private Boolean isPositive;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
