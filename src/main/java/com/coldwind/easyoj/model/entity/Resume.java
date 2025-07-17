package com.coldwind.easyoj.model.entity;// 修正 ResumeEditorEnhanced.vue 的保存逻辑package com.coldwind.easyoj.model.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 简历实体类
 */
@Data
@TableName("resume")
public class Resume {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户唯一标识（手机号或邮箱）
     */
    private String userKey;
    
    /**
     * 简历名称
     */
    private String name;
    
    /**
     * 姓名
     */
    private String fullName;
    
    /**
     * 电话
     */
    private String phone;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 期望职位
     */
    private String position;
    
    /**
     * 工作年限
     */
    private Integer workYears;
    
    /**
     * 所在地
     */
    private String location;
    
    /**
     * 个人简介
     */
    private String profile;
    
    /**
     * 是否默认简历
     */
    private Boolean isDefault;
    
    /**
     * 软删除标记
     */
    @TableLogic
    private Boolean isDeleted;
    
    /**
     * 分享链接
     */
    private String shareUrl;
    
    /**
     * 查看次数
     */
    private Integer viewCount;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
} 