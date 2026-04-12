package com.aiinterview.model.entity.disctest;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * DISC测试结果实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("disc_test_results")
public class DiscTestResult {
    
    @TableId(value = "result_id", type = IdType.AUTO)
    private Long resultId;
    
    /**
     * 测试记录ID
     */
    private Long recordId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * D型得分
     */
    private Integer dScore;
    
    /**
     * I型得分
     */
    private Integer iScore;
    
    /**
     * S型得分
     */
    private Integer sScore;
    
    /**
     * C型得分
     */
    private Integer cScore;
    
    /**
     * 主要类型
     */
    private String primaryType;
    
    /**
     * 次要类型
     */
    private String secondaryType;
    
    /**
     * 性格档案(如DI, SC等)
     */
    private String personalityProfile;
    
    /**
     * 详细分析结果
     */
    private String detailedAnalysis;
    
    /**
     * 优势特点
     */
    private String strengths;
    
    /**
     * 改进建议
     */
    private String weaknesses;
    
    /**
     * 职业建议
     */
    private String careerSuggestions;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
