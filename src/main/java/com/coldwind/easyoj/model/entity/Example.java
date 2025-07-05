package com.coldwind.easyoj.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 题目示例实体类
 */
@Data
@TableName("example")
public class Example implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 示例ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 题目ID
     */
    private Integer problemId;
    
    /**
     * 输入示例
     */
    private String input;
    
    /**
     * 输出示例
     */
    private String output;
} 