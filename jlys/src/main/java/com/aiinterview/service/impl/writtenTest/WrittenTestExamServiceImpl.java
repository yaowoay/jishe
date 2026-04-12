package com.aiinterview.service.impl.writtenTest;

import com.aiinterview.model.dto.writtenTest.WrittenTestExamDTO;
import com.aiinterview.model.entity.writtenTest.WrittenTestAnswer;
import com.aiinterview.model.entity.writtenTest.WrittenTestResult;
import com.aiinterview.repository.writtenTest.WrittenTestAnswerRepository;
import com.aiinterview.repository.writtenTest.WrittenTestResultRepository;
import com.aiinterview.service.writtenTest.WrittenTestExamService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 笔试考试服务实现类
 */
@Slf4j
@Service
public class WrittenTestExamServiceImpl implements WrittenTestExamService {

    @Autowired
    private WrittenTestResultRepository writtenTestResultRepository;

    @Autowired
    private WrittenTestAnswerRepository writtenTestAnswerRepository;

    @Override
    public WrittenTestExamDTO getExamInfo(Integer applicationId) {
        log.info("获取笔试考试信息，申请ID: {}", applicationId);

        // 1. 获取笔试题目
        WrittenTestResult testResult = writtenTestResultRepository.findByApplicationId(applicationId);
        if (testResult == null) {
            throw new RuntimeException("未找到笔试题目，请联系HR");
        }

        if (!"success".equals(testResult.getGenerationStatus())) {
            throw new RuntimeException("笔试题目生成失败，请联系HR");
        }

        // 2. 检查是否已经答过题
        WrittenTestAnswer existingAnswer = writtenTestAnswerRepository.findByApplicationIdAndTestResultId(
                applicationId, testResult.getId());

        // 3. 解析题目（不包含答案）
        List<WrittenTestExamDTO.ExamQuestionDTO> examQuestions = parseQuestionsForExam(testResult.getQuestionsJson());

        return WrittenTestExamDTO.builder()
                .testResultId(testResult.getId())
                .applicationId(applicationId)
                .jobPosition(testResult.getJobPosition())
                .totalQuestions(testResult.getTotalQuestions())
                .totalScore(testResult.getTotalScore())
                .status(existingAnswer != null ? existingAnswer.getStatus() : "not_started")
                .startTime(existingAnswer != null ? existingAnswer.getStartTime() : null)
                .timeLimit(60) // 默认60分钟
                .questions(examQuestions)
                .build();
    }

    @Override
    @Transactional
    public WrittenTestExamDTO startExam(Integer applicationId) {
        log.info("开始笔试考试，申请ID: {}", applicationId);

        WrittenTestExamDTO examInfo = getExamInfo(applicationId);

        // 创建或更新答题记录
        WrittenTestAnswer answer = writtenTestAnswerRepository.findByApplicationIdAndTestResultId(
                applicationId, examInfo.getTestResultId());

        if (answer == null) {
            // 创建新的答题记录
            answer = new WrittenTestAnswer();
            answer.setApplicationId(applicationId);
            answer.setWrittenTestResultId(examInfo.getTestResultId());
            answer.setTotalQuestions(examInfo.getTotalQuestions());
            answer.setStatus("in_progress");
            answer.setStartTime(LocalDateTime.now());
            writtenTestAnswerRepository.insert(answer);
        } else if ("not_started".equals(answer.getStatus())) {
            // 开始答题
            answer.setStatus("in_progress");
            answer.setStartTime(LocalDateTime.now());
            writtenTestAnswerRepository.updateById(answer);
        } else if ("completed".equals(answer.getStatus())) {
            // 如果已完成，允许重新开始（重置答题记录）
            log.info("检测到已完成的笔试，允许重新开始，申请ID: {}", applicationId);
            answer.setAnswersJson(null);
            answer.setScore(null);
            answer.setCorrectAnswers(null);
            answer.setWrongAnswers(null);
            answer.setCompletionTime(null);
            answer.setSubmitTime(null);
            answer.setStatus("in_progress");
            answer.setStartTime(LocalDateTime.now());
            writtenTestAnswerRepository.updateById(answer);
        } else if ("in_progress".equals(answer.getStatus())) {
            // 如果正在进行中，继续使用现有记录
            log.info("继续进行中的笔试，申请ID: {}", applicationId);
        }

        examInfo.setStatus("in_progress");
        examInfo.setStartTime(answer.getStartTime());

        return examInfo;
    }

