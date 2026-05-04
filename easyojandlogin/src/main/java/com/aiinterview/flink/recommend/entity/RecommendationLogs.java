package com.aiinterview.flink.recommend.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 推荐日志实体：对应recommendation_logs表，存储推荐结果
 */
@Data
@TableName("recommendation_logs")
public class RecommendationLogs {
    @TableId(type = IdType.AUTO) // 自增ID
    private Long id;
    private Long userId; // 学生ID（关联student.studentId）
    private Long jobId; // 岗位ID（关联job.jobId）
    private String algorithmType; // 算法类型：hybrid（混合算法）
    private Double score; // 最终推荐得分（%）
    private Integer isClicked; // 是否点击：0-未点击，1-已点击
    private Integer isApplied; // 是否投递：0-未投递，1-已投递
    @TableField(value = "created_at")
    private Date createTime;
}