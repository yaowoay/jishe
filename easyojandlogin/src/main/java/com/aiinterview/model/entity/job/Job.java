package com.aiinterview.model.entity.job;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 职位实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jobs")
public class Job {
    
    @TableId(value = "job_id", type = IdType.AUTO)
    private Long jobId;
    
    @TableField("company_id")
    private Long companyId;
    
    @TableField("title")
    private String title;
    
    @TableField("job_type")
    private String jobType; // '全职', '兼职', '实习'
    
    @TableField("job_duty")
    private String description; // 岗位职责

    @TableField("job_requirements")
    private String requirements; // 岗位要求

    @TableField("experience_requirement")
    private String experience; // 经验要求

    @TableField("education_requirement")
    private String education; // 学历要求

    @TableField("job_skills")
    private String skills; // 职位技能，JSON格式

    @TableField("min_salary")
    private Integer minSalary;
    
    @TableField("max_salary")
    private Integer maxSalary;
    
    @TableField("location")
    private String location;
    
    @TableField("is_active")
    private Boolean isActive;

    @TableField("verify_status")
    private String verifyStatus;

    @TableField("verify_remark")
    private String verifyRemark;

    @TableField("post_date")
    private LocalDate postDate;
    
    @TableField("expiration_date")
    private LocalDate expirationDate;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
