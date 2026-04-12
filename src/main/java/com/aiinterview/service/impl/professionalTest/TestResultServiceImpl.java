package com.aiinterview.service.impl.professionalTest;

import com.aiinterview.model.dto.professionalTest.TestResultDTO;
import com.aiinterview.model.entity.professionalTest.TestResult;
import com.aiinterview.model.entity.professionalTest.TestQuestion;
import com.aiinterview.model.entity.professionalTest.UserAnswer;
import com.aiinterview.repository.professionalTest.TestResultRepository;
import com.aiinterview.repository.professionalTest.TestQuestionRepository;
import com.aiinterview.repository.professionalTest.UserAnswerRepository;
import com.aiinterview.service.professionalTest.TestResultService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 测试结果服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TestResultServiceImpl implements TestResultService {
    
    private final TestResultRepository testResultRepository;
    private final TestQuestionRepository testQuestionRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final ObjectMapper objectMapper;
    
    @Override
    public TestResult saveTestResult(TestResultDTO testResultDTO) {
        log.info("开始保存测试结果，用户ID: {}, 类别: {}", testResultDTO.getUserId(), testResultDTO.getCategory());

        // 验证用户ID是否存在（可选：添加用户存在性检查）
        if (testResultDTO.getUserId() == null || testResultDTO.getUserId() <= 0) {
            throw new IllegalArgumentException("无效的用户ID: " + testResultDTO.getUserId());
        }

        try {
            // 创建测试结果实体
            TestResult testResult = new TestResult();
            testResult.setUserId(testResultDTO.getUserId());
            testResult.setCategory(testResultDTO.getCategory());
            testResult.setCategoryName(testResultDTO.getCategoryName());
            testResult.setScore(testResultDTO.getScore());
            testResult.setTotalQuestions(testResultDTO.getTotalQuestions());
            testResult.setCorrectAnswers(testResultDTO.getCorrectAnswers());
            testResult.setDuration(testResultDTO.getDuration());
            testResult.setCompletedAt(testResultDTO.getCompletedAt());
            
            // 将分析报告转换为JSON字符串
            if (testResultDTO.getAnalysisReport() != null) {
                String analysisReportJson = objectMapper.writeValueAsString(testResultDTO.getAnalysisReport());
                testResult.setAnalysisReport(analysisReportJson);
            }
            
            // 保存测试结果主记录
            testResultRepository.insert(testResult);
            log.info("测试结果保存成功，ID: {}", testResult.getId());

            // 保存题目详情
            if (testResultDTO.getQuestions() != null && !testResultDTO.getQuestions().isEmpty()) {
                saveTestQuestions(testResult.getId(), testResultDTO.getQuestions());
            }

            // 保存用户答题记录
            if (testResultDTO.getUserAnswers() != null && !testResultDTO.getUserAnswers().isEmpty()) {
                saveUserAnswers(testResult.getId(), testResultDTO.getUserAnswers());
            }

            return testResult;
            
        } catch (JsonProcessingException e) {
            log.error("转换分析报告为JSON时发生错误", e);
            throw new RuntimeException("保存测试结果失败：分析报告格式错误", e);
        } catch (Exception e) {
            log.error("保存测试结果时发生错误", e);
            throw new RuntimeException("保存测试结果失败", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<TestResult> findById(Long id) {
        TestResult result = testResultRepository.selectById(id);
        return Optional.ofNullable(result);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TestResult> findByUserId(Integer userId) {
        return testResultRepository.findByUserIdOrderByCompletedAtDesc(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TestResult> findByUserIdAndCategory(Integer userId, String category) {
        return testResultRepository.findByUserIdAndCategoryOrderByCompletedAtDesc(userId, category);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<TestResult> findByUserIdWithPagination(Integer userId, Page<TestResult> page) {
        QueryWrapper<TestResult> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).orderByDesc("completed_at");
        return testResultRepository.selectPage(page, queryWrapper);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TestResult> findByUserIdAndCategoryWithPagination(Integer userId, String category, Page<TestResult> page) {
        QueryWrapper<TestResult> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("category", category).orderByDesc("completed_at");
        return testResultRepository.selectPage(page, queryWrapper);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TestResult> findByUserIdAndTimeRange(Integer userId, LocalDateTime startTime, LocalDateTime endTime) {
        return testResultRepository.findByUserIdAndCompletedAtBetweenOrderByCompletedAtDesc(userId, startTime, endTime);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getTestStatistics(Integer userId) {
        Map<String, Object> statistics = testResultRepository.findTestStatisticsByUserId(userId);

        if (statistics == null || statistics.isEmpty()) {
            statistics = new HashMap<>();
            statistics.put("totalTests", 0L);
            statistics.put("averageScore", 0.0);
            statistics.put("bestScore", 0);
            statistics.put("lowestScore", 0);
        } else {
            // 处理平均分数的精度
            Object avgScore = statistics.get("averageScore");
            if (avgScore != null && avgScore instanceof Double) {
                statistics.put("averageScore", Math.round(((Double) avgScore) * 100.0) / 100.0);
            }
        }

        return statistics;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getTestStatisticsByCategory(Integer userId, String category) {
        Map<String, Object> statistics = testResultRepository.findTestStatisticsByUserIdAndCategory(userId, category);

        if (statistics == null || statistics.isEmpty()) {
            statistics = new HashMap<>();
            statistics.put("totalTests", 0L);
            statistics.put("averageScore", 0.0);
            statistics.put("bestScore", 0);
            statistics.put("lowestScore", 0);
        } else {
            // 处理平均分数的精度
            Object avgScore = statistics.get("averageScore");
            if (avgScore != null && avgScore instanceof Double) {
                statistics.put("averageScore", Math.round(((Double) avgScore) * 100.0) / 100.0);
            }
        }

        return statistics;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<TestResult> getLatestTestResult(Integer userId) {
        TestResult result = testResultRepository.findFirstByUserIdOrderByCompletedAtDesc(userId);
        return Optional.ofNullable(result);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TestResult> getLatestTestResultByCategory(Integer userId, String category) {
        TestResult result = testResultRepository.findFirstByUserIdAndCategoryOrderByCompletedAtDesc(userId, category);
        return Optional.ofNullable(result);
    }
    
    @Override
    public boolean deleteTestResult(Long id, Integer userId) {
        try {
            TestResult testResult = testResultRepository.selectById(id);
            if (testResult != null) {
                // 验证是否为当前用户的测试结果
                if (testResult.getUserId().equals(userId)) {
                    testResultRepository.deleteById(id);
                    log.info("删除测试结果成功，ID: {}, 用户ID: {}", id, userId);
                    return true;
                } else {
                    log.warn("用户 {} 尝试删除不属于自己的测试结果 {}", userId, id);
                    return false;
                }
            }
            return false;
        } catch (Exception e) {
            log.error("删除测试结果时发生错误，ID: {}", id, e);
            return false;
        }
    }
    
    @Override
    public int batchDeleteTestResults(List<Long> ids, Integer userId) {
        int deletedCount = 0;
        for (Long id : ids) {
            if (deleteTestResult(id, userId)) {
                deletedCount++;
            }
        }
        log.info("批量删除测试结果完成，用户ID: {}, 删除数量: {}/{}", userId, deletedCount, ids.size());
        return deletedCount;
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getTotalTestCount(Integer userId) {
        return testResultRepository.countByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getTestCountByCategory(Integer userId, String category) {
        return testResultRepository.countByUserIdAndCategory(userId, category);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Integer getMaxScore(Integer userId) {
        Integer maxScore = testResultRepository.findMaxScoreByUserId(userId);
        return maxScore != null ? maxScore : 0;
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getMaxScoreByCategory(Integer userId, String category) {
        Integer maxScore = testResultRepository.findMaxScoreByUserIdAndCategory(userId, category);
        return maxScore != null ? maxScore : 0;
    }

    /**
     * 保存测试题目详情
     */
    private void saveTestQuestions(Long testResultId, List<TestResultDTO.QuestionDTO> questions) {
        try {
            for (TestResultDTO.QuestionDTO questionDTO : questions) {
                TestQuestion testQuestion = new TestQuestion();
                testQuestion.setTestResultId(testResultId);
                testQuestion.setQuestionId(questionDTO.getId());
                testQuestion.setQuestion(questionDTO.getQuestion());
                testQuestion.setEnglishQuestion(questionDTO.getEnglishQuestion());
                testQuestion.setQuestionType(questionDTO.getType());
                testQuestion.setChineseAnswer(questionDTO.getChineseAnswer());
                testQuestion.setEnglishAnswer(questionDTO.getEnglishAnswer());
                testQuestion.setDifficulty(questionDTO.getDifficulty());
                testQuestion.setCategory(questionDTO.getCategory());

                // 将选项和正确答案转换为JSON字符串
                if (questionDTO.getOptions() != null) {
                    testQuestion.setOptions(objectMapper.writeValueAsString(questionDTO.getOptions()));
                }
                if (questionDTO.getCorrectAnswer() != null) {
                    testQuestion.setCorrectAnswer(objectMapper.writeValueAsString(questionDTO.getCorrectAnswer()));
                }

                testQuestionRepository.insert(testQuestion);
            }
            log.info("保存测试题目成功，测试结果ID: {}, 题目数量: {}", testResultId, questions.size());
        } catch (Exception e) {
            log.error("保存测试题目失败，测试结果ID: {}", testResultId, e);
            throw new RuntimeException("保存测试题目失败", e);
        }
    }

    /**
     * 保存用户答题记录
     */
    private void saveUserAnswers(Long testResultId, List<TestResultDTO.UserAnswerDTO> userAnswers) {
        try {
            for (TestResultDTO.UserAnswerDTO answerDTO : userAnswers) {
                UserAnswer userAnswer = new UserAnswer();
                userAnswer.setTestResultId(testResultId);
                userAnswer.setQuestionId(answerDTO.getQuestionId());
                userAnswer.setIsCorrect(answerDTO.getIsCorrect());
                userAnswer.setAnswerTime(null); // 暂时不记录答题时间，可以后续扩展

                // 将用户答案转换为JSON字符串
                if (answerDTO.getUserAnswer() != null) {
                    userAnswer.setUserAnswer(objectMapper.writeValueAsString(answerDTO.getUserAnswer()));
                }

                userAnswerRepository.insert(userAnswer);
            }
            log.info("保存用户答题记录成功，测试结果ID: {}, 答题记录数量: {}", testResultId, userAnswers.size());
        } catch (Exception e) {
            log.error("保存用户答题记录失败，测试结果ID: {}", testResultId, e);
            throw new RuntimeException("保存用户答题记录失败", e);
        }
    }
}
