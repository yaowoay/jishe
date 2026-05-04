package com.aiinterview.flink.recommend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 职位实体类，对应数据库jobs表
 */
@Data
@TableName("jobs")
public class Position {
    @TableId(value = "job_id", type = IdType.AUTO)
    private Long id;

    @TableField("title")
    private String name;

    @TableField("job_type")
    private String type;

    @TableField("location")
    private String city;

    @TableField("min_salary")
    private Integer salaryMin;

    @TableField("max_salary")
    private Integer salaryMax;

    @TableField("job_skills")
    private String skills;

    @TableField("education_requirement")
    private String education;

    @TableField("experience_requirement")
    private String workYears;

    @TableField(exist = false)
    private String industry;

    @TableField(exist = false)
    private String companyName;

    @TableField(exist = false)
    private String scale;

    // 对应 companies.financing_status，用于前端展示公司融资情况
    @TableField(exist = false)
    private String companyType;

    // 匹配度得分（非数据库字段，用于推荐排序）
    @TableField(exist = false)
    private Double matchScore;

    // 推荐依据（非数据库字段，用于前端展示）
    @TableField(exist = false)
    private String recommendReason;

    // 推荐标签（非数据库字段，用于前端展示）
    @TableField(exist = false)
    private String recommendTag;
}