    @Override
    @Transactional
    public WrittenTestExamDTO.TestResultDTO submitAnswers(WrittenTestExamDTO.SubmitAnswersDTO submitData) {
        log.info("提交笔试答案，申请ID: {}", submitData.getApplicationId());

        try {
            // 1. 获取答题记录
            WrittenTestAnswer answer = writtenTestAnswerRepository.findByApplicationIdAndTestResultId(
                    submitData.getApplicationId(), submitData.getTestResultId());

            if (answer == null) {
                throw new RuntimeException("未找到答题记录");
            }

            if ("completed".equals(answer.getStatus())) {
                throw new RuntimeException("已经提交过答案，无法重复提交");
            }

            // 2. 获取正确答案
            WrittenTestResult testResult = writtenTestResultRepository.selectById(submitData.getTestResultId());
            if (testResult == null) {
                throw new RuntimeException("未找到笔试题目");
            }

            // 3. 批改答案
            TestGradingResult gradingResult = gradeAnswers(testResult.getQuestionsJson(), submitData.getAnswers());

            // 4. 更新答题记录
            answer.setAnswersJson(JSON.toJSONString(submitData.getAnswers()));
            answer.setScore(gradingResult.getScore());
            answer.setCorrectAnswers(gradingResult.getCorrectCount());
            answer.setWrongAnswers(gradingResult.getWrongCount());
            answer.setCompletionTime(submitData.getCompletionTime());
            answer.setSubmitTime(LocalDateTime.now());
            answer.setStatus("completed");

            writtenTestAnswerRepository.updateById(answer);

            // 5. 构建返回结果
            return buildTestResult(answer, testResult, gradingResult);

        } catch (Exception e) {
            log.error("提交笔试答案失败", e);
            throw new RuntimeException("提交答案失败: " + e.getMessage());
        }
    }

    @Override
    public WrittenTestExamDTO.TestResultDTO getTestResult(Integer applicationId) {
        log.info("获取笔试结果，申请ID: {}", applicationId);

        WrittenTestAnswer answer = writtenTestAnswerRepository.findByApplicationId(applicationId);
        if (answer == null || !"completed".equals(answer.getStatus())) {
            throw new RuntimeException("未找到完成的笔试结果");
        }

        WrittenTestResult testResult = writtenTestResultRepository.selectById(answer.getWrittenTestResultId());
        if (testResult == null) {
            throw new RuntimeException("未找到笔试题目");
        }

        // 重新计算结果（用于显示详细信息）
        List<WrittenTestExamDTO.UserAnswerDTO> userAnswers = JSON.parseArray(
                answer.getAnswersJson(), WrittenTestExamDTO.UserAnswerDTO.class);
        TestGradingResult gradingResult = gradeAnswers(testResult.getQuestionsJson(), userAnswers);

        return buildTestResult(answer, testResult, gradingResult);
    }

    @Override
    public WrittenTestAnswer getTestStatus(Integer applicationId) {
        return writtenTestAnswerRepository.findByApplicationId(applicationId);
    }

    @Override
    @Transactional
    public WrittenTestExamDTO restartExam(Integer applicationId) {
        log.info("重新开始笔试，申请ID: {}", applicationId);

        // 删除现有答题记录
        writtenTestAnswerRepository.deleteByApplicationId(applicationId);

        // 重新开始考试
        return startExam(applicationId);
    }

