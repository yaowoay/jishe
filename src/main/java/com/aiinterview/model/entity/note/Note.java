package com.aiinterview.model.entity.note;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 笔记实体类
 * 用于存储用户的学习笔记、面试准备笔记等
 */
@Data
@TableName("notes")
public class Note implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 笔记ID - 主键，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID - 关联用户表
     */
    @TableField("userId")
    private Long userId;
    
    /**
     * 笔记标题
     */
    private String title;
    
    /**
     * 笔记内容 - 支持Markdown格式
     */
    private String content;
    
    /**
     * 笔记分类 - 如：技术笔记、面试准备、学习心得等
     */
    private String category;
    
    /**
     * 标签 - 多个标签用逗号分隔
     */
    private String tags;
    
    /**
     * 是否公开 - 0:私有 1:公开
     */
    @TableField("isPublic")
    private Integer isPublic;
    
    /**
     * 创建时间
     */
    @TableField("createTime")
    private Date createTime;
    
    /**
     * 更新时间
     */
    @TableField("updateTime")
    private Date updateTime;
    
    /**
     * 是否删除 - 逻辑删除标记
     */
    @TableField("isDelete")
    private Integer isDelete;
}
