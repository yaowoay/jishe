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
 * 帮扶记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("assistance_records")
public class AssistanceRecord {

    @TableId(value = "record_id", type = IdType.AUTO)
    private Long recordId;

    @TableField("student_id")
    private Long studentId;

    @TableField("teacher_id")
    private Long teacherId;

    @TableField("difficulty_type")
    private String difficultyType;

    @TableField("difficulty_level")
    private String difficultyLevel;

    @TableField("description")
    private String description;

    @TableField("assistance_plan")
    private String assistancePlan;

    @TableField("assistance_actions")
    private String assistanceActions;

    @TableField("start_date")
    private LocalDate startDate;

    @TableField("end_date")
    private LocalDate endDate;

    @TableField("result")
    private String result;

    @TableField("follow_up_date")
    private LocalDate followUpDate;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
