package com.aiinterview.model.entity.teacher;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 就业台账实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("employment_ledger")
public class EmploymentLedger {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("student_id")
    private Long studentId;

    @TableField("college_id")
    private Long collegeId;

    @TableField("major_id")
    private Long majorId;

    @TableField("employment_status")
    private String employmentStatus;

    @TableField("company_name")
    private String companyName;

    @TableField("position")
    private String position;

    @TableField("salary_range")
    private String salaryRange;

    @TableField("employment_city")
    private String employmentCity;

    @TableField("further_study_school")
    private String furtherStudySchool;

    @TableField("verify_status")
    private String verifyStatus;

    @TableField("verified_by")
    private Long verifiedBy;

    @TableField("verified_at")
    private LocalDateTime verifiedAt;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
