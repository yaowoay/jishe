package com.aiinterview.model.entity.teacher;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 校企合作申请实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("cooperation_applications")
public class CooperationApplication {

    @TableId(value = "application_id", type = IdType.AUTO)
    private Long applicationId;

    @TableField("enterprise_id")
    private Long enterpriseId;

    @TableField("college_id")
    private Long collegeId;

    @TableField("cooperation_type")
    private String cooperationType;

    @TableField("title")
    private String title;

    @TableField("description")
    private String description;

    @TableField("proposal_url")
    private String proposalUrl;

    @TableField("expected_start_date")
    private LocalDate expectedStartDate;

    @TableField("expected_students")
    private Integer expectedStudents;

    @TableField("status")
    private String status;

    @TableField("review_comment")
    private String reviewComment;

    @TableField("reviewed_by")
    private Long reviewedBy;

    @TableField("signed_at")
    private LocalDateTime signedAt;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
