package com.coldwind.easyoj.model.vo;

import lombok.Data;

import java.util.List;

/**
 * 题目VO
 */
@Data
public class ProblemVO {
    
    /**
     * 题目ID
     */
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
     * 题目状态
     */
    private String status;
    
    /**
     * 示例列表
     */
    private List<ExampleVO> examples;
} 