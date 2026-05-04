package com.aiinterview.model.entity.cooperation;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 合作案例实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("cooperation_cases")
public class CooperationCase {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("case_title")
    private String caseTitle;

    @TableField("company_name")
    private String companyName;

    @TableField("cooperation_type")
    private String cooperationType; // 'training_base', 'order_class', 'internship', 'research'

    @TableField("cooperation_duration")
    private String cooperationDuration; // 如：2020-2023

    @TableField("student_count")
    private Integer studentCount;

    @TableField("case_summary")
    private String caseSummary;

    @TableField("achievements")
    private String achievements; // JSON格式存储成果列表

    @TableField("success_factors")
    private String successFactors;

    @TableField("images")
    private String images; // JSON格式存储图片列表

    @TableField("documents")
    private String documents; // JSON格式存储文档列表

    @TableField("is_featured")
    private Boolean isFeatured; // 是否精选案例

    @TableField("view_count")
    private Integer viewCount;

    @TableField("status")
    private String status; // 'draft', 'published', 'archived'

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
