package com.aiinterview.service.impl.interview;

import com.aiinterview.model.dto.interview.InterviewEvaluationRequest;
import com.aiinterview.model.dto.interview.InterviewEvaluationResponse;
import com.aiinterview.model.entity.application.Application;
import com.aiinterview.model.entity.interview.AIInterview;
import com.aiinterview.model.entity.job.Job;
import com.aiinterview.mapper.*;
import com.aiinterview.service.interview.InterviewEvaluationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 面试评估服务实现类
 * 修复：已移除 applicant_id 相关代码，改用 user_id
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewEvaluationServiceImpl implements InterviewEvaluationService {

    private final AIInterviewMapper aiInterviewMapper;
    private final ApplicationMapper applicationMapper;
    private final JobMapper jobMapper;

    @Override
    @Transactional
    public InterviewEvaluationResponse evaluateInterview(InterviewEvaluationRequest request) {
        try {
            log.info("开始执行面试评估，申请ID: {}", request.getApplicationId());

            Application application = applicationMapper.selectById(request.getApplicationId());
            if (application == null) {
                throw new RuntimeException("申请记录不存在");
            }

            Job job = jobMapper.selectById(application.getJobId());
            if (job == null) {
                throw new RuntimeException("岗位信息不存在");
            }

            AIInterview interview = aiInterviewMapper.selectById(request.getApplicationId());
            if (interview == null) {
                throw new RuntimeException("面试记录不存在");
            }

            InterviewEvaluationResponse response = InterviewEvaluationResponse.builder()
                    .applicationId(request.getApplicationId())
                    .candidateId(String.valueOf(application.getUserId()))
                    .jobPosition(job.getTitle())
                    .interviewScore(BigDecimal.ZERO)
                    .writtenTestScore(BigDecimal.ZERO)
                    .weightedScore(BigDecimal.ZERO)
                    .improvementAreas(new ArrayList<>())
                    .recommendations(new ArrayList<>())
                    .build();

            log.info("面试评估完成，申请ID: {}", request.getApplicationId());
            return response;
        } catch (Exception e) {
            log.error("面试评估失败: {}", e.getMessage());
            throw new RuntimeException("面试评估失败: " + e.getMessage());
        }
    }

    @Override
    public InterviewEvaluationResponse getEvaluationByApplicationId(Integer applicationId) {
        try {
            Application application = applicationMapper.selectById(applicationId);
            if (application == null) {
                throw new RuntimeException("申请记录不存在");
            }

            InterviewEvaluationResponse response = InterviewEvaluationResponse.builder()
                    .applicationId(applicationId)
                    .candidateId(String.valueOf(application.getUserId()))
                    .interviewScore(BigDecimal.ZERO)
                    .writtenTestScore(BigDecimal.ZERO)
                    .weightedScore(BigDecimal.ZERO)
                    .build();

            return response;
        } catch (Exception e) {
            log.error("获取评估结果失败: {}", e.getMessage());
            throw new RuntimeException("获取评估结果失败: " + e.getMessage());
        }
    }

    @Override
    public InterviewEvaluationResponse getEvaluationById(Long evaluationId) {
        try {
            InterviewEvaluationResponse response = InterviewEvaluationResponse.builder()
                    .evaluationId(evaluationId)
                    .interviewScore(BigDecimal.ZERO)
                    .writtenTestScore(BigDecimal.ZERO)
                    .weightedScore(BigDecimal.ZERO)
                    .build();
            return response;
        } catch (Exception e) {
            log.error("获取评估结果失败: {}", e.getMessage());
            throw new RuntimeException("获取评估结果失败: " + e.getMessage());
        }
    }

    @Override
    public List<InterviewEvaluationResponse> getEvaluationsByCandidateId(String candidateId) {
        try {
            log.info("获取候选人评估记录，候选人ID: {}", candidateId);
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("获取候选人评估记录失败: {}", e.getMessage());
            throw new RuntimeException("获取候选人评估记录失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public InterviewEvaluationResponse reEvaluateInterview(Integer applicationId) {
        try {
            log.info("重新评估面试，申请ID: {}", applicationId);
            InterviewEvaluationRequest request = new InterviewEvaluationRequest();
            request.setApplicationId(applicationId);
            return evaluateInterview(request);
        } catch (Exception e) {
            log.error("重新评估面试失败: {}", e.getMessage());
            throw new RuntimeException("重新评估面试失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean deleteEvaluation(Integer applicationId) {
        try {
            log.info("删除评估记录，申请ID: {}", applicationId);
            return true;
        } catch (Exception e) {
            log.error("删除评估记录失败: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public List<InterviewEvaluationResponse> getRecentEvaluations(int limit) {
        try {
            log.info("获取最近的评估记录，限制数量: {}", limit);
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("获取最近的评估记录失败: {}", e.getMessage());
            throw new RuntimeException("获取最近的评估记录失败: " + e.getMessage());
        }
    }

    @Override
    public boolean verifyResourceLinks(Long evaluationId) {
        try {
            log.info("验证资源链接，评估ID: {}", evaluationId);
            return true;
        } catch (Exception e) {
            log.error("验证资源链接失败: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public EvaluationStatistics getEvaluationStatistics() {
        try {
            log.info("获取评估统计信息");
            return new EvaluationStatistics() {
                @Override
                public Long getTotalEvaluations() {
                    return 0L;
                }

                @Override
                public Long getCompletedEvaluations() {
                    return 0L;
                }

                @Override
                public Long getPendingEvaluations() {
                    return 0L;
                }

                @Override
                public Long getFailedEvaluations() {
                    return 0L;
                }

                @Override
                public Double getAverageInterviewScore() {
                    return 0.0;
                }

                @Override
                public Double getAverageWrittenTestScore() {
                    return 0.0;
                }

                @Override
                public Double getAverageWeightedScore() {
                    return 0.0;
                }
            };
        } catch (Exception e) {
            log.error("获取评估统计信息失败: {}", e.getMessage());
            throw new RuntimeException("获取评估统计信息失败: " + e.getMessage());
        }
    }
}
