package com.aiinterview.model.entity.teacher;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 教师实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("teachers")
public class Teacher {
    
    @TableId(value = "teacher_id", type = IdType.AUTO)
    private Long teacherId;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("teacher_no")
    private String teacherNo;
    
    @TableField("real_name")
    private String realName;
    
    @TableField("college_id")
    private Long collegeId;
    
    @TableField("role_type")
    private String roleType; // 'counselor', 'advisor', 'admin', 'leader'
    
    @TableField("phone")
    private String phone;
    
    @TableField("email")
    private String email;
    
    @TableField("managed_colleges")
    private String managedColleges; // JSON格式存储管理的学院ID列表
    
    @TableField("managed_majors")
    private String managedMajors; // JSON格式存储管理的专业ID列表
    
    @TableField("profile_completion")
    private Integer profileCompletion; // 0-100
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}