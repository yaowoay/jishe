package com.aiinterview.model.entity.user;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("users")
public class User {
    
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;
    
    @TableField("email")
    private String email;
    
    @TableField("password")
    private String password;
    
    @TableField("role")
    private String role; // 'student', 'company' 或 'teacher'
    
    @TableField("phone")
    private String phone; // 手机号
    
    @TableField("profile_completed")
    private Integer profileCompleted; // 0-未完善, 1-已完善
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
