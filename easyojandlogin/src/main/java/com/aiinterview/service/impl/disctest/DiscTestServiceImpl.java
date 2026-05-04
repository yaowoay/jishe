package com.aiinterview.service.impl.disctest;

import com.aiinterview.model.dto.disctest.DiscTestRequest;
import com.aiinterview.model.dto.disctest.DiscTestResponse;
import com.aiinterview.model.entity.disctest.DiscQuestion;
import com.aiinterview.model.entity.disctest.DiscTestRecord;
import com.aiinterview.model.entity.disctest.DiscTestResult;
import com.aiinterview.mapper.DiscTestMapper;
import com.aiinterview.service.disctest.DiscTestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * DISC测试服务实现类
 */
@Slf4j
@Service
public class DiscTestServiceImpl implements DiscTestService {
    
    @Autowired
    private DiscTestMapper discTestMapper;
    
    @Override
    public DiscTestResponse startTest(Long userId) {
        // 生成测试会话ID
        String testSession = "DISC_" + System.currentTimeMillis() + "_" + 
                           (userId != null ? userId : "GUEST");
        
        // 创建测试记录
        DiscTestRecord record = new DiscTestRecord();
        // 如果userId为null，使用默认的用户ID 1 (GUEST用户)
        record.setUserId(userId != null ? userId : 1L);
        record.setTestSession(testSession);
        record.setTestStatus("in_progress");
        record.setStartTime(LocalDateTime.now());
        record.setTotalQuestions(40); // 完整的40题DISC测试
        record.setAnsweredQuestions(0);
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        
        discTestMapper.insert(record);
        
        // 获取所有题目
        List<DiscQuestion> allQuestions = discTestMapper.getAllQuestions();
        
        // 按题目组分组
        Map<Integer, List<DiscQuestion>> questionGroups = allQuestions.stream()
                .collect(Collectors.groupingBy(DiscQuestion::getQuestionGroup));
        
        // 构建响应
        DiscTestResponse response = new DiscTestResponse();
        response.setTestSession(testSession);
        response.setTestStatus("in_progress");
        
        List<DiscTestResponse.DiscQuestionGroup> questionGroupList = new ArrayList<>();
        
        // 获取全部40题
        for (int i = 1; i <= 40; i++) {
            List<DiscQuestion> groupQuestions = questionGroups.get(i);
            if (groupQuestions != null) {
                DiscTestResponse.DiscQuestionGroup group = new DiscTestResponse.DiscQuestionGroup();
                group.setQuestionGroup(i);
                
                List<DiscTestResponse.DiscOption> options = groupQuestions.stream()
                        .map(q -> {
                            DiscTestResponse.DiscOption option = new DiscTestResponse.DiscOption();
                            option.setOptionLetter(q.getOptionLetter());
                            option.setOptionText(q.getOptionText());
                            return option;
                        })
                        .collect(Collectors.toList());
                
                group.setOptions(options);
                questionGroupList.add(group);
            }
        }
        
        response.setQuestions(questionGroupList);
        
        log.info("DISC测试开始，会话ID: {}, 用户ID: {}", testSession, userId);
        return response;
    }
    
