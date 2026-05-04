package com.aiinterview.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 薪资预测结果
 * 对应数据库表 salary_prediction_result
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryPredictionResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer predId;                // 预测ID
    private Integer userId;                // 用户ID
    private BigDecimal predictedSalary;    // 预测薪资
    private BigDecimal salaryRangeMin;     // 薪资范围最小值
    private BigDecimal salaryRangeMax;     // 薪资范围最大值
    private BigDecimal confidenceScore;    // 置信度分数
    private LocalDateTime createdAt;       // 创建时间
}
