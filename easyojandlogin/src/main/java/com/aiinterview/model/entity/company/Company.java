package com.aiinterview.model.entity.company;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("companies")
public class Company {

    @TableId(value = "company_id", type = IdType.AUTO)
    private Long companyId;

    private Long userId;
    private String companyName;
    private String industry;
    private String address;
    private String scale;  // '1-50人','51-100人','101-500人','500人以上'
    private String website;
    private String contactPhone;
    private String logoUrl;
    private String description;

    // ✅ 存在的字段
    private String verifyStatus;  // pending, verified, rejected
    private Integer creditScore;  // 信用评分，默认60

    // ✅ 额外字段
    private Integer profileCompletion;  // 资料完成度 0-100
    private String companyType;  // 企业类型
    private String companyWelfare;  // 福利标签
    private String companyTags;  // 公司标签

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}