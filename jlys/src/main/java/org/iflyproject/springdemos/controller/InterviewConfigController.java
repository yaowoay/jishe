package org.iflyproject.springdemos.controller;
//已完成测试
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.iflyproject.springdemos.domain.InterviewerConfig;
import org.iflyproject.springdemos.domain.QuestionConfig;
import org.iflyproject.springdemos.domain.InterviewMode;
import org.iflyproject.springdemos.service.InterviewConfigService;

/**
 * 面试配置控制器
 * 对应三个配置模块：
 * 1. 面试官设置（形象、语气、性格）
 * 2. 题目设置（范围、难易程度、数量、偏好）
 * 3. 面试模式设置（模拟/正式、岗位、时长）
 */
@RestController
@RequestMapping("/api/interview/config")
public class InterviewConfigController {

    @Autowired
    private InterviewConfigService configService;

    /**
     * 保存面试官配置
     * 对应图片中的"面试官设置"部分
     */
    @PostMapping("/interviewer")
    public ResponseEntity<?> saveInterviewerConfig(@RequestBody InterviewerConfig config) {
        InterviewerConfig savedConfig = configService.saveInterviewerConfig(config);
        return ResponseEntity.ok(savedConfig);
    }

    /**
     * 保存题目配置
     * 对应图片中的"题目设置"部分
     */
    @PostMapping("/questions")
    public ResponseEntity<?> saveQuestionConfig(@RequestBody QuestionConfig config) {
        QuestionConfig savedConfig = configService.saveQuestionConfig(config);
        return ResponseEntity.ok(savedConfig);
    }

    /**
     * 保存面试模式
     * 对应图片中的"面试模式设置"部分
     */
    @PostMapping("/mode")
    public ResponseEntity<?> saveInterviewMode(@RequestBody InterviewMode mode) {
        InterviewMode savedMode = configService.saveInterviewMode(mode);
        return ResponseEntity.ok(savedMode);
    }
}