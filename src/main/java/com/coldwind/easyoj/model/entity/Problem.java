package com.coldwind.easyoj.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 题目实体类
 */
@Data
@TableName("problem")
public class Problem implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 题目ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 题目标题
     */
    private String title;
    
    /**
     * 题目描述
     */
    private String description;
    
    /**
     * 是否收藏
     */
    private Boolean isFavorite;
    
    /**
     * 题目状态：未尝试、尝试中、通过
     */
    private String status;
} 