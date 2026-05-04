package com.aiinterview.service;

import com.aiinterview.model.dto.candidataAnswer.CandidateAnswerStatsDTO;
import com.aiinterview.model.entity.candidataAnswer.CandidateAnswerStats;
import com.aiinterview.service.candidataAnswer.CandidateAnswerStatsService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 应聘者答题统计信息服务测试类
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CandidateAnswerStatsServiceTest {

    @Autowired
    private CandidateAnswerStatsService candidateAnswerStatsService;

    @Test
    public void testSetWrittenTestSettingsWithAI() {
        // 测试AI生成模式的笔试设置
        Integer applicationId = 1;
        Integer answerCount = 0; // AI模式下数量为0
        String answerRange = ""; // AI模式下范围为空
        Boolean aiGenerated = true;

        CandidateAnswerStats result = candidateAnswerStatsService.setWrittenTestSettings(
                applicationId, answerCount, answerRange, aiGenerated);

        assertNotNull(result);
        assertEquals(applicationId, result.getApplicationId());
        assertEquals(answerCount, result.getWrittenAnswerCount());
        assertEquals(answerRange, result.getWrittenAnswerRange());
        assertTrue(result.getWrittenAiGenerated());
    }

    @Test
    public void testSetWrittenTestSettingsWithoutAI() {
        // 测试自定义模式的笔试设置
        Integer applicationId = 2;
        Integer answerCount = 15;
        String answerRange = "java-basic,spring,database";
        Boolean aiGenerated = false;

        CandidateAnswerStats result = candidateAnswerStatsService.setWrittenTestSettings(
                applicationId, answerCount, answerRange, aiGenerated);

        assertNotNull(result);
        assertEquals(applicationId, result.getApplicationId());
        assertEquals(answerCount, result.getWrittenAnswerCount());
        assertEquals(answerRange, result.getWrittenAnswerRange());
        assertFalse(result.getWrittenAiGenerated());
    }

    @Test
    public void testSetInterviewSettingsWithAI() {
        // 测试AI生成模式的面试设置
        Integer applicationId = 3;
        Integer answerCount = 0; // AI模式下数量为0
        String answerRange = ""; // AI模式下范围为空
        Boolean aiGenerated = true;

        CandidateAnswerStats result = candidateAnswerStatsService.setInterviewSettings(
                applicationId, answerCount, answerRange, aiGenerated);

        assertNotNull(result);
        assertEquals(applicationId, result.getApplicationId());
        assertEquals(answerCount, result.getInterviewAnswerCount());
        assertEquals(answerRange, result.getInterviewAnswerRange());
        assertTrue(result.getInterviewAiGenerated());
    }

    @Test
    public void testSetInterviewSettingsWithoutAI() {
        // 测试自定义模式的面试设置
        Integer applicationId = 4;
        Integer answerCount = 8;
        String answerRange = "technical-depth,project-experience,problem-solving";
        Boolean aiGenerated = false;

        CandidateAnswerStats result = candidateAnswerStatsService.setInterviewSettings(
                applicationId, answerCount, answerRange, aiGenerated);

        assertNotNull(result);
        assertEquals(applicationId, result.getApplicationId());
        assertEquals(answerCount, result.getInterviewAnswerCount());
        assertEquals(answerRange, result.getInterviewAnswerRange());
        assertFalse(result.getInterviewAiGenerated());
    }

    @Test
    public void testSaveOrUpdateWithAIFields() {
        // 测试保存包含AI字段的DTO
        CandidateAnswerStatsDTO dto = new CandidateAnswerStatsDTO();
        dto.setApplicationId(5);
        dto.setWrittenAnswerCount(12);
        dto.setWrittenAnswerRange("java-basic,algorithm");
        dto.setWrittenAiGenerated(false);
        dto.setInterviewAnswerCount(6);
        dto.setInterviewAnswerRange("technical-depth,teamwork");
        dto.setInterviewAiGenerated(true);

        CandidateAnswerStats result = candidateAnswerStatsService.saveOrUpdate(dto);

        assertNotNull(result);
        assertEquals(dto.getApplicationId(), result.getApplicationId());
        assertEquals(dto.getWrittenAnswerCount(), result.getWrittenAnswerCount());
        assertEquals(dto.getWrittenAnswerRange(), result.getWrittenAnswerRange());
        assertEquals(dto.getWrittenAiGenerated(), result.getWrittenAiGenerated());
        assertEquals(dto.getInterviewAnswerCount(), result.getInterviewAnswerCount());
        assertEquals(dto.getInterviewAnswerRange(), result.getInterviewAnswerRange());
        assertEquals(dto.getInterviewAiGenerated(), result.getInterviewAiGenerated());
    }
}
