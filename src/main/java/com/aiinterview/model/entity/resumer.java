package com.aiinterview.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 简历实体类
 */
@Data
@TableName("resume")
public class resumer {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户唯一标识（手机号或邮箱）
     */
    @TableField("USER_KEY")
    private String userKey;

    /**
     * 简历名称
     */
    @TableField("NAME")
    private String name;

    /**
     * 姓名
     */
    @TableField("FULL_NAME")
    private String fullName;

    /**
     * 电话
     */
    @TableField("PHONE")
    private String phone;

    /**
     * 邮箱
     */
    @TableField("EMAIL")
    private String email;

    /**
     * 期望职位
     */
    @TableField("POSITION")
    private String position;

    /**
     * 工作年限
     */
    @TableField("WORK_YEARS")
    private Integer workYears;

    /**
     * 所在地
     */
    @TableField("LOCATION")
    private String location;

    /**
     * 个人简介
     */
    @TableField("PROFILE")
    private String profile;

    /**
     * 简历模板类型 (template1, template2, template3, template4)
     */
    @TableField("TEMPLATE")
    private String template;

    /**
     * 是否默认简历
     */
    @TableField("IS_DEFAULT")
    private Boolean isDefault;

    /**
     * 软删除标记
     */
    @TableLogic
    @TableField("IS_DELETED")
    private Boolean isDeleted;

    /**
     * 分享链接
     */
    @TableField("SHARE_URL")
    private String shareUrl;

    /**
     * 查看次数
     */
    @TableField("VIEW_COUNT")
    private Integer viewCount;

    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "UPDATE_TIME", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}