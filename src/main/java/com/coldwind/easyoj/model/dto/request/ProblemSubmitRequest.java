package com.coldwind.easyoj.model.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 代码提交请求
 */
@Data
public class ProblemSubmitRequest {
    
    /**
     * 题目ID
     */
    @NotNull(message = "题目ID不能为空")
    private Integer problemId;
    
    /**
     * 代码内容
     */
    @NotBlank(message = "代码不能为空")
    private String code;
    
    /**
     * 编程语言
     */
    @NotBlank(message = "编程语言不能为空")
    private String language;
}