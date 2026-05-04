package com.aiinterview.model.dto.disctest;

import lombok.Data;

import java.util.List;

/**
 * DISC测试请求DTO
 */
@Data
public class DiscTestRequest {
    
    /**
     * 测试会话ID
     */
    private String testSession;
    
    /**
     * 用户ID（可选）
     */
    private Long userId;
    
    /**
     * 测试答案列表
     */
    private List<DiscAnswer> answers;
    
    /**
     * DISC答案内部类
     */
    @Data
    public static class DiscAnswer {
        /**
         * 题目组号
         */
        private Integer questionGroup;
        
        /**
         * 选择的选项字母
         */
        private String selectedOption;
    }
}