    /**
     * 解析题目用于考试（不包含答案）
     */
    private List<WrittenTestExamDTO.ExamQuestionDTO> parseQuestionsForExam(String questionsJson) {
        List<WrittenTestExamDTO.ExamQuestionDTO> examQuestions = new ArrayList<>();

        try {
            JSONObject questionsObj = JSON.parseObject(questionsJson);
            JSONArray questionsArray = questionsObj.getJSONArray("questions");

            for (int i = 0; i < questionsArray.size(); i++) {
                JSONObject questionObj = questionsArray.getJSONObject(i);

                WrittenTestExamDTO.ExamQuestionDTO examQuestion = WrittenTestExamDTO.ExamQuestionDTO.builder()
                        .questionId(questionObj.getString("questionId"))
                        .type(questionObj.getString("type"))
                        .build();

                if ("choice".equals(examQuestion.getType())) {
                    examQuestion.setQuestionContent(questionObj.getString("questionContent"));
                    examQuestion.setOptionA(questionObj.getString("A"));
                    examQuestion.setOptionB(questionObj.getString("B"));
                    examQuestion.setOptionC(questionObj.getString("C"));
                    examQuestion.setOptionD(questionObj.getString("D"));
                } else if ("true_false".equals(examQuestion.getType())) {
                    examQuestion.setStatement(questionObj.getString("statement"));
                }

                examQuestions.add(examQuestion);
            }
        } catch (Exception e) {
            log.error("解析题目失败", e);
            throw new RuntimeException("题目格式错误");
        }

        return examQuestions;
    }

    /**
     * 批改答案
     */
    private TestGradingResult gradeAnswers(String questionsJson, List<WrittenTestExamDTO.UserAnswerDTO> userAnswers) {
        TestGradingResult result = new TestGradingResult();
        List<WrittenTestExamDTO.TestResultDTO.QuestionResultDTO> questionResults = new ArrayList<>();

        try {
            JSONObject questionsObj = JSON.parseObject(questionsJson);
            JSONArray questionsArray = questionsObj.getJSONArray("questions");

            int correctCount = 0;
            int totalQuestions = questionsArray.size();

            for (int i = 0; i < questionsArray.size(); i++) {
                JSONObject questionObj = questionsArray.getJSONObject(i);
                String questionId = questionObj.getString("questionId");
                String correctAnswer = questionObj.getString("answer");

                // 找到用户答案
                String userAnswer = userAnswers.stream()
                        .filter(ua -> questionId.equals(ua.getQuestionId()))
                        .map(WrittenTestExamDTO.UserAnswerDTO::getUserAnswer)
                        .findFirst()
                        .orElse("");

                boolean isCorrect = isAnswerCorrect(correctAnswer, userAnswer, questionObj.getString("type"));
                if (isCorrect) {
                    correctCount++;
                }

                // 构建题目结果
                WrittenTestExamDTO.TestResultDTO.QuestionResultDTO questionResult = 
                    WrittenTestExamDTO.TestResultDTO.QuestionResultDTO.builder()
                        .questionId(questionId)
                        .type(questionObj.getString("type"))
                        .correctAnswer(correctAnswer)
                        .userAnswer(userAnswer)
                        .isCorrect(isCorrect)
                        .explanation(questionObj.getString("explanation"))
                        .build();

                if ("choice".equals(questionResult.getType())) {
                    questionResult.setQuestionContent(questionObj.getString("questionContent"));
                    questionResult.setOptionA(questionObj.getString("A"));
                    questionResult.setOptionB(questionObj.getString("B"));
                    questionResult.setOptionC(questionObj.getString("C"));
                    questionResult.setOptionD(questionObj.getString("D"));
                } else if ("true_false".equals(questionResult.getType())) {
                    questionResult.setStatement(questionObj.getString("statement"));
                }

                questionResults.add(questionResult);
            }

            // 计算分数
            BigDecimal score = BigDecimal.valueOf(correctCount)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalQuestions), 2, RoundingMode.HALF_UP);