    @Override
    @Transactional
    public DiscTestResponse submitTest(DiscTestRequest request) {
        try {
            // 获取测试记录
            DiscTestRecord record = discTestMapper.getRecordBySession(request.getTestSession());
            if (record == null) {
                throw new RuntimeException("测试会话不存在: " + request.getTestSession());
            }
            
            // 保存答案并计算得分
            DiscTestResponse.DiscResult result = calculateDiscScores(request);
            
            // 保存答案到数据库
            for (DiscTestRequest.DiscAnswer answer : request.getAnswers()) {
                String discType = discTestMapper.getDiscTypeByOption(
                        answer.getQuestionGroup(), answer.getSelectedOption());
                
                discTestMapper.saveTestAnswer(record.getRecordId(), 
                        answer.getQuestionGroup(), answer.getSelectedOption(), discType);
            }
            
            // 更新测试记录状态
            record.setTestStatus("completed");
            record.setCompleteTime(LocalDateTime.now());
            record.setAnsweredQuestions(request.getAnswers().size());
            record.setUpdatedAt(LocalDateTime.now());
            discTestMapper.updateById(record);
            
            // 保存测试结果
            DiscTestResult testResult = new DiscTestResult();
            testResult.setRecordId(record.getRecordId());
            // 确保使用相同的用户ID
            testResult.setUserId(record.getUserId());
            testResult.setDScore(result.getScores().get("D"));
            testResult.setIScore(result.getScores().get("I"));
            testResult.setSScore(result.getScores().get("S"));
            testResult.setCScore(result.getScores().get("C"));
            testResult.setPrimaryType(result.getPrimaryType());
            testResult.setSecondaryType(result.getSecondaryType());
            testResult.setPersonalityProfile(result.getPersonalityProfile());
            testResult.setDetailedAnalysis(result.getDetailedAnalysis());
            testResult.setStrengths(result.getStrengths());
            testResult.setWeaknesses(result.getWeaknesses());
            testResult.setCareerSuggestions(result.getCareerSuggestions());
            testResult.setCreatedAt(LocalDateTime.now());
            
            discTestMapper.saveTestResult(testResult);
            
            // 构建响应
            DiscTestResponse response = new DiscTestResponse();
            response.setTestSession(request.getTestSession());
            response.setTestStatus("completed");
            response.setResult(result);
            
            log.info("DISC测试完成，会话ID: {}, 主要类型: {}", 
                    request.getTestSession(), result.getPrimaryType());
            
            return response;
            
        } catch (Exception e) {
            log.error("DISC测试提交失败，会话ID: {}", request.getTestSession(), e);
            throw new RuntimeException("测试提交失败: " + e.getMessage());
        }
    }
    
    @Override
    public DiscTestResponse getTestResult(String testSession) {
        DiscTestRecord record = discTestMapper.getRecordBySession(testSession);
        if (record == null) {
            return null;
        }
        
        DiscTestResult result = discTestMapper.getResultByRecordId(record.getRecordId());
        if (result == null) {
            return null;
        }
        
        return buildResponseFromResult(testSession, result);
    }
    
    @Override
    public DiscTestResponse getLatestTestResult(Long userId) {
        DiscTestResult result = discTestMapper.getLatestResultByUserId(userId);
        if (result == null) {
            return null;
        }

        DiscTestRecord record = discTestMapper.selectById(result.getRecordId());
        return buildResponseFromResult(record.getTestSession(), result);
    }

    @Override
    public List<DiscTestResponse> getTestHistory(Long userId) {
        List<DiscTestResult> results = discTestMapper.getTestHistoryByUserId(userId);
        List<DiscTestResponse> responses = new ArrayList<>();

        for (DiscTestResult result : results) {
            DiscTestRecord record = discTestMapper.selectById(result.getRecordId());
            if (record != null) {
                DiscTestResponse response = buildResponseFromResult(record.getTestSession(), result);
                responses.add(response);
            }
        }

        return responses;
    }
    
    @Override
    public DiscTestResponse.DiscResult calculateDiscScores(DiscTestRequest request) {
        // 统计各类型得分
        Map<String, Integer> scores = new HashMap<>();
        scores.put("D", 0);
        scores.put("I", 0);
        scores.put("S", 0);
        scores.put("C", 0);
        
        // 计算得分
        for (DiscTestRequest.DiscAnswer answer : request.getAnswers()) {
            String discType = discTestMapper.getDiscTypeByOption(
                    answer.getQuestionGroup(), answer.getSelectedOption());
            if (discType != null) {
                scores.put(discType, scores.get(discType) + 1);
            }
        }
        
        // 确定主要类型和次要类型
        List<Map.Entry<String, Integer>> sortedScores = scores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());
        
        String primaryType = sortedScores.get(0).getKey();
        String secondaryType = sortedScores.get(1).getKey();
        String personalityProfile = primaryType + secondaryType;
        
        // 获取类型描述
        Map<String, Object> typeDesc = discTestMapper.getTypeDescription(primaryType);
        
        DiscTestResponse.DiscTypeDescription typeDescription = new DiscTestResponse.DiscTypeDescription();
        if (typeDesc != null) {
            typeDescription.setTypeName((String) typeDesc.get("type_name"));
            typeDescription.setTypeTitle((String) typeDesc.get("type_title"));
            typeDescription.setDescription((String) typeDesc.get("description"));
            typeDescription.setCharacteristics((String) typeDesc.get("characteristics"));
            typeDescription.setWorkStyle((String) typeDesc.get("work_style"));
            typeDescription.setCommunicationStyle((String) typeDesc.get("communication_style"));
            typeDescription.setManagementTips((String) typeDesc.get("management_tips"));
            typeDescription.setCareerFields((String) typeDesc.get("career_fields"));
        }
        
