package com.aiinterview.flink.recommend.entity;

import lombok.Data;
import java.sql.Timestamp;

/**
 * 用户完整画像：静态基础+实时偏好
 * 静态字段来自 student_profile + resume，外加实时偏好扩展字段
 */
@Data
public class UserFullProfile {
    // ========== 静态基础画像（数据库字段） ==========
    private Integer userId;           // 用户ID（主键）
    private String position;          // 期望职位
    private Byte workYears;           // 工作年限
    private String expectedCity;      // 期望城市
    private Integer expectedSalaryMin;// 期望薪资下限
    private Integer expectedSalaryMax;// 期望薪资上限
    private String expectedIndustry;  // 期望行业
    private String major;             // 专业
    private String educationLevel;    // 学历
    private String skills;            // 技能（逗号分隔）


    // ========== 实时偏好画像（扩展字段，不持久化到数据库） ==========
    private String realTimeJobTypes;   // 实时偏好职位类型（逗号分隔）
    private String realTimeCities;     // 实时偏好城市（逗号分隔）
    private Integer realTimeSalaryMin; // 实时偏好薪资下限
    private Integer realTimeSalaryMax; // 实时偏好薪资上限
}