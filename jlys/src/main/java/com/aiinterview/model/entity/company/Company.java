package com.aiinterview.model.entity.company;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 公司实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("companies")
public class Company {
    
    @TableId(value = "company_id", type = IdType.AUTO)
    private Integer companyId;
    
    @TableField("user_id")
    private Integer userId;
    
    @TableField("company_name")
    private String companyName;
    
    @TableField("industry")
    private String industry;
    
    @TableField("address")
    private String address;
    
    @TableField("scale")
    private String scale; // '1-50人', '51-100人', '101-500人', '500人以上'
    
    @TableField("website")
    private String website;
    
    @TableField("contact_phone")
    private String contactPhone;
    
    @TableField("logo_url")
    private String logoUrl;
    
    @TableField("description")
    private String description;

    @TableField("verify_status")
    private String verifyStatus;

    @TableField("credit_score")
    private Integer creditScore;

    @TableField("profile_completion")
    private Integer profileCompletion; // 0-100

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
