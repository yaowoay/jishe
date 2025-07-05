package com.coldwind.easyoj.model.dto.response;

import lombok.Data;

/**
 * 代码运行响应
 */
@Data
public class ProblemRunResponse {
    
    /**
     * 是否成功
     */
    private Boolean success;
    
    /**
     * 输出结果
     */
    private String output;
    
    /**
     * 错误信息
     */
    private String error;
    
    /**
     * 运行时间（毫秒）
     */
    private Long time;
    
    /**
     * 内存使用（KB）
     */
    private Long memory;
} 