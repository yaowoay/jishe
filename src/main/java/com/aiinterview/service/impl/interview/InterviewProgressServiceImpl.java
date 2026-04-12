package com.aiinterview.service.impl.interview;

import com.aiinterview.model.dto.writtenTest.WrittenTestExamDTO;
import com.aiinterview.model.entity.interview.InterviewProgress;
import com.aiinterview.repository.interview.InterviewProgressRepository;
import com.aiinterview.service.interview.InterviewProgressService;
import com.aiinterview.service.application.ApplicationService;
import com.alibaba.fastjson.JSON;

import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 面试进度服务实现类
 */
@Slf4j
@Service
public class InterviewProgressServiceImpl implements InterviewProgressService {

    @Autowired
    private InterviewProgressRepository interviewProgressRepository;

    @Autowired
    private ApplicationService applicationService;

    @Override
    @Transactional
    public InterviewProgress saveWrittenTestResult(Integer applicationId, WrittenTestExamDTO.TestResultDTO testResult, Boolean passed) {
        log.info("保存笔试结果到面试进度，申请ID: {}, 是否通过: {}", applicationId, passed);

        try {
            // 查找或创建面试进度记录
            InterviewProgress progress = interviewProgressRepository.findByApplicationId(applicationId);
            if (progress == null) {
                progress = initializeProgress(applicationId);
            }

            // 更新笔试相关信息
            progress.setWrittenTestStatus(passed ? "passed" : "failed");
            progress.setWrittenTestScore(testResult.getScore());
            progress.setWrittenTestPassed(passed);
            progress.setWrittenTestCompletedAt(LocalDateTime.now());
            progress.setWrittenTestResultJson(JSON.toJSONString(testResult));

            // 更新整体状态
            if (passed) {
                progress.setOverallStatus("interview"); // 进入面试阶段
                progress.setInterviewStatus("not_started"); // 面试未开始
            } else {
                progress.setOverallStatus("rejected"); // 被拒绝
                progress.setFinalResult("failed"); // 最终失败
                progress.setInterviewStatus("not_started"); // 面试不会进行
            }

            // 保存或更新记录
            if (progress.getId() == null) {
                interviewProgressRepository.insert(progress);
            } else {
                interviewProgressRepository.updateById(progress);
            }

            // 更新申请状态
            updateApplicationStatus(applicationId, testResult.getScore(), passed);

            log.info("笔试结果保存成功，进度ID: {}", progress.getId());
            return progress;

        } catch (Exception e) {
            log.error("保存笔试结果失败", e);
            throw new RuntimeException("保存笔试结果失败: " + e.getMessage());
        }
    }

    @Override
    public InterviewProgress getProgressByApplicationId(Integer applicationId) {
        log.info("获取面试进度，申请ID: {}", applicationId);
        return interviewProgressRepository.findByApplicationId(applicationId);
    }

    @Override
    @Transactional
    public boolean updateInterviewStatus(Integer applicationId, String status) {
        log.info("更新面试状态，申请ID: {}, 状态: {}", applicationId, status);

        try {
            InterviewProgress progress = interviewProgressRepository.findByApplicationId(applicationId);
            if (progress == null) {
                log.warn("未找到面试进度记录，申请ID: {}", applicationId);
                return false;
            }

            progress.setInterviewStatus(status);
            
            // 根据面试状态更新整体状态
            switch (status) {
                case "scheduled":
                    progress.setOverallStatus("interview");
                    break;
                case "in_progress":
                    progress.setOverallStatus("interview");
                    break;
                case "completed":
                    progress.setOverallStatus("completed");
                    break;
                case "passed":
                    progress.setOverallStatus("completed");
                    progress.setFinalResult("passed");
                    break;
                case "failed":
                    progress.setOverallStatus("completed");
                    progress.setFinalResult("failed");
                    break;
            }

            interviewProgressRepository.updateById(progress);
            return true;

        } catch (Exception e) {
            log.error("更新面试状态失败", e);
            return false;
        }
    }

    @Override
    @Transactional
    public InterviewProgress saveInterviewResult(Integer applicationId, Object interviewResult, Boolean passed) {
        log.info("保存面试结果，申请ID: {}, 是否通过: {}", applicationId, passed);

        try {
            InterviewProgress progress = interviewProgressRepository.findByApplicationId(applicationId);
            if (progress == null) {
                throw new RuntimeException("未找到面试进度记录");
            }

            // 更新面试结果
            progress.setInterviewStatus(passed ? "passed" : "failed");
            progress.setInterviewPassed(passed);
            progress.setInterviewCompletedAt(LocalDateTime.now());
            progress.setInterviewResultJson(JSON.toJSONString(interviewResult));

            // 更新最终状态
            progress.setOverallStatus("completed");
            progress.setFinalResult(passed ? "passed" : "failed");

            interviewProgressRepository.updateById(progress);
            return progress;

        } catch (Exception e) {
            log.error("保存面试结果失败", e);
            throw new RuntimeException("保存面试结果失败: " + e.getMessage());
        }
    }

    @Override
    public List<InterviewProgress> getAllProgress() {
        return interviewProgressRepository.selectList(null);
    }

    @Override
    public List<InterviewProgress> getProgressByStatus(String status) {
        return interviewProgressRepository.findByOverallStatus(status);
    }

    @Override
    @Transactional
    public InterviewProgress initializeProgress(Integer applicationId) {
        log.info("初始化面试进度记录，申请ID: {}", applicationId);

        InterviewProgress progress = new InterviewProgress();
        progress.setApplicationId(applicationId);
        progress.setWrittenTestStatus("not_started");
        progress.setInterviewStatus("not_started");
        progress.setOverallStatus("pending");
        progress.setFinalResult("pending");

        return progress;
    }

    /**
     * 根据笔试结果更新申请状态
     * @param applicationId 申请ID
     * @param score 笔试分数
     * @param passed 是否通过
     */
    private void updateApplicationStatus(Integer applicationId, BigDecimal score, Boolean passed) {
        try {
            log.info("更新申请状态，申请ID: {}, 分数: {}, 是否通过: {}", applicationId, score, passed);

            String newStatus;
            String feedback;

            if (score.compareTo(new BigDecimal("60")) < 0) {
                // 分数小于60分，状态改为淘汰
                newStatus = "淘汰";
                feedback = String.format("笔试成绩：%.1f分，未达到及格线(60分)，很遗憾未能通过笔试。", score.doubleValue());
            } else {
                // 分数大于等于60分，状态改为待面试
                newStatus = "待面试";
                feedback = String.format("笔试成绩：%.1f分，恭喜通过笔试！请等待面试安排。", score.doubleValue());
            }

            // 调用ApplicationService更新状态
            boolean success = applicationService.updateApplicationStatus(
                applicationId.longValue(),
                newStatus,
                feedback
            );

            if (success) {
                log.info("申请状态更新成功，申请ID: {}, 新状态: {}", applicationId, newStatus);
            } else {
                log.warn("申请状态更新失败，申请ID: {}", applicationId);
            }

        } catch (Exception e) {
            log.error("更新申请状态失败，申请ID: {}", applicationId, e);
            // 不抛出异常，避免影响面试进度的保存
        }
    }
}
