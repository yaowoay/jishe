package com.coldwind.easyoj.model.vo;

import lombok.Data;

/**
 * 示例VO
 */
@Data
public class ExampleVO {
    
    /**
     * 示例ID
     */
    private Integer id;
    
    /**
     * 输入示例
     */
    private String input;
    
    /**
     * 输出示例
     */
    private String output;
}