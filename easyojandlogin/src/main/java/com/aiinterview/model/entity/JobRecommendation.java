package com.aiinterview.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 职位推荐
 * 对应数据库表 job_recommendation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobRecommendation implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long recId;                    // 推荐ID
    private Long userId;                   // 用户ID
    private Integer jobId;                 // 职位ID
    private BigDecimal matchScore;         // 匹配度分数
    private String reason;                 // 推荐原因
    private LocalDateTime createdAt;       // 创建时间
}
