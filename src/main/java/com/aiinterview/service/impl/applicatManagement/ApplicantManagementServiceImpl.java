package com.aiinterview.service.impl.applicatManagement;

import com.aiinterview.mapper.*;
import com.aiinterview.model.dto.applicant.ApplicantDetailDTO;
import com.aiinterview.model.entity.application.Application;
import com.aiinterview.model.entity.applicant.Applicant;
import com.aiinterview.model.entity.job.Job;
import com.aiinterview.model.entity.interview.AIInterview;
import com.aiinterview.model.entity.interview.InterviewScores;
import com.aiinterview.model.entity.resume.ResumeScores;
import com.aiinterview.model.entity.report.Report;
import com.aiinterview.model.entity.company.Company;
import com.aiinterview.service.applicatManagement.ApplicantManagementService;
import com.aiinterview.service.application.ApplicationService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 应聘者管理服务实现类 - 基于 route_application.py 重新设计
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicantManagementServiceImpl implements ApplicantManagementService {

    private final ApplicationMapper applicationMapper;
    private final ApplicationService applicationService;
    private final ApplicantMapper applicantMapper;
    private final JobMapper jobMapper;
    private final AIInterviewMapper aiInterviewMapper;
    private final InterviewScoresMapper interviewScoresMapper;
    private final ResumeMapper resumeMapper;
    private final ResumeScoresMapper resumeScoresMapper;
    private final ReportMapper reportMapper;
    private final CompanyMapper companyMapper;

    // ==================== 求职者端功能实现 ====================

    @Override
    public List<Map<String, Object>> getRecentInterviews(Long userId, Integer limit) {
        try {
            log.info("获取用户最近面试记录: userId={}, limit={}", userId, limit);

            // 获取申请人信息
            Applicant applicant = getApplicantByUserId(userId);
            if (applicant == null) {
                throw new RuntimeException("用户未找到对应的申请人");
            }

            // 设置默认限制数量
            if (limit == null || limit <= 0) {
                limit = 5;
            }

            // 查询最近的面试记录，包含AI面试总分
            QueryWrapper<Application> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("applicant_id", applicant.getApplicantId())
                       .orderByDesc("apply_time")
                       .last("LIMIT " + limit);

            List<Application> applications = applicationMapper.selectList(queryWrapper);

            List<Map<String, Object>> interviews = new ArrayList<>();
            for (Application application : applications) {
                try {
                    Job job = jobMapper.selectById(application.getJobId());
                    Company company = job != null ? companyMapper.selectById(job.getCompanyId()) : null;

                    // 查询AI面试总分
                    QueryWrapper<AIInterview> aiQueryWrapper = new QueryWrapper<>();
                    aiQueryWrapper.eq("application_id", application.getApplicationId())
                                 .orderByDesc("interview_id")
                                 .last("LIMIT 1");
                    AIInterview aiInterview = aiInterviewMapper.selectOne(aiQueryWrapper);

                    Map<String, Object> interview = new HashMap<>();
                    interview.put("application_id", application.getApplicationId());
                    interview.put("job_title", job != null ? job.getTitle() : "未知职位");
                    interview.put("company_name", company != null ? company.getCompanyName() : "未知公司");
                    interview.put("apply_time", application.getApplyTime());
                    interview.put("status", application.getStatus());
                    interview.put("ai_evaluation_score", aiInterview != null ? aiInterview.getOverallScore() : null);

                    interviews.add(interview);
                } catch (Exception e) {
                    log.warn("处理面试记录失败: applicationId={}, error={}", application.getApplicationId(), e.getMessage());
                }
            }

            log.info("获取到 {} 条最近面试记录", interviews.size());
            return interviews;
        } catch (Exception e) {
            log.error("获取最近面试记录失败: userId={}, error={}", userId, e.getMessage());
            throw new RuntimeException("获取面试情况失败: " + e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getAllInterviews(Long userId) {
        try {
            log.info("获取用户全部面试记录: userId={}", userId);

            // 获取申请人信息
            Applicant applicant = getApplicantByUserId(userId);
            if (applicant == null) {
                throw new RuntimeException("用户未找到对应的申请人");
            }

            // 查询全部面试记录
            QueryWrapper<Application> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("applicant_id", applicant.getApplicantId())
                       .orderByDesc("apply_time");

            List<Application> applications = applicationMapper.selectList(queryWrapper);

            List<Map<String, Object>> interviews = new ArrayList<>();
            for (Application application : applications) {
                try {
                    Job job = jobMapper.selectById(application.getJobId());
                    Company company = job != null ? companyMapper.selectById(job.getCompanyId()) : null;

                    Map<String, Object> interview = new HashMap<>();
                    interview.put("application_id", application.getApplicationId());
                    interview.put("job_title", job != null ? job.getTitle() : "未知职位");
                    interview.put("expiration_date", job != null ? job.getExpirationDate() : null);
                    interview.put("company_name", company != null ? company.getCompanyName() : "未知公司");
                    interview.put("apply_time", application.getApplyTime());
                    interview.put("status", application.getStatus());
                    interview.put("ai_evaluation_score", application.getAiEvaluationScore());

                    interviews.add(interview);
                } catch (Exception e) {
                    log.warn("处理面试记录失败: applicationId={}, error={}", application.getApplicationId(), e.getMessage());
                }
            }

            log.info("获取到 {} 条全部面试记录", interviews.size());
            return interviews;
        } catch (Exception e) {
            log.error("获取全部面试记录失败: userId={}, error={}", userId, e.getMessage());
            throw new RuntimeException("获取面试情况失败: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> enterInterviewRoom(Long userId, Long applicationId) {
        try {
            log.info("用户进入面试房间: userId={}, applicationId={}", userId, applicationId);

            // 验证申请记录是否存在且属于该用户
            Application application = applicationService.getApplicationById(applicationId, userId);
            if (application == null) {
                throw new RuntimeException("未找到面试记录或无权限访问");
            }

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "成功进入面试房间");
            result.put("application_id", applicationId);
            result.put("status", application.getStatus());

            log.info("用户成功进入面试房间: userId={}, applicationId={}", userId, applicationId);
            return result;
        } catch (Exception e) {
            log.error("进入面试房间失败: userId={}, applicationId={}, error={}", userId, applicationId, e.getMessage());
            throw new RuntimeException("获取面试房间数据失败: " + e.getMessage());
        }
    }

    // ==================== 企业端功能实现 ====================

    @Override
    public Page<ApplicantDetailDTO> getApplicantsByCompany(Long companyId, int current, int size, String status, String keyword) {
        return applicationService.getApplicantsByCompany(companyId, current, size, status, keyword);
    }

    @Override
    public Map<String, Object> getJobDetailWithApplicants(Long jobId, Long companyId) {
        try {
            log.info("获取职位详情和申请者信息: jobId={}, companyId={}", jobId, companyId);

            // 验证职位是否属于该公司
            Job job = jobMapper.selectById(jobId);
            if (job == null || !job.getCompanyId().equals(companyId)) {
                throw new RuntimeException("职位未找到或无权限访问");
            }

            // 获取该职位的所有申请
            QueryWrapper<Application> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("job_id", jobId);
            List<Application> applications = applicationMapper.selectList(queryWrapper);

            List<Map<String, Object>> applicantsInfo = new ArrayList<>();

            for (Application application : applications) {
                try {
                    Applicant applicant = applicantMapper.selectById(application.getApplicantId());
                    com.aiinterview.model.entity.resume.Resume resume = resumeMapper.selectById(application.getResumeId());

                    // 查询简历分析报告
                    QueryWrapper<Report> reportQuery = new QueryWrapper<>();
                    reportQuery.eq("job_id", jobId).eq("resume_id", application.getResumeId());
                    Report report = reportMapper.selectOne(reportQuery);

                    if (report == null) {
                        log.warn("申请者必须先进行简历分析才能投递岗位: applicationId={}", application.getApplicationId());
                        continue;
                    }

                    // 查询简历评分
                    QueryWrapper<ResumeScores> scoresQuery = new QueryWrapper<>();
                    scoresQuery.eq("report_id", report.getReportId());
                    ResumeScores resumeScores = resumeScoresMapper.selectOne(scoresQuery);

                    Map<String, Object> applicantInfo = new HashMap<>();
                    applicantInfo.put("applicant_id", applicant.getApplicantId());
                    applicantInfo.put("full_name", applicant.getFullName());
                    applicantInfo.put("resume", resume != null ? resume.getResumeId() : null);
                    applicantInfo.put("status", application.getStatus());
                    applicantInfo.put("application_id", application.getApplicationId());

                    if (resumeScores != null) {
                        applicantInfo.put("total_score", resumeScores.getTotalScore());
                        applicantInfo.put("tech_match", resumeScores.getTechMatch());
                        applicantInfo.put("experience_match", resumeScores.getExperienceMatch());
                        applicantInfo.put("education_match", resumeScores.getEducationMatch());
                        applicantInfo.put("potential_match", resumeScores.getPotentialMatch());
                    }

                    applicantsInfo.add(applicantInfo);
                } catch (Exception e) {
                    log.warn("处理申请者信息失败: applicationId={}, error={}", application.getApplicationId(), e.getMessage());
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("job", job);
            result.put("applicants", applicantsInfo);

            log.info("获取到职位详情和 {} 个申请者信息", applicantsInfo.size());
            return result;
        } catch (Exception e) {
            log.error("获取职位详情失败: jobId={}, companyId={}, error={}", jobId, companyId, e.getMessage());
            throw new RuntimeException("获取职位详情失败: " + e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getCandidates(Long jobId, Boolean showAll, Long companyId) {
        try {
            log.info("获取候选人列表: jobId={}, showAll={}, companyId={}", jobId, showAll, companyId);

            // 验证职位是否属于该公司
            Job job = jobMapper.selectById(jobId);
            if (job == null || !job.getCompanyId().equals(companyId)) {
                throw new RuntimeException("职位未找到或无权限访问");
            }

            // 查询状态为"待定"的申请记录
            QueryWrapper<Application> appQuery = new QueryWrapper<>();
            appQuery.eq("job_id", jobId).eq("status", "待定");
            List<Application> applications = applicationMapper.selectList(appQuery);

            List<Map<String, Object>> candidatesData = new ArrayList<>();

            for (Application application : applications) {
                try {
                    // 查询面试信息
                    QueryWrapper<AIInterview> interviewQuery = new QueryWrapper<>();
                    interviewQuery.eq("application_id", application.getApplicationId())
                                 .orderByDesc("interview_id")
                                 .last("LIMIT 1");
                    AIInterview interview = aiInterviewMapper.selectOne(interviewQuery);

                    if (interview == null || interview.getOverallScore() == null) {
                        continue;
                    }

                    // 如果不显示全部且分数低于60分，跳过
                    if (!Boolean.TRUE.equals(showAll) && interview.getOverallScore() < 60) {
                        continue;
                    }

                    // 查询面试评分
                    QueryWrapper<InterviewScores> scoresQuery = new QueryWrapper<>();
                    scoresQuery.eq("interview_id", interview.getInterviewId());
                    InterviewScores interviewScores = interviewScoresMapper.selectOne(scoresQuery);

                    if (interviewScores == null) {
                        continue;
                    }

                    // 查询申请人信息
                    Applicant applicant = applicantMapper.selectById(application.getApplicantId());
                    if (applicant == null) {
                        continue;
                    }

                    Map<String, Object> candidate = new HashMap<>();
                    candidate.put("application_id", application.getApplicationId());
                    candidate.put("applicant_id", applicant.getApplicantId());
                    candidate.put("full_name", applicant.getFullName());
                    candidate.put("overall_score", interview.getOverallScore());
                    candidate.put("interview_scores", interviewScores);
                    candidate.put("job_title", job.getTitle());

                    candidatesData.add(candidate);
                } catch (Exception e) {
                    log.warn("处理候选人信息失败: applicationId={}, error={}", application.getApplicationId(), e.getMessage());
                }
            }

            log.info("获取到 {} 个候选人", candidatesData.size());
            return candidatesData;
        } catch (Exception e) {
            log.error("获取候选人列表失败: jobId={}, error={}", jobId, e.getMessage());
            throw new RuntimeException("获取候选人列表失败: " + e.getMessage());
        }
    }

    @Override
    public ApplicantDetailDTO getApplicantDetail(Long applicationId, Long companyId) {
        return applicationService.getApplicantDetail(applicationId, companyId);
    }

    // ==================== 状态管理功能实现 ====================

    @Override
    @Transactional
    public boolean markApplicationAsViewed(Long applicationId, Long companyId) {
        try {
            log.info("标记申请为已查看: applicationId={}, companyId={}", applicationId, companyId);

            // 验证申请是否属于该公司
            if (!isApplicationBelongsToCompany(applicationId, companyId)) {
                throw new RuntimeException("申请记录不存在或无权限访问");
            }

            // 获取申请记录
            Application application = applicationMapper.selectById(applicationId);
            if (application == null) {
                throw new RuntimeException("申请记录未找到");
            }

            // 更新状态为"已查看"
            application.setStatus("已查看");
            application.setUpdatedAt(LocalDateTime.now());

            int result = applicationMapper.updateById(application);

            if (result > 0) {
                log.info("申请状态已更新为已查看: applicationId={}", applicationId);
            }

            return result > 0;
        } catch (Exception e) {
            log.error("标记申请为已查看失败: applicationId={}, companyId={}, error={}",
                     applicationId, companyId, e.getMessage());
            throw new RuntimeException("标记申请为已查看失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean sendInterviewInvitation(Long applicationId, String interviewTime, String notes, Long companyId) {
        try {
            log.info("发送面试邀请: applicationId={}, companyId={}, interviewTime={}", applicationId, companyId, interviewTime);

            // 验证申请是否属于该公司
            if (!isApplicationBelongsToCompany(applicationId, companyId)) {
                throw new RuntimeException("申请记录不存在或无权限访问");
            }

            // 获取申请记录
            Application application = applicationMapper.selectById(applicationId);
            if (application == null) {
                throw new RuntimeException("申请记录未找到");
            }

            // 更新状态为"待面试"
            application.setStatus("待面试");

            // 设置面试时间到interview_time字段
            if (interviewTime != null && !interviewTime.trim().isEmpty()) {
                try {
                    // 解析时间字符串为 LocalDateTime
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    LocalDateTime parsedDateTime = LocalDateTime.parse(interviewTime, formatter);
                    application.setInterviewTime(parsedDateTime);
                    log.info("面试时间设置成功: {}", parsedDateTime);
                } catch (Exception e) {
                    log.warn("解析面试时间失败: {}, 错误: {}", interviewTime, e.getMessage());
                }
            }

            // 如果有备注，可以设置到notes字段或其他合适的字段
            if (notes != null && !notes.trim().isEmpty()) {
                // 这里可以根据需要设置备注信息
                log.info("面试备注: {}", notes);
            }

            application.setUpdatedAt(LocalDateTime.now());

            int result = applicationMapper.updateById(application);

            if (result > 0) {
                log.info("面试邀请发送成功: applicationId={}", applicationId);
                // TODO: 这里可以添加发送邮件或短信通知的逻辑
            }

            return result > 0;
        } catch (Exception e) {
            log.error("发送面试邀请失败: applicationId={}, companyId={}, error={}",
                     applicationId, companyId, e.getMessage());
            throw new RuntimeException("发送面试邀请失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean rejectApplication(Long applicationId, String reason, Long companyId) {
        try {
            log.info("拒绝申请: applicationId={}, companyId={}", applicationId, companyId);

            // 验证申请是否属于该公司
            if (!isApplicationBelongsToCompany(applicationId, companyId)) {
                throw new RuntimeException("申请记录不存在或无权限访问");
            }

            // 获取申请记录
            Application application = applicationMapper.selectById(applicationId);
            if (application == null) {
                throw new RuntimeException("申请记录未找到");
            }

            // 更新状态为"淘汰"
            application.setStatus("淘汰");
            // 设置拒绝原因到rejection_reason字段
            application.setRejectionReason(reason);
            application.setUpdatedAt(LocalDateTime.now());

            int result = applicationMapper.updateById(application);

            if (result > 0) {
                log.info("申请拒绝成功: applicationId={}", applicationId);
                // TODO: 这里可以添加发送拒绝通知的逻辑
            }

            return result > 0;
        } catch (Exception e) {
            log.error("拒绝申请失败: applicationId={}, companyId={}, error={}",
                     applicationId, companyId, e.getMessage());
            throw new RuntimeException("拒绝申请失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean acceptApplication(Long applicationId, String feedback, Long companyId) {
        try {
            log.info("录用申请: applicationId={}, companyId={}", applicationId, companyId);

            // 验证申请是否属于该公司
            if (!isApplicationBelongsToCompany(applicationId, companyId)) {
                throw new RuntimeException("申请记录不存在或无权限访问");
            }

            // 获取申请记录
            Application application = applicationMapper.selectById(applicationId);
            if (application == null) {
                throw new RuntimeException("申请记录未找到");
            }

            // 构建录用反馈信息
            StringBuilder feedbackBuilder = new StringBuilder();
            feedbackBuilder.append("🎉 恭喜您被录用！\n");
            if (feedback != null && !feedback.trim().isEmpty()) {
                feedbackBuilder.append("📝 录用说明: ").append(feedback);
            } else {
                feedbackBuilder.append("📝 欢迎加入我们的团队！");
            }

            // 更新状态为"录用"
            application.setStatus("录用");
            application.setFeedback(feedbackBuilder.toString());
            application.setUpdatedAt(LocalDateTime.now());

            int result = applicationMapper.updateById(application);

            if (result > 0) {
                log.info("申请录用成功: applicationId={}", applicationId);
                // TODO: 这里可以添加发送录用通知的逻辑
            }

            return result > 0;
        } catch (Exception e) {
            log.error("录用申请失败: applicationId={}, companyId={}, error={}",
                     applicationId, companyId, e.getMessage());
            throw new RuntimeException("录用申请失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean updateApplicationStatus(Long applicationId, String status, String feedback, Long companyId) {
        return applicationService.updateApplicationStatusByCompany(applicationId, status, feedback, companyId);
    }

    // ==================== 批量操作和统计功能 ====================

    @Override
    @Transactional
    public boolean batchUpdateApplications(List<Long> applicationIds, String status, String feedback, Long companyId) {
        try {
            log.info("批量更新申请状态: applicationIds={}, status={}, companyId={}", applicationIds, status, companyId);

            int successCount = 0;
            for (Long applicationId : applicationIds) {
                try {
                    if (updateApplicationStatus(applicationId, status, feedback, companyId)) {
                        successCount++;
                    }
                } catch (Exception e) {
                    log.warn("批量更新申请失败: applicationId={}, error={}", applicationId, e.getMessage());
                }
            }

            log.info("批量更新申请完成: 总数={}, 成功={}", applicationIds.size(), successCount);
            return successCount > 0;
        } catch (Exception e) {
            log.error("批量更新申请失败: {}", e.getMessage());
            throw new RuntimeException("批量更新申请失败: " + e.getMessage());
        }
    }

//    @Override
//    public Map<String, Object> getApplicationStatistics(Long companyId) {
//        try {
//            log.info("获取申请统计信息: companyId={}", companyId);
//
//            Map<String, Object> statistics = new HashMap<>();
//
//            // 获取各状态的申请数量
//            Integer pending = applicationMapper.countByCompanyAndStatus(companyId, "已投递");
//            Integer reviewed = applicationMapper.countByCompanyAndStatus(companyId, "已查看");
//            Integer interview = applicationMapper.countByCompanyAndStatus(companyId, "面试中");
//            Integer accepted = applicationMapper.countByCompanyAndStatus(companyId, "录用");
//            Integer rejected = applicationMapper.countByCompanyAndStatus(companyId, "拒绝");
//
//            statistics.put("pending", pending != null ? pending : 0);
//            statistics.put("reviewed", reviewed != null ? reviewed : 0);
//            statistics.put("interview", interview != null ? interview : 0);
//            statistics.put("accepted", accepted != null ? accepted : 0);
//            statistics.put("rejected", rejected != null ? rejected : 0);
//
//            // 计算总数
//            int total = (pending != null ? pending : 0) +
//                       (reviewed != null ? reviewed : 0) +
//                       (interview != null ? interview : 0) +
//                       (accepted != null ? accepted : 0) +
//                       (rejected != null ? rejected : 0);
//            statistics.put("total", total);
//
//            log.info("申请统计信息: {}", statistics);
//            return statistics;
//        } catch (Exception e) {
//            log.error("获取申请统计失败: companyId={}, error={}", companyId, e.getMessage());
//            throw new RuntimeException("获取申请统计失败: " + e.getMessage());
//        }
//    }
    @Override
    public Map<String, Object> getApplicationStatistics(Long companyId) {
        try {
            log.info("获取申请统计信息: companyId={}", companyId);

            // 验证公司ID
            if (companyId == null) {
                throw new RuntimeException("公司ID不能为空");
            }

            Map<String, Object> statistics = new HashMap<>();

            // 添加空值检查和默认值处理
            try {
                // 待处理：包括pending、已投递等初始状态
                Integer pending1 = applicationMapper.countByCompanyAndStatus(companyId, "pending");
                Integer pending2 = applicationMapper.countByCompanyAndStatus(companyId, "已投递");
                Integer pending = (pending1 != null ? pending1 : 0) + (pending2 != null ? pending2 : 0);

                // 已查看：包括已查看、待笔试、待面试等处理中状态
                Integer reviewed1 = applicationMapper.countByCompanyAndStatus(companyId, "已查看");
                Integer reviewed2 = applicationMapper.countByCompanyAndStatus(companyId, "待笔试");
                Integer reviewed3 = applicationMapper.countByCompanyAndStatus(companyId, "待面试");
                Integer reviewed = (reviewed1 != null ? reviewed1 : 0) +
                                 (reviewed2 != null ? reviewed2 : 0) +
                                 (reviewed3 != null ? reviewed3 : 0);

                // 面试中：包括面试中、面试进行中等状态
                Integer interview1 = applicationMapper.countByCompanyAndStatus(companyId, "面试中");
                Integer interview2 = applicationMapper.countByCompanyAndStatus(companyId, "面试进行中");
                Integer interview = (interview1 != null ? interview1 : 0) + (interview2 != null ? interview2 : 0);

                // 已录用
                Integer accepted = applicationMapper.countByCompanyAndStatus(companyId, "录用");

                // 已拒绝：包括拒绝、淘汰等状态
                Integer rejected1 = applicationMapper.countByCompanyAndStatus(companyId, "拒绝");
                Integer rejected2 = applicationMapper.countByCompanyAndStatus(companyId, "淘汰");
                Integer rejected = (rejected1 != null ? rejected1 : 0) + (rejected2 != null ? rejected2 : 0);

                statistics.put("pending", pending);
                statistics.put("reviewed", reviewed);
                statistics.put("interview", interview);
                statistics.put("accepted", accepted != null ? accepted : 0);
                statistics.put("rejected", rejected);

                // 计算总数
                int total = pending + reviewed + interview +
                           (accepted != null ? accepted : 0) + rejected;
                statistics.put("total", total);

                log.info("申请统计信息获取成功: {}", statistics);
                return statistics;

            } catch (Exception dbError) {
                log.error("数据库查询失败: {}", dbError.getMessage());
                // 返回默认统计数据
                statistics.put("pending", 0);
                statistics.put("reviewed", 0);
                statistics.put("interview", 0);
                statistics.put("accepted", 0);
                statistics.put("rejected", 0);
                statistics.put("total", 0);
                return statistics;
            }

        } catch (Exception e) {
            log.error("获取申请统计失败: companyId={}, error={}", companyId, e.getMessage(), e);
            throw new RuntimeException("获取申请统计失败: " + e.getMessage());
        }
    }
    @Override
    public Map<String, Object> getInterviewStatistics(Long companyId) {
        try {
            log.info("获取面试统计信息: companyId={}", companyId);

            Map<String, Object> statistics = new HashMap<>();

            // 查询该公司所有职位的面试统计
            QueryWrapper<Job> jobQuery = new QueryWrapper<>();
            jobQuery.eq("company_id", companyId);
            List<Job> jobs = jobMapper.selectList(jobQuery);

            List<Long> jobIds = jobs.stream().map(Job::getJobId).collect(Collectors.toList());

            if (!jobIds.isEmpty()) {
                // 统计面试相关数据
                QueryWrapper<Application> appQuery = new QueryWrapper<>();
                appQuery.in("job_id", jobIds);

                // 总申请数
                Long totalApplications = applicationMapper.selectCount(appQuery);

                // 面试中的申请数
                QueryWrapper<Application> interviewQuery = new QueryWrapper<>();
                interviewQuery.in("job_id", jobIds).eq("status", "面试中");
                Long interviewingCount = applicationMapper.selectCount(interviewQuery);

                // 已完成面试的申请数
                QueryWrapper<Application> completedQuery = new QueryWrapper<>();
                completedQuery.in("job_id", jobIds).in("status", Arrays.asList("录用", "拒绝"));
                Long completedCount = applicationMapper.selectCount(completedQuery);

                statistics.put("total_applications", totalApplications);
                statistics.put("interviewing", interviewingCount);
                statistics.put("completed", completedCount);
                statistics.put("pending_review", totalApplications - interviewingCount - completedCount);
            } else {
                statistics.put("total_applications", 0L);
                statistics.put("interviewing", 0L);
                statistics.put("completed", 0L);
                statistics.put("pending_review", 0L);
            }

            log.info("面试统计信息: {}", statistics);
            return statistics;
        } catch (Exception e) {
            log.error("获取面试统计失败: companyId={}, error={}", companyId, e.getMessage());
            throw new RuntimeException("获取面试统计失败: " + e.getMessage());
        }
    }

    // ==================== 兼容性方法实现 ====================

    @Override
    @Transactional
    public boolean hireApplicant(Long applicationId, String feedback, Long companyId) {
        // 兼容旧接口，直接调用新的 acceptApplication 方法
        return acceptApplication(applicationId, feedback, companyId);
    }

    @Override
    @Transactional
    public boolean rejectHire(Long applicationId, String reason, Long companyId) {
        try {
            log.info("拒绝录用: applicationId={}, companyId={}", applicationId, companyId);

            String feedback = reason != null && !reason.trim().isEmpty()
                ? "很遗憾，您未能通过最终录用环节。原因: " + reason
                : "很遗憾，您未能通过最终录用环节。";

            boolean result = updateApplicationStatus(applicationId, "reject_hire", feedback, companyId);

            if (result) {
                log.info("拒绝录用成功: applicationId={}, reason={}", applicationId, reason);
            }

            return result;
        } catch (Exception e) {
            log.error("拒绝录用失败: applicationId={}, companyId={}, error={}",
                     applicationId, companyId, e.getMessage());
            throw new RuntimeException("拒绝录用失败: " + e.getMessage());
        }
    }

    // ==================== 辅助方法 ====================

    /**
     * 根据用户ID获取申请人信息
     */
    private Applicant getApplicantByUserId(Long userId) {
        try {
            QueryWrapper<Applicant> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            return applicantMapper.selectOne(queryWrapper);
        } catch (Exception e) {
            log.error("根据用户ID获取申请人失败: userId={}, error={}", userId, e.getMessage());
            throw new RuntimeException("获取申请人信息失败");
        }
    }

    /**
     * 验证申请是否属于该公司
     */
    private boolean isApplicationBelongsToCompany(Long applicationId, Long companyId) {
        try {
            Integer count = applicationMapper.countApplicationByCompany(applicationId, companyId);
            return count != null && count > 0;
        } catch (Exception e) {
            log.error("验证申请归属失败: applicationId={}, companyId={}, error={}",
                     applicationId, companyId, e.getMessage());
            return false;
        }
    }

    /**
     * 验证职位是否属于该公司
     */
    private boolean isJobBelongsToCompany(Long jobId, Long companyId) {
        try {
            Job job = jobMapper.selectById(jobId);
            return job != null && job.getCompanyId().equals(companyId);
        } catch (Exception e) {
            log.error("验证职位归属失败: jobId={}, companyId={}, error={}",
                     jobId, companyId, e.getMessage());
            return false;
        }
    }

    /**
     * 构建标准化的反馈信息
     */
    private String buildFeedbackMessage(String action, String content) {
        StringBuilder builder = new StringBuilder();

        switch (action) {
            case "viewed":
                builder.append("👁️ 申请已被查看");
                break;
            case "interview":
                builder.append("📧 面试邀请已发送");
                break;
            case "reject":
                builder.append("❌ 申请已被拒绝");
                break;
            case "accept":
                builder.append("🎉 恭喜您被录用！");
                break;
            default:
                builder.append("📝 状态已更新");
        }

        if (content != null && !content.trim().isEmpty()) {
            builder.append("\n📝 ").append(content);
        }

        return builder.toString();
    }
}
