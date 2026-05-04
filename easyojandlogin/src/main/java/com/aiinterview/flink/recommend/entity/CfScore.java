package com.aiinterview.flink.recommend.entity;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 协同过滤分数实体：对应ClickHouse的student_job_cf_score表
 */
@Data
public class CfScore implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long studentId; // 学生ID
    private Long jobId; // 岗位ID
    private Double cfScore; // 协同过滤分数（%）
    private Date createDate; // 训练日期
}