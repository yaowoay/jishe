package com.aiinterview.model.dto.professionalTest;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 测试结果数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestResultDTO {
    
    @NotNull(message = "用户ID不能为空")
    private Integer userId;
    
    @NotBlank(message = "测试类别不能为空")
    @Size(max = 50, message = "测试类别长度不能超过50个字符")
    private String category;
    
    @NotBlank(message = "测试类别名称不能为空")
    @Size(max = 100, message = "测试类别名称长度不能超过100个字符")
    private String categoryName;
    
    @NotNull(message = "分数不能为空")
    @Min(value = 0, message = "分数不能小于0")
    @Max(value = 100, message = "分数不能大于100")
    private Integer score;
    
    @NotNull(message = "总题数不能为空")
    @Min(value = 1, message = "总题数必须大于0")
    private Integer totalQuestions;
    
    @NotNull(message = "正确答案数不能为空")
    @Min(value = 0, message = "正确答案数不能小于0")
    private Integer correctAnswers;
    
    @NotNull(message = "测试用时不能为空")
    @Min(value = 0, message = "测试用时不能小于0")
    private Integer duration;
    
    @NotNull(message = "完成时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completedAt;
    
    // 题目列表
    private List<QuestionDTO> questions;
    
    // 用户答案列表
    private List<UserAnswerDTO> userAnswers;
    
    // 能力分析报告
    private Map<String, Object> analysisReport;
    
    /**
     * 题目DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionDTO {
        private Integer id;
        private String question;
        private String englishQuestion;
        private String type;
        private List<String> options;
        private Object correctAnswer;
        private String chineseAnswer;
        private String englishAnswer;
        private String difficulty;
        private String category;
    }
    
    /**
     * 用户答案DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserAnswerDTO {
        private Integer questionId;
        private Object userAnswer;
        private Boolean isCorrect;
    }
}
