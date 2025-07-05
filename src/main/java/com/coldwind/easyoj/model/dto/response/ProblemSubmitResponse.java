package com.coldwind.easyoj.model.dto.response;

import lombok.Data;

/**
 * 代码提交响应
 */
@Data
public class ProblemSubmitResponse {
    
    /**
     * 判题结果：通过、未通过
     */
    private String result;
    
    /**
     * 结果消息
     */
    private String message;
    
    /**
     * 通过测试用例数
     */
    private Integer passedCases;
    
    /**
     * 总测试用例数
     */
    private Integer totalCases;
    
    /**
     * 运行时间（毫秒）
     */
    private Long time;
    
    /**
     * 内存使用（KB）
     */
    private Long memory;
}