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
 * 专业实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("majors")
public class Major {

    @TableId(value = "major_id", type = IdType.AUTO)
    private Long majorId;

    @TableField("college_id")
    private Long collegeId;

    @TableField("major_code")
    private String majorCode;

    @TableField("major_name")
    private String majorName;

    @TableField("education_level")
    private String educationLevel;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