        // 生成详细分析
        String detailedAnalysis = generateDetailedAnalysis(primaryType, secondaryType, scores);
        String strengths = (String) typeDesc.get("strengths");
        String weaknesses = (String) typeDesc.get("weaknesses");
        String careerSuggestions = (String) typeDesc.get("career_fields");
        
        // 构建结果
        DiscTestResponse.DiscResult result = new DiscTestResponse.DiscResult();
        result.setScores(scores);
        result.setPrimaryType(primaryType);
        result.setSecondaryType(secondaryType);
        result.setPersonalityProfile(personalityProfile);
        result.setTypeDescription(typeDescription);
        result.setDetailedAnalysis(detailedAnalysis);
        result.setStrengths(strengths);
        result.setWeaknesses(weaknesses);
        result.setCareerSuggestions(careerSuggestions);
        result.setCompleteTime(LocalDateTime.now());
        
        return result;
    }
    
    /**
     * 从数据库结果构建响应对象
     */
    private DiscTestResponse buildResponseFromResult(String testSession, DiscTestResult result) {
        Map<String, Integer> scores = new HashMap<>();
        scores.put("D", result.getDScore());
        scores.put("I", result.getIScore());
        scores.put("S", result.getSScore());
        scores.put("C", result.getCScore());
        
        // 获取类型描述
        Map<String, Object> typeDesc = discTestMapper.getTypeDescription(result.getPrimaryType());
        
        DiscTestResponse.DiscTypeDescription typeDescription = new DiscTestResponse.DiscTypeDescription();
        if (typeDesc != null) {
            typeDescription.setTypeName((String) typeDesc.get("type_name"));
            typeDescription.setTypeTitle((String) typeDesc.get("type_title"));
            typeDescription.setDescription((String) typeDesc.get("description"));
            typeDescription.setCharacteristics((String) typeDesc.get("characteristics"));
            typeDescription.setWorkStyle((String) typeDesc.get("work_style"));
            typeDescription.setCommunicationStyle((String) typeDesc.get("communication_style"));
            typeDescription.setManagementTips((String) typeDesc.get("management_tips"));
            typeDescription.setCareerFields((String) typeDesc.get("career_fields"));
        }
        
        DiscTestResponse.DiscResult discResult = new DiscTestResponse.DiscResult();
        discResult.setScores(scores);
        discResult.setPrimaryType(result.getPrimaryType());
        discResult.setSecondaryType(result.getSecondaryType());
        discResult.setPersonalityProfile(result.getPersonalityProfile());
        discResult.setTypeDescription(typeDescription);
        discResult.setDetailedAnalysis(result.getDetailedAnalysis());
        discResult.setStrengths(result.getStrengths());
        discResult.setWeaknesses(result.getWeaknesses());
        discResult.setCareerSuggestions(result.getCareerSuggestions());
        discResult.setCompleteTime(result.getCreatedAt());
        
        DiscTestResponse response = new DiscTestResponse();
        response.setTestSession(testSession);
        response.setTestStatus("completed");
        response.setResult(discResult);
        
        return response;
    }
    
    /**
     * 生成详细分析报告
     */
    private String generateDetailedAnalysis(String primaryType, String secondaryType, 
                                          Map<String, Integer> scores) {
        StringBuilder analysis = new StringBuilder();
        
        analysis.append("根据您的DISC性格测试结果，您的主要性格类型是").append(primaryType)
                .append("型，次要类型是").append(secondaryType).append("型。\n\n");
        
        analysis.append("各维度得分情况：\n");
        analysis.append("D（支配型）: ").append(scores.get("D")).append("分\n");
        analysis.append("I（影响型）: ").append(scores.get("I")).append("分\n");
        analysis.append("S（稳定型）: ").append(scores.get("S")).append("分\n");
        analysis.append("C（谨慎型）: ").append(scores.get("C")).append("分\n\n");
        
        // 根据得分高低给出分析
        int primaryScore = scores.get(primaryType);
        if (primaryScore >= 15) {
            analysis.append("您在").append(primaryType).append("型特征上表现非常突出，");
        } else if (primaryScore >= 10) {
            analysis.append("您在").append(primaryType).append("型特征上表现明显，");
        } else {
            analysis.append("您的性格类型相对均衡，");
        }
        
        analysis.append("这表明您具有该类型的典型特征和行为模式。");
        
        return analysis.toString();
    }
}