            result.setScore(score);
            result.setCorrectCount(correctCount);
            result.setWrongCount(totalQuestions - correctCount);
            result.setQuestionResults(questionResults);

        } catch (Exception e) {
            log.error("批改答案失败", e);
            throw new RuntimeException("批改答案失败");
        }

        return result;
    }

    /**
     * 判断答案是否正确（支持不同格式的答案比较）
     */
    private boolean isAnswerCorrect(String correctAnswer, String userAnswer, String questionType) {
        if (correctAnswer == null || userAnswer == null) {
            return false;
        }

        // 对于判断题，进行灵活的答案比较
        if ("true_false".equals(questionType)) {
            return isTrueFalseAnswerCorrect(correctAnswer, userAnswer);
        }

        // 对于选择题，进行大小写不敏感的比较
        if ("choice".equals(questionType)) {
            return correctAnswer.trim().equalsIgnoreCase(userAnswer.trim());
        }

        // 默认情况下进行严格比较
        return correctAnswer.equals(userAnswer);
    }

    /**
     * 判断题答案比较（支持多种格式）
     */
    private boolean isTrueFalseAnswerCorrect(String correctAnswer, String userAnswer) {
        // 标准化正确答案
        boolean correctValue = normalizeBoolean(correctAnswer);
        // 标准化用户答案
        boolean userValue = normalizeBoolean(userAnswer);

        return correctValue == userValue;
    }

    /**
     * 将各种布尔值表示标准化为boolean
     */
    private boolean normalizeBoolean(String answer) {
        if (answer == null) {
            return false;
        }

        String normalized = answer.trim().toLowerCase();

        // 支持的"真"值格式
        return "true".equals(normalized) ||
               "t".equals(normalized) ||
               "yes".equals(normalized) ||
               "y".equals(normalized) ||
               "1".equals(normalized) ||
               "正确".equals(normalized) ||
               "对".equals(normalized);
    }

    /**
     * 构建测试结果
     */
    private WrittenTestExamDTO.TestResultDTO buildTestResult(WrittenTestAnswer answer, 
                                                           WrittenTestResult testResult, 
                                                           TestGradingResult gradingResult) {
        return WrittenTestExamDTO.TestResultDTO.builder()
                .id(answer.getId())
                .applicationId(answer.getApplicationId())
                .testResultId(answer.getWrittenTestResultId())
                .score(answer.getScore())
                .totalQuestions(answer.getTotalQuestions())
                .correctAnswers(answer.getCorrectAnswers())
                .wrongAnswers(answer.getWrongAnswers())
                .completionTime(answer.getCompletionTime())
                .startTime(answer.getStartTime())
                .submitTime(answer.getSubmitTime())
                .status(answer.getStatus())
                .questionResults(gradingResult.getQuestionResults())
                .build();
    }

    /**
     * 批改结果内部类
     */
    private static class TestGradingResult {
        private BigDecimal score;
        private int correctCount;
        private int wrongCount;
        private List<WrittenTestExamDTO.TestResultDTO.QuestionResultDTO> questionResults;

        // getters and setters
        public BigDecimal getScore() { return score; }
        public void setScore(BigDecimal score) { this.score = score; }
        public int getCorrectCount() { return correctCount; }
        public void setCorrectCount(int correctCount) { this.correctCount = correctCount; }
        public int getWrongCount() { return wrongCount; }
        public void setWrongCount(int wrongCount) { this.wrongCount = wrongCount; }
        public List<WrittenTestExamDTO.TestResultDTO.QuestionResultDTO> getQuestionResults() { return questionResults; }
        public void setQuestionResults(List<WrittenTestExamDTO.TestResultDTO.QuestionResultDTO> questionResults) { this.questionResults = questionResults; }
    }
}
