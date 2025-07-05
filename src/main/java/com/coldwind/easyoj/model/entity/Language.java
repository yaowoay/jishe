package com.coldwind.easyoj.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 编程语言实体类
 */
@Data
@TableName("language")
public class Language implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 语言ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 语言名称
     */
    private String name;
} 