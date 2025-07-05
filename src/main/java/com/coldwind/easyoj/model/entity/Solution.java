package com.coldwind.easyoj.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 题目解答实体类
 */
@Data
@TableName("solution")
public class Solution implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 解答ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 题目ID
     */
    private Integer problemId;
    
    /**
     * 语言ID
     */
    private Integer languageId;
    
    /**
     * 代码内容
     */
    private String code;
}