package com.aiinterview.service.impl.interview;

import com.aiinterview.model.dto.interview.InterviewEvaluationRequest;
import com.aiinterview.model.dto.interview.InterviewEvaluationResponse;
import com.aiinterview.model.entity.resourceRecomment.ResourceRecommendation;
import com.aiinterview.model.entity.application.Application;
import com.aiinterview.model.entity.interview.AIInterview;
import com.aiinterview.model.entity.interview.InterviewEvaluation;
import com.aiinterview.model.entity.job.Job;
import com.aiinterview.model.entity.writtenTest.WrittenTestAnswer;
import com.aiinterview.repository.interview.InterviewEvaluationRepository;
import com.aiinterview.repository.resourceRecomment.ResourceRecommendationRepository;
import com.aiinterview.repository.writtenTest.WrittenTestAnswerRepository;
import com.aiinterview.service.interview.InterviewEvaluationService;
import com.aiinterview.service.api.DifyService;
import com.aiinterview.constants.WorkflowType;
import com.aiinterview.mapper.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 面试评估服务实现类
 */
@Slf4j
@Service
public class InterviewEvaluationServiceImpl implements InterviewEvaluationService {
    
    @Autowired
    private InterviewEvaluationRepository interviewEvaluationRepository;
    
    @Autowired
    private ResourceRecommendationRepository resourceRecommendationRepository;
    
    @Autowired
    private AIInterviewMapper aiInterviewMapper;
    
    @Autowired
    private ApplicationMapper applicationMapper;
    
    @Autowired
    private JobMapper jobMapper;
    
    @Autowired
    private WrittenTestAnswerRepository writtenTestAnswerRepository;
    
    @Autowired
    private DifyService difyService;
    
    @Override
    @Transactional
    public InterviewEvaluationResponse evaluateInterview(InterviewEvaluationRequest request) {
        log.info("开始执行面试评估，申请ID: {}", request.getApplicationId());
        
        try {
            // 1. 检查是否已有评估结果
            if (!request.getForceReEvaluate()) {
                InterviewEvaluation existingEvaluation = interviewEvaluationRepository.findByApplicationId(request.getApplicationId());
                if (existingEvaluation != null && "completed".equals(existingEvaluation.getEvaluationStatus())) {
                    log.info("已存在评估结果，直接返回，申请ID: {}", request.getApplicationId());
                    return convertToResponse(existingEvaluation);
                }
            }
            
            // 2. 获取申请信息
            Application application = getApplicationById(request.getApplicationId());
            if (application == null) {
                throw new RuntimeException("申请记录不存在");
            }
            
            // 3. 获取岗位信息
            Job job = getJobById(application.getJobId());
            String jobPosition = job != null ? job.getTitle() : "未知岗位";
            
            // 4. 获取面试记录
            AIInterview aiInterview = getAIInterviewByApplicationId(request.getApplicationId());
            String msReply = extractMsReplyFromHistory(aiInterview);
            log.info("面试记录获取结果 - 申请ID: {}, 面试记录存在: {}, 面试回答长度: {}",
                request.getApplicationId(), aiInterview != null, msReply.length());

            // 5. 获取笔试记录
            WrittenTestAnswer writtenTestAnswer = getWrittenTestAnswerByApplicationId(request.getApplicationId());
            String bsReply = extractBsReplyFromAnswers(writtenTestAnswer);
            log.info("笔试记录获取结果 - 申请ID: {}, 笔试记录存在: {}, 笔试回答长度: {}",
                request.getApplicationId(), writtenTestAnswer != null, bsReply.length());
            
            // 6. 调用Dify API进行评估
            Map<String, Object> difyRequest = buildDifyRequest(msReply, bsReply, jobPosition);
            log.info("构建Dify请求 - 申请ID: {}, 岗位: {}, 面试回答为空: {}, 笔试回答为空: {}",
                request.getApplicationId(), jobPosition, msReply.isEmpty(), bsReply.isEmpty());

            Map<String, Object> difyResponse = callDifyEvaluationAPI(difyRequest);
            log.info("Dify API调用结果 - 申请ID: {}, 响应为空: {}",
                request.getApplicationId(), difyResponse == null);

            // 7. 解析Dify响应
            EvaluationResult evaluationResult = parseDifyResponse(difyResponse);
            log.info("评估结果解析 - 申请ID: {}, 解析成功: {}, 面试得分: {}, 笔试得分: {}, 综合得分: {}",
                request.getApplicationId(), evaluationResult != null,
                evaluationResult != null ? evaluationResult.getInterviewScore() : "null",
                evaluationResult != null ? evaluationResult.getWrittenTestScore() : "null",
                evaluationResult != null ? evaluationResult.getWeightedScore() : "null");
            
            // 8. 保存评估结果
            InterviewEvaluation evaluation = saveEvaluationResult(request, evaluationResult, application, aiInterview, writtenTestAnswer);
            
            // 9. 保存推荐资源
            saveResourceRecommendations(evaluation, evaluationResult.getResources());
            
            log.info("面试评估完成，申请ID: {}, 评估ID: {}", request.getApplicationId(), evaluation.getId());
            return convertToResponse(evaluation);
            
        } catch (Exception e) {
            log.error("面试评估失败，申请ID: {}", request.getApplicationId(), e);
            
            // 保存失败状态
            saveFailedEvaluationStatus(request.getApplicationId(), e.getMessage());
            
            throw new RuntimeException("面试评估失败: " + e.getMessage());
        }
    }
    
    @Override
    public InterviewEvaluationResponse getEvaluationByApplicationId(Integer applicationId) {
        log.info("获取评估结果，申请ID: {}", applicationId);
        
        InterviewEvaluation evaluation = interviewEvaluationRepository.findByApplicationId(applicationId);
        if (evaluation == null) {
            throw new RuntimeException("未找到评估记录");
        }
        
        return convertToResponse(evaluation);
    }
    
    @Override
    public InterviewEvaluationResponse getEvaluationById(Long evaluationId) {
        log.info("获取评估结果，评估ID: {}", evaluationId);
        
        InterviewEvaluation evaluation = interviewEvaluationRepository.selectById(evaluationId);
        if (evaluation == null) {
            throw new RuntimeException("未找到评估记录");
        }
        
        return convertToResponse(evaluation);
    }
    
    @Override
    public List<InterviewEvaluationResponse> getEvaluationsByCandidateId(String candidateId) {
        log.info("获取候选人评估记录，候选人ID: {}", candidateId);
        
        List<InterviewEvaluation> evaluations = interviewEvaluationRepository.findByCandidateId(candidateId);
        return evaluations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public InterviewEvaluationResponse reEvaluateInterview(Integer applicationId) {
        log.info("重新评估面试，申请ID: {}", applicationId);
        
        InterviewEvaluationRequest request = InterviewEvaluationRequest.builder()
                .applicationId(applicationId)
                .forceReEvaluate(true)
                .evaluationType("full")
                .build();
        
        return evaluateInterview(request);
    }
    
    @Override
    @Transactional
    public boolean deleteEvaluation(Integer applicationId) {
        log.info("删除评估记录，申请ID: {}", applicationId);
        
        try {
            // 删除推荐资源
            resourceRecommendationRepository.deleteByApplicationId(applicationId);
            
            // 删除评估记录
            int deletedCount = interviewEvaluationRepository.deleteByApplicationId(applicationId);
            
            log.info("删除评估记录完成，申请ID: {}, 删除数量: {}", applicationId, deletedCount);
            return deletedCount > 0;
            
        } catch (Exception e) {
            log.error("删除评估记录失败，申请ID: {}", applicationId, e);
            return false;
        }
    }
    
    @Override
    public List<InterviewEvaluationResponse> getRecentEvaluations(int limit) {
        log.info("获取最近评估记录，限制数量: {}", limit);
        
        List<InterviewEvaluation> evaluations = interviewEvaluationRepository.findRecentEvaluations(limit);
        return evaluations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean verifyResourceLinks(Long evaluationId) {
        log.info("验证资源链接，评估ID: {}", evaluationId);
        
        List<ResourceRecommendation> resources = resourceRecommendationRepository.findByInterviewEvaluationId(evaluationId);
        
        // TODO: 实现资源链接验证逻辑
        // 这里可以添加HTTP请求验证链接是否可访问
        
        return true;
    }
    
    @Override
    public EvaluationStatistics getEvaluationStatistics() {
        log.info("获取评估统计信息");
        
        EvaluationStatistics statistics = new EvaluationStatistics();
        
        // TODO: 实现统计逻辑
        statistics.setTotalEvaluations(interviewEvaluationRepository.selectCount(null));
        statistics.setCompletedEvaluations(interviewEvaluationRepository.countByStatus("completed"));
        statistics.setPendingEvaluations(interviewEvaluationRepository.countByStatus("pending"));
        statistics.setFailedEvaluations(interviewEvaluationRepository.countByStatus("failed"));
        
        return statistics;
    }
    
    /**
     * 获取申请记录
     */
    private Application getApplicationById(Integer applicationId) {
        return applicationMapper.selectById(applicationId);
    }

    /**
     * 获取岗位记录
     */
    private Job getJobById(Long jobId) {
        return jobMapper.selectById(jobId);
    }

    /**
     * 根据申请ID获取AI面试记录
     */
    private AIInterview getAIInterviewByApplicationId(Integer applicationId) {
        // 使用MyBatis-Plus查询
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<AIInterview> queryWrapper =
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        queryWrapper.eq("application_id", applicationId);
        queryWrapper.orderByDesc("created_at");
        queryWrapper.last("LIMIT 1");

        return aiInterviewMapper.selectOne(queryWrapper);
    }

    /**
     * 根据申请ID获取笔试答案记录
     */
    private WrittenTestAnswer getWrittenTestAnswerByApplicationId(Integer applicationId) {
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<WrittenTestAnswer> queryWrapper =
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        queryWrapper.eq("application_id", applicationId);
        queryWrapper.orderByDesc("created_at");
        queryWrapper.last("LIMIT 1");

        return writtenTestAnswerRepository.selectOne(queryWrapper);
    }

    /**
     * 从面试历史记录中提取面试回答
     */
    private String extractMsReplyFromHistory(AIInterview aiInterview) {
        if (aiInterview == null || aiInterview.getHistory() == null) {
            return "";
        }

        try {
            JSONObject historyJson = JSON.parseObject(aiInterview.getHistory());

            // 提取对话列表中的用户回答
            StringBuilder msReply = new StringBuilder();

            if (historyJson.containsKey("dialogList")) {
                JSONArray dialogList = historyJson.getJSONArray("dialogList");
                for (int i = 0; i < dialogList.size(); i++) {
                    JSONObject dialog = dialogList.getJSONObject(i);
                    if ("user".equals(dialog.getString("role"))) {
                        msReply.append("问题: ").append(getPreviousAIMessage(dialogList, i)).append("\n");
                        msReply.append("回答: ").append(dialog.getString("text")).append("\n\n");
                    }
                }
            }

            // 如果没有对话列表，尝试从answers字段提取
            if (msReply.length() == 0 && historyJson.containsKey("answers")) {
                JSONArray answers = historyJson.getJSONArray("answers");
                for (int i = 0; i < answers.size(); i++) {
                    JSONObject answer = answers.getJSONObject(i);
                    msReply.append("回答 ").append(i + 1).append(": ").append(answer.getString("answer")).append("\n");
                }
            }

            return msReply.toString();

        } catch (Exception e) {
            log.warn("解析面试历史记录失败，申请ID: {}", aiInterview.getApplicationId(), e);
            return aiInterview.getHistory(); // 返回原始数据
        }
    }

    /**
     * 获取对话列表中前一条AI消息（作为问题）
     */
    private String getPreviousAIMessage(JSONArray dialogList, int currentIndex) {
        for (int i = currentIndex - 1; i >= 0; i--) {
            JSONObject dialog = dialogList.getJSONObject(i);
            if ("ai".equals(dialog.getString("role"))) {
                return dialog.getString("text");
            }
        }
        return "未知问题";
    }

    /**
     * 从笔试答案中提取笔试回答
     */
    private String extractBsReplyFromAnswers(WrittenTestAnswer writtenTestAnswer) {
        if (writtenTestAnswer == null || writtenTestAnswer.getAnswersJson() == null) {
            return "";
        }

        try {
            JSONArray answersArray = JSON.parseArray(writtenTestAnswer.getAnswersJson());
            StringBuilder bsReply = new StringBuilder();

            for (int i = 0; i < answersArray.size(); i++) {
                JSONObject answerObj = answersArray.getJSONObject(i);

                // 提取问题和答案
                String question = answerObj.getString("question");
                String answer = answerObj.getString("selectedAnswer");

                bsReply.append("问题 ").append(i + 1).append(": ").append(question).append("\n");
                bsReply.append("回答: ").append(answer).append("\n\n");
            }

            return bsReply.toString();

        } catch (Exception e) {
            log.warn("解析笔试答案失败，申请ID: {}", writtenTestAnswer.getApplicationId(), e);
            return writtenTestAnswer.getAnswersJson(); // 返回原始数据
        }
    }

    /**
     * 构建Dify API请求
     */
    private Map<String, Object> buildDifyRequest(String msReply, String bsReply, String jobPosition) {
        Map<String, Object> request = new HashMap<>();

        // 根据Dify工作流的输入变量设置参数
        request.put("bs_reply", bsReply != null ? bsReply : "");
        request.put("ms_reply", msReply != null ? msReply : "");
        request.put("ms_job", jobPosition != null ? jobPosition : "");

        log.info("构建Dify请求参数: bs_reply长度={}, ms_reply长度={}, ms_job={}",
                bsReply != null ? bsReply.length() : 0,
                msReply != null ? msReply.length() : 0,
                jobPosition);

        return request;
    }

    /**
     * 调用Dify评估API
     */
    private Map<String, Object> callDifyEvaluationAPI(Map<String, Object> request) {
        try {
            log.info("调用Dify评估API");

            // 使用DifyService调用评估与推荐工作流
            // 使用WorkflowType枚举来获取正确的工作流配置
            WorkflowType workflowType = WorkflowType.EVALUATION_RECOMMENDATION;
            String response = difyService.runWorkflow(workflowType.getCode(), request);

            // 解析响应
            JSONObject responseJson = JSON.parseObject(response);

            if (responseJson.containsKey("data")) {
                return responseJson.getJSONObject("data").getInnerMap();
            } else {
                throw new RuntimeException("Dify API响应格式错误: " + response);
            }

        } catch (Exception e) {
            log.error("调用Dify评估API失败", e);
            throw new RuntimeException("调用Dify评估API失败: " + e.getMessage());
        }
    }

    /**
     * 解析Dify响应
     */
    private EvaluationResult parseDifyResponse(Map<String, Object> difyResponse) {
        try {
            EvaluationResult result = new EvaluationResult();

            // 解析面试评分
            if (difyResponse.containsKey("ms_answer_score")) {
                Map<String, Object> msScore = (Map<String, Object>) difyResponse.get("ms_answer_score");
                result.setInterviewScore(new BigDecimal(msScore.get("score").toString()));
                result.setInterviewEvaluation(msScore);
            }

            // 解析笔试评分
            if (difyResponse.containsKey("bs_answer_score")) {
                Map<String, Object> bsScore = (Map<String, Object>) difyResponse.get("bs_answer_score");
                result.setWrittenTestScore(new BigDecimal(bsScore.get("score").toString()));
                result.setWrittenTestEvaluation(bsScore);
            }

            // 解析权重后的总分
            if (difyResponse.containsKey("total_answer")) {
                Map<String, Object> totalAnswer = (Map<String, Object>) difyResponse.get("total_answer");
                result.setWeightedScore(new BigDecimal(totalAnswer.get("finalScore").toString()));
            }

            // 解析学习建议和资源推荐
            if (difyResponse.containsKey("study_skills")) {
                Map<String, Object> studySkills = (Map<String, Object>) difyResponse.get("study_skills");

                // 改进领域
                if (studySkills.containsKey("improvementAreas")) {
                    result.setImprovementAreas((List<String>) studySkills.get("improvementAreas"));
                }

                // 学习建议
                if (studySkills.containsKey("recommendations")) {
                    result.setRecommendations((List<String>) studySkills.get("recommendations"));
                }

                // 推荐资源
                if (studySkills.containsKey("resources")) {
                    List<Map<String, Object>> resourcesList = (List<Map<String, Object>>) studySkills.get("resources");
                    result.setResources(resourcesList);
                }
            }

            return result;

        } catch (Exception e) {
            log.error("解析Dify响应失败", e);
            throw new RuntimeException("解析Dify响应失败: " + e.getMessage());
        }
    }

    /**
     * 保存评估结果
     */
    private InterviewEvaluation saveEvaluationResult(InterviewEvaluationRequest request,
                                                   EvaluationResult evaluationResult,
                                                   Application application,
                                                   AIInterview aiInterview,
                                                   WrittenTestAnswer writtenTestAnswer) {

        InterviewEvaluation evaluation = new InterviewEvaluation();
        evaluation.setApplicationId(request.getApplicationId());
        evaluation.setInterviewId(aiInterview != null ? aiInterview.getInterviewId() : null);
        evaluation.setWrittenTestAnswerId(writtenTestAnswer != null ? writtenTestAnswer.getId() : null);
        evaluation.setJobPosition(evaluationResult.getJobPosition());
        evaluation.setCandidateId(application.getApplicantId().toString());
        evaluation.setInterviewScore(evaluationResult.getInterviewScore());
        evaluation.setWrittenTestScore(evaluationResult.getWrittenTestScore());
        evaluation.setWeightedScore(evaluationResult.getWeightedScore());
        evaluation.setInterviewEvaluationJson(JSON.toJSONString(evaluationResult.getInterviewEvaluation()));
        evaluation.setWrittenTestEvaluationJson(JSON.toJSONString(evaluationResult.getWrittenTestEvaluation()));
        evaluation.setImprovementAreas(JSON.toJSONString(evaluationResult.getImprovementAreas()));
        evaluation.setRecommendations(JSON.toJSONString(evaluationResult.getRecommendations()));
        evaluation.setResourcesJson(JSON.toJSONString(evaluationResult.getResources()));
        evaluation.setEvaluationStatus("completed");
        evaluation.setCreatedAt(LocalDateTime.now());
        evaluation.setUpdatedAt(LocalDateTime.now());

        interviewEvaluationRepository.insert(evaluation);

        log.info("保存评估结果成功，评估ID: {}", evaluation.getId());
        return evaluation;
    }

    /**
     * 保存推荐资源
     */
    private void saveResourceRecommendations(InterviewEvaluation evaluation, List<Map<String, Object>> resources) {
        if (resources == null || resources.isEmpty()) {
            return;
        }

        for (int i = 0; i < resources.size(); i++) {
            Map<String, Object> resource = resources.get(i);

            ResourceRecommendation recommendation = new ResourceRecommendation();
            recommendation.setInterviewEvaluationId(evaluation.getId());
            recommendation.setApplicationId(evaluation.getApplicationId());
            recommendation.setResourceName((String) resource.get("name"));
            recommendation.setResourceType((String) resource.get("type"));
            recommendation.setResourceUrl((String) resource.get("url"));
            recommendation.setResourceDescription((String) resource.getOrDefault("description", ""));
            recommendation.setImprovementArea((String) resource.getOrDefault("improvementArea", ""));
            recommendation.setPriority(i + 1); // 按顺序设置优先级
            recommendation.setIsVerified(false); // 默认未验证
            recommendation.setCreatedAt(LocalDateTime.now());
            recommendation.setUpdatedAt(LocalDateTime.now());

            resourceRecommendationRepository.insert(recommendation);
        }

        log.info("保存推荐资源成功，评估ID: {}, 资源数量: {}", evaluation.getId(), resources.size());
    }

    /**
     * 保存失败状态
     */
    private void saveFailedEvaluationStatus(Integer applicationId, String errorMessage) {
        try {
            InterviewEvaluation evaluation = new InterviewEvaluation();
            evaluation.setApplicationId(applicationId);
            evaluation.setEvaluationStatus("failed");
            evaluation.setRecommendations(errorMessage); // 临时存储错误信息
            evaluation.setCreatedAt(LocalDateTime.now());
            evaluation.setUpdatedAt(LocalDateTime.now());

            interviewEvaluationRepository.insert(evaluation);

        } catch (Exception e) {
            log.error("保存失败状态失败，申请ID: {}", applicationId, e);
        }
    }

    /**
     * 转换为响应DTO
     */
    private InterviewEvaluationResponse convertToResponse(InterviewEvaluation evaluation) {
        InterviewEvaluationResponse.InterviewEvaluationResponseBuilder builder = InterviewEvaluationResponse.builder()
                .evaluationId(evaluation.getId())
                .applicationId(evaluation.getApplicationId())
                .candidateId(evaluation.getCandidateId())
                .jobPosition(evaluation.getJobPosition())
                .interviewScore(evaluation.getInterviewScore())
                .writtenTestScore(evaluation.getWrittenTestScore())
                .weightedScore(evaluation.getWeightedScore())
                .evaluationStatus(evaluation.getEvaluationStatus())
                .evaluatedAt(evaluation.getCreatedAt());

        // InterviewEvaluation
        // 解析面试评估详情
//        System.out.println(evaluation);

        if (evaluation.getInterviewEvaluationJson() != null) {
            try {
                JSONObject interviewEval = JSON.parseObject(evaluation.getInterviewEvaluationJson());
                InterviewEvaluationResponse.InterviewScoreDetail interviewDetail =
                    InterviewEvaluationResponse.InterviewScoreDetail.builder()
                        .jobTitle(interviewEval.getString("jobTitle"))
                        .question(interviewEval.getString("question"))
                        .candidateResponse(interviewEval.getString("candidateResponse"))
                        .score(interviewEval.getInteger("score"))
                        .build();
                builder.interviewEvaluation(interviewDetail);
            } catch (Exception e) {
                log.warn("解析面试评估详情失败，评估ID: {}", evaluation.getId(), e);
            }
        }

        // 解析笔试评估详情
        if (evaluation.getWrittenTestEvaluationJson() != null) {
            try {
                JSONObject writtenTestEval = JSON.parseObject(evaluation.getWrittenTestEvaluationJson());
                InterviewEvaluationResponse.WrittenTestScoreDetail writtenTestDetail =
                    InterviewEvaluationResponse.WrittenTestScoreDetail.builder()
                        .jobTitle(writtenTestEval.getString("jobTitle"))
                        .question(writtenTestEval.getString("question"))
                        .candidateResponse(writtenTestEval.getString("candidateResponse"))
                        .score(writtenTestEval.getInteger("score"))
                        .build();
                builder.writtenTestEvaluation(writtenTestDetail);
            } catch (Exception e) {
                log.warn("解析笔试评估详情失败，评估ID: {}", evaluation.getId(), e);
            }
        }

        // 解析改进领域
        if (evaluation.getImprovementAreas() != null) {
            try {
                List<String> improvementAreas = JSON.parseArray(evaluation.getImprovementAreas(), String.class);
                builder.improvementAreas(improvementAreas);
            } catch (Exception e) {
                log.warn("解析改进领域失败，评估ID: {}", evaluation.getId(), e);
            }
        }

        // 解析学习建议
        if (evaluation.getRecommendations() != null) {
            try {
                List<String> recommendations = JSON.parseArray(evaluation.getRecommendations(), String.class);
                builder.recommendations(recommendations);
            } catch (Exception e) {
                log.warn("解析学习建议失败，评估ID: {}", evaluation.getId(), e);
            }
        }

        // 获取推荐资源
        List<ResourceRecommendation> resourceRecommendations =
            resourceRecommendationRepository.findByInterviewEvaluationId(evaluation.getId());

        List<InterviewEvaluationResponse.ResourceDetail> resourceDetails = resourceRecommendations.stream()
                .map(resource -> InterviewEvaluationResponse.ResourceDetail.builder()
                        .name(resource.getResourceName())
                        .type(resource.getResourceType())
                        .url(resource.getResourceUrl())
                        .description(resource.getResourceDescription())
                        .improvementArea(resource.getImprovementArea())
                        .priority(resource.getPriority())
                        .build())
                .collect(Collectors.toList());

        builder.resources(resourceDetails);

        return builder.build();
    }

    /**
     * 评估结果内部类
     */
    private static class EvaluationResult {
        private String jobPosition;
        private BigDecimal interviewScore;
        private BigDecimal writtenTestScore;
        private BigDecimal weightedScore;
        private Map<String, Object> interviewEvaluation;
        private Map<String, Object> writtenTestEvaluation;
        private List<String> improvementAreas;
        private List<String> recommendations;
        private List<Map<String, Object>> resources;

        // Getters and Setters
        public String getJobPosition() { return jobPosition; }
        public void setJobPosition(String jobPosition) { this.jobPosition = jobPosition; }

        public BigDecimal getInterviewScore() { return interviewScore; }
        public void setInterviewScore(BigDecimal interviewScore) { this.interviewScore = interviewScore; }

        public BigDecimal getWrittenTestScore() { return writtenTestScore; }
        public void setWrittenTestScore(BigDecimal writtenTestScore) { this.writtenTestScore = writtenTestScore; }

        public BigDecimal getWeightedScore() { return weightedScore; }
        public void setWeightedScore(BigDecimal weightedScore) { this.weightedScore = weightedScore; }

        public Map<String, Object> getInterviewEvaluation() { return interviewEvaluation; }
        public void setInterviewEvaluation(Map<String, Object> interviewEvaluation) { this.interviewEvaluation = interviewEvaluation; }

        public Map<String, Object> getWrittenTestEvaluation() { return writtenTestEvaluation; }
        public void setWrittenTestEvaluation(Map<String, Object> writtenTestEvaluation) { this.writtenTestEvaluation = writtenTestEvaluation; }

        public List<String> getImprovementAreas() { return improvementAreas; }
        public void setImprovementAreas(List<String> improvementAreas) { this.improvementAreas = improvementAreas; }

        public List<String> getRecommendations() { return recommendations; }
        public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }

        public List<Map<String, Object>> getResources() { return resources; }
        public void setResources(List<Map<String, Object>> resources) { this.resources = resources; }
    }
}
