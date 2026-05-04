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
 * 学院实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("colleges")
public class College {

    @TableId(value = "college_id", type = IdType.AUTO)
    private Long collegeId;

    @TableField("college_code")
    private String collegeCode;

    @TableField("college_name")
    private String collegeName;

    @TableField("dean")
    private String dean;

    @TableField("student_count")
    private Integer studentCount;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
