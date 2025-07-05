package com.coldwind.easyoj.service.impl;

import com.coldwind.easyoj.mapper.ExamRecordMapper;
import com.coldwind.easyoj.model.dto.response.ExamRecordDTO;
import com.coldwind.easyoj.model.dto.response.ExamQuestionDetailDTO;
import com.coldwind.easyoj.model.entity.ExamQuestionDetail;
import com.coldwind.easyoj.model.entity.ExamJudgmentDetail;
import com.coldwind.easyoj.service.ExamRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 笔试记录服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExamRecordServiceImpl implements ExamRecordService {
    
    private final ExamRecordMapper examRecordMapper;
    
    @Override
    public List<ExamRecordDTO> getUserExamRecords(String userId) {
        // 获取所有已完成的考试taskId
        List<String> taskIds = examRecordMapper.getAllCompletedTaskIds();
        
        return taskIds.stream()
                .map(this::getExamRecordByTaskId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    @Override
    public ExamRecordDTO getExamRecordByTaskId(String taskId) {
        try {
            // 获取题目详情
            List<ExamQuestionDetail> questions = examRecordMapper.getQuestionsByTaskId(taskId);
            // 获取判题详情
            List<ExamJudgmentDetail> judgments = examRecordMapper.getJudgmentsByTaskId(taskId);
            
            if (questions.isEmpty() || judgments.isEmpty()) {
                log.warn("未找到taskId为{}的考试记录", taskId);
                return null;
            }
            
            // 构建判题结果Map，方便查找
            Map<String, ExamJudgmentDetail> judgmentMap = judgments.stream()
                    .collect(Collectors.toMap(ExamJudgmentDetail::getQuestionId, j -> j));
            
            // 构建题目详情DTO列表
            List<ExamQuestionDetailDTO> questionDetails = questions.stream()
                    .map(q -> buildQuestionDetailDTO(q, judgmentMap.get(q.getQuestionId())))
                    .collect(Collectors.toList());
            
            // 构建考试记录DTO
            ExamRecordDTO record = new ExamRecordDTO();
            record.setTaskId(taskId);
            record.setQuestions(questionDetails);
            record.setTotalQuestions(questions.size());
            
            // 统计正确题目数
            long correctCount = judgments.stream()
                    .filter(ExamJudgmentDetail::getIsCorrect)
                    .count();
            record.setCorrectCount((int) correctCount);
            
            // 计算得分
            double score = questions.size() > 0 ? (double) correctCount / questions.size() * 100 : 0;
            record.setScore(score);
            
            // 设置考试时间
            String examTimeStr = examRecordMapper.getExamTimeByTaskId(taskId);
            if (examTimeStr != null) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    record.setExamTime(sdf.parse(examTimeStr));
                } catch (Exception e) {
                    log.error("解析考试时间失败: {}", examTimeStr, e);
                    record.setExamTime(new Date());
                }
            } else {
                record.setExamTime(new Date());
            }
            
            return record;
            
        } catch (Exception e) {
            log.error("获取考试记录失败，taskId: {}", taskId, e);
            return null;
        }
    }
    
    @Override
    public List<ExamRecordDTO> getRecentExamRecords(String userId, int limit) {
        List<String> taskIds = examRecordMapper.getAllCompletedTaskIds();
        
        return taskIds.stream()
                .limit(limit)
                .map(this::getExamRecordByTaskId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ExamRecordDTO> getBatchExamRecords(String userId) {
        // 获取同一批次生成的考试taskId
        List<String> taskIds = examRecordMapper.getBatchExamTaskIds();
        
        return taskIds.stream()
                .map(this::getExamRecordByTaskId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    /**
     * 构建题目详情DTO
     */
    private ExamQuestionDetailDTO buildQuestionDetailDTO(ExamQuestionDetail question, ExamJudgmentDetail judgment) {
        ExamQuestionDetailDTO dto = new ExamQuestionDetailDTO();
        dto.setQuestionId(question.getQuestionId());
        dto.setType(question.getType());
        dto.setContent(question.getContent());
        dto.setOptionA(question.getOptionA());
        dto.setOptionB(question.getOptionB());
        dto.setOptionC(question.getOptionC());
        dto.setOptionD(question.getOptionD());
        dto.setCorrectAnswer(question.getAnswer());
        dto.setExplanation(question.getExplanation());
        
        if (judgment != null) {
            dto.setUserAnswer(judgment.getUserAnswer());
            dto.setIsCorrect(judgment.getIsCorrect());
            // 如果答错了，设置错误原因
            if (!judgment.getIsCorrect()) {
                dto.setErrorReason(judgment.getExplanation());
            }
        }
        
        return dto;
    }
} 