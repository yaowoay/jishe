package com.aiinterview.model.entity.teacher;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学生档案实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("student_profile")
public class StudentProfile {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("student_no")
    private String studentNo;

    @TableField("real_name")
    private String realName;

    @TableField("college_id")
    private Long collegeId;

    @TableField("major_id")
    private Long majorId;

    @TableField("class_name")
    private String className;

    @TableField("education_level")
    private String educationLevel;

    @TableField("graduation_year")
    private Integer graduationYear;

    @TableField("gpa")
    private BigDecimal gpa;

    @TableField("skills")
    private String skills;

    @TableField("expected_city")
    private String expectedCity;

    @TableField("expected_salary_min")
    private Integer expectedSalaryMin;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
