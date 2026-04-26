package com.aiinterview.model.entity.student;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;

/**
 * 学生档案实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("student_profile")
public class StudentProfile {

    @TableId(value = "student_id", type = IdType.AUTO)
    private Long studentId;

    @TableField("user_id")
    private Long userId;

    @TableField("student_no")
    private String studentNo;

    @TableField("real_name")
    private String realName;

    @TableField("gender")
    private String gender; // 'male', 'female', 'other'

    @TableField("birth_date")
    private LocalDate birthDate;

    @TableField("college")
    private String college;

    @TableField("major")
    private String major;

    @TableField("class_name")
    private String className;

    @TableField("grade")
    private String grade;

    @TableField("education_level")
    private String educationLevel; // '专科', '本科', '硕士', '博士'

    @TableField("graduation_year")
    private Year graduationYear;

    @TableField("profile_completion")
    private Integer profileCompletion; // 0-100

    @TableField("research_experience")
    private String researchExperience; // JSON格式存储科研经历

    @TableField("honors_awards")
    private String honorsAwards; // JSON格式存储荣誉获奖

    @TableField("internship_experience")
    private String internshipExperience; // JSON格式存储实习经历

    @TableField("resume_completion_status")
    private Integer resumeCompletionStatus; // 0-未完善 1-已完善

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
