package com.aiinterview.model.entity.cooperation;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 合作项目实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("cooperation_projects")
public class CooperationProject {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("company_id")
    private Long companyId;

    @TableField("project_name")
    private String projectName;

    @TableField("project_type")
    private String projectType; // 'training_base', 'order_class', 'internship', 'research'

    @TableField("cooperation_mode")
    private String cooperationMode; // '共建实训基地', '订单班', '实习基地', '产学研合作'

    @TableField("project_desc")
    private String projectDesc;

    @TableField("target_major")
    private String targetMajor;

    @TableField("student_count")
    private Integer studentCount;

    @TableField("investment_amount")
    private java.math.BigDecimal investmentAmount;

    @TableField("start_date")
    private LocalDate startDate;

    @TableField("end_date")
    private LocalDate endDate;

    @TableField("contact_person")
    private String contactPerson;

    @TableField("contact_phone")
    private String contactPhone;

    @TableField("status")
    private String status; // 'draft', 'submitted', 'reviewing', 'approved', 'rejected', 'ongoing', 'completed'

    @TableField("submit_time")
    private LocalDateTime submitTime;

    @TableField("review_time")
    private LocalDateTime reviewTime;

    @TableField("reviewed_by")
    private Long reviewedBy;

    @TableField("review_comment")
    private String reviewComment;

    @TableField("attachments")
    private String attachments; // JSON格式存储附件列表

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
