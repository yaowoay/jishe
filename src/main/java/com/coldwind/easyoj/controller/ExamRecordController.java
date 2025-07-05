package com.coldwind.easyoj.controller;

import com.coldwind.easyoj.model.dto.response.ExamRecordDTO;
import com.coldwind.easyoj.service.ExamRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 笔试记录Controller
 */
@Slf4j
@RestController
@RequestMapping("/exam-record")
@RequiredArgsConstructor
public class ExamRecordController {
    
    private final ExamRecordService examRecordService;
    
    /**
     * 获取用户的所有笔试记录
     * @param userId 用户ID（可以从token中获取，这里暂时作为参数）
     * @return 笔试记录列表
     */
//    @GetMapping("/list")
//    public List<ExamRecordDTO> getUserExamRecords(@RequestParam(required = false) String userId) {
//        log.info("获取用户笔试记录，userId: {}", userId);
//        return examRecordService.getUserExamRecords(userId);
//    }
    
    /**
     * 根据taskId获取具体的笔试记录详情
     * @param taskId 考试任务ID
     * @return 笔试记录详情
     */

    //根据浏览器缓存的taskID来获取
    @GetMapping("/detail/{taskId}")
    public ExamRecordDTO getExamRecordDetail(@PathVariable String taskId) {
        log.info("获取笔试记录详情，taskId: {}", taskId);
        return examRecordService.getExamRecordByTaskId(taskId);
    }
    
    /**
     * 获取最近的笔试记录
     * @param userId 用户ID
     * @param limit 限制数量，默认5条
     * @return 最近的笔试记录列表
     */
//    @GetMapping("/recent")
//    public List<ExamRecordDTO> getRecentExamRecords(
//            @RequestParam(required = false) String userId,
//            @RequestParam(defaultValue = "5") int limit) {
//        log.info("获取最近笔试记录，userId: {}, limit: {}", userId, limit);
//        return examRecordService.getRecentExamRecords(userId, limit);
//    }
    
    /**
     * 获取同一批次生成的考试记录（created_at相差不到1秒的题目）
     * @param userId 用户ID
     * @return 批次考试记录列表
     */
//    @GetMapping("/batch")
//    public List<ExamRecordDTO> getBatchExamRecords(@RequestParam(required = false) String userId) {
//        log.info("获取批次考试记录，userId: {}", userId);
//        return examRecordService.getBatchExamRecords(userId);
//    }
} 