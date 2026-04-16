package com.aiinterview.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("resume")
public class resumer {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID（对应 users.user_id） */
    @TableField("user_id")
    private Long userId;

    /** 简历名称 */
    @TableField("name")
    private String name;

    /** 真实姓名 ss*/
    @TableField("full_name")
    private String fullName;

    /** 电话 */
    @TableField("phone")
    private String phone;

    /** 邮箱 */
    @TableField("email")
    private String email;

    /** 头像 */
    @TableField("avatar_url")
    private String avatarUrl;

    /** 期望职位 */
    @TableField("position")
    private String position;

    /** 工作年限 */
    @TableField("work_years")
    private Integer workYears;

    /** 期望城市 */
    @TableField("expected_city")
    private String expectedCity;

    /** 期望薪资下限 */
    @TableField("expected_salary_min")
    private Integer expectedSalaryMin;

    /** 期望薪资上限 */
    @TableField("expected_salary_max")
    private Integer expectedSalaryMax;

    /** 期望行业 */
    @TableField("expected_industry")
    private String expectedIndustry;

    /** 个人简介 */
    @TableField("profile")
    private String profile;

    /** 模板ID */
    @TableField("template_id")
    private Integer templateId;

    /** 模板类型 */
    @TableField("template_type")
    private String templateType;

    /** 模板配置 */
    @TableField("template_config")
    private String templateConfig;

    /** 是否默认 */
    @TableField("is_default")
    private Boolean isDefault;

    /** 软删除 */
    @TableLogic
    @TableField("is_deleted")
    private Boolean isDeleted;

    /** 分享码 */
    @TableField("share_code")
    private String shareCode;

    /** 查看次数 */
    @TableField("view_count")
    private Integer viewCount;

    /** 下载次数 */
    @TableField("download_count")
    private Integer downloadCount;

    /** 简历状态 */
    @TableField("status")
    private String status;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}