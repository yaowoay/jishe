package com.aiinterview.model.entity.cooperation;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 订单班实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("order_classes")
public class OrderClass {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("project_id")
    private Long projectId;

    @TableField("class_name")
    private String className;

    @TableField("company_id")
    private Long companyId;

    @TableField("major")
    private String major;

    @TableField("grade")
    private String grade;

    @TableField("planned_count")
    private Integer plannedCount;

    @TableField("enrolled_count")
    private Integer enrolledCount;

    @TableField("training_plan")
    private String trainingPlan; // JSON格式存储培养方案

    @TableField("curriculum")
    private String curriculum; // JSON格式存储课程体系

    @TableField("employment_rate")
    private java.math.BigDecimal employmentRate;

    @TableField("start_date")
    private LocalDate startDate;

    @TableField("graduation_date")
    private LocalDate graduationDate;

    @TableField("class_status")
    private String classStatus; // 'recruiting', 'training', 'graduated'

    @TableField("instructor")
    private String instructor;

    @TableField("company_mentor")
    private String companyMentor;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
