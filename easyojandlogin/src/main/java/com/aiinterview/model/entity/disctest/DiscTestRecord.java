package com.aiinterview.model.entity.disctest;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * DISC测试记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("disc_test_records")
public class DiscTestRecord {
    
    @TableId(value = "record_id", type = IdType.AUTO)
    private Long recordId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 测试会话ID
     */
    private String testSession;
    
    /**
     * 测试状态：in_progress, completed, abandoned
     */
    private String testStatus;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 完成时间
     */
    private LocalDateTime completeTime;
    
    /**
     * 总题数
     */
    private Integer totalQuestions;
    
    /**
     * 已答题数
     */
    private Integer answeredQuestions;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
