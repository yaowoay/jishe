package com.aiinterview.model.entity.cooperation;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 实训基地实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("training_bases")
public class TrainingBase {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("project_id")
    private Long projectId;

    @TableField("base_name")
    private String baseName;

    @TableField("base_address")
    private String baseAddress;

    @TableField("base_area")
    private java.math.BigDecimal baseArea; // 平方米

    @TableField("equipment_value")
    private java.math.BigDecimal equipmentValue;

    @TableField("capacity")
    private Integer capacity; // 可容纳学生数

    @TableField("training_fields")
    private String trainingFields; // JSON格式存储培训领域

    @TableField("facilities")
    private String facilities; // JSON格式存储设施设备

    @TableField("construction_status")
    private String constructionStatus; // 'planning', 'constructing', 'completed', 'operating'

    @TableField("completion_date")
    private LocalDateTime completionDate;

    @TableField("annual_training_count")
    private Integer annualTrainingCount;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
