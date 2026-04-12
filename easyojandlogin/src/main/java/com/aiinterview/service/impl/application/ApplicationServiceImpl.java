package com.aiinterview.service.impl.application;

import com.aiinterview.model.dto.applicant.ApplicantDetailDTO;
import com.aiinterview.model.dto.application.ApplicationDetailDTO;
import com.aiinterview.constants.ApplicationStatus;
import com.aiinterview.model.entity.applicant.Applicant;
import com.aiinterview.model.entity.application.Application;
import com.aiinterview.mapper.ApplicantMapper;
import com.aiinterview.mapper.ApplicationMapper;
import com.aiinterview.service.application.ApplicationService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 简历投递申请服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationMapper applicationMapper;
    private final ApplicantMapper applicantMapper;

    @Override
    @Transactional
    public Application submitApplication(Long userId, Long jobId, Long resumeId) {
        try {
            // 根据userId获取applicantId
            Long applicantId = getApplicantIdByUserId(userId);

            // 检查是否已投递过该职位
            if (hasAppliedForJob(applicantId, jobId)) {
                throw new RuntimeException("您已投递过该职位");
            }

            // 创建申请记录
            Application application = new Application();
            application.setApplicantId(applicantId);  // 使用applicantId而不是userId
            application.setJobId(jobId);
            application.setResumeId(resumeId);
            application.setStatus("已投递");  // 设置状态为已投递
            application.setApplyTime(LocalDateTime.now());

            applicationMapper.insert(application);

            log.info("简历投递成功: 用户ID={}, 求职者ID={}, 职位ID={}, 简历ID={}", userId, applicantId, jobId, resumeId);
            return application;

        } catch (Exception e) {
            log.error("简历投递失败: {}", e.getMessage());
            throw new RuntimeException("简历投递失败: " + e.getMessage());
        }
    }

    @Override
    public List<Application> getApplicationsByUserId(Long userId) {
        try {
            // 根据userId获取applicantId
            Long applicantId = getApplicantIdByUserId(userId);

            QueryWrapper<Application> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("applicant_id", applicantId)
                       .orderByDesc("apply_time");
            return applicationMapper.selectList(queryWrapper);
        } catch (Exception e) {
            log.error("获取投递历史失败: {}", e.getMessage());
            throw new RuntimeException("获取投递历史失败");
        }
    }

    @Override
    public List<Long> getSubmittedJobIds(Long userId) {
        try {
            List<Application> applications = getApplicationsByUserId(userId);
            return applications.stream()
                    .map(Application::getJobId)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取已投递职位列表失败: {}", e.getMessage());
            throw new RuntimeException("获取已投递职位列表失败");
        }
    }

    @Override
    public List<ApplicationDetailDTO> getSubmittedApplicationDetails(Long userId) {
        try {
            log.info("获取用户投递记录详细信息: userId={}", userId);
            List<ApplicationDetailDTO> details = applicationMapper.selectApplicationDetailsByUserId(userId);

            // 为每个记录设置状态的中文显示名称
            details.forEach(detail -> {
                detail.setStatusDisplayName(ApplicationStatus.getDisplayName(detail.getStatus()));
            });

            log.info("获取到 {} 条投递记录", details.size());
            return details;
        } catch (Exception e) {
            log.error("获取投递记录详细信息失败: userId={}, error={}", userId, e.getMessage());
            throw new RuntimeException("获取投递记录详细信息失败: " + e.getMessage());
        }
    }

    @Override
    public Application getApplicationById(Long applicationId, Long userId) {
        try {
            // 根据userId获取applicantId
            Long applicantId = getApplicantIdByUserId(userId);

            QueryWrapper<Application> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("application_id", applicationId)
                       .eq("applicant_id", applicantId);
            return applicationMapper.selectOne(queryWrapper);
        } catch (Exception e) {
            log.error("获取申请详情失败: {}", e.getMessage());
            throw new RuntimeException("获取申请详情失败");
        }
    }

    @Override
    @Transactional
    public boolean withdrawApplication(Long applicationId, Long userId) {
        try {
            Application application = getApplicationById(applicationId, userId);
            if (application == null) {
                throw new RuntimeException("申请记录不存在");
            }

            if (!"pending".equals(application.getStatus())) {
                throw new RuntimeException("只能撤回待处理状态的申请");
            }

            // 删除申请记录
            applicationMapper.deleteById(applicationId);

            log.info("申请撤回成功: ID={}", applicationId);
            return true;
        } catch (Exception e) {
            log.error("撤回申请失败: {}", e.getMessage());
            throw new RuntimeException("撤回申请失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean updateApplicationStatus(Long applicationId, String status, String reviewerNotes) {
        try {
            Application application = applicationMapper.selectById(applicationId);
            if (application == null) {
                throw new RuntimeException("申请记录不存在");
            }

            application.setStatus(status);
            // application.setFeedback(reviewerNotes); // 暂时注释，因为实体类中可能没有这个字段

            applicationMapper.updateById(application);

            log.info("申请状态更新成功: ID={}, 状态={}", applicationId, status);
            return true;
        } catch (Exception e) {
            log.error("更新申请状态失败: {}", e.getMessage());
            throw new RuntimeException("更新申请状态失败: " + e.getMessage());
        }
    }

    @Override
    public boolean hasAppliedForJob(Long applicantId, Long jobId) {
        try {
            QueryWrapper<Application> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("applicant_id", applicantId)
                       .eq("job_id", jobId);
            return applicationMapper.selectCount(queryWrapper) > 0;
        } catch (Exception e) {
            log.error("检查投递状态失败: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public int getApplicationCountByResumeId(Long resumeId) {
        try {
            QueryWrapper<Application> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("resume_id", resumeId);
            return Math.toIntExact(applicationMapper.selectCount(queryWrapper));
        } catch (Exception e) {
            log.error("获取简历投递次数失败: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 根据用户ID获取求职者ID
     */
    private Long getApplicantIdByUserId(Long userId) {
        try {
            QueryWrapper<Applicant> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            Applicant applicant = applicantMapper.selectOne(queryWrapper);

            if (applicant == null) {
                throw new RuntimeException("未找到求职者信息，请先完善个人信息");
            }

            return applicant.getApplicantId();
        } catch (Exception e) {
            log.error("根据用户ID获取求职者ID失败: userId={}, error={}", userId, e.getMessage());
            throw new RuntimeException("获取求职者信息失败: " + e.getMessage());
        }
    }

    // ==================== 企业端方法 ====================

    @Override
    public Page<ApplicantDetailDTO> getApplicantsByCompany(Long companyId, int current, int size, String status, String keyword) {
        try {
            log.info("企业查询应聘者列表: companyId={}, current={}, size={}, status={}, keyword={}",
                    companyId, current, size, status, keyword);

            Page<ApplicantDetailDTO> page = new Page<>(current, size);
            List<ApplicantDetailDTO> applicants = applicationMapper.selectApplicantsByCompany(
                page, companyId, status, keyword
            );
            page.setRecords(applicants);

            log.info("查询到 {} 条应聘者记录", applicants.size());
            return page;
        } catch (Exception e) {
            log.error("企业获取应聘者列表失败: companyId={}, error={}", companyId, e.getMessage());
            throw new RuntimeException("获取应聘者列表失败: " + e.getMessage());
        }
    }

    @Override
    public ApplicantDetailDTO getApplicantDetail(Long applicationId, Long companyId) {
        try {
            log.info("企业查询应聘者详情: applicationId={}, companyId={}", applicationId, companyId);

            ApplicantDetailDTO detail = applicationMapper.selectApplicantDetail(applicationId, companyId);
            if (detail == null) {
                throw new RuntimeException("应聘者信息不存在或无权限查看");
            }

            log.info("查询应聘者详情成功: applicationId={}", applicationId);
            return detail;
        } catch (Exception e) {
            log.error("企业获取应聘者详情失败: applicationId={}, companyId={}, error={}",
                    applicationId, companyId, e.getMessage());
            throw new RuntimeException("获取应聘者详情失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean updateApplicationStatusByCompany(Long applicationId, String status, String feedback, Long companyId) {
        try {
            log.info("企业更新申请状态: applicationId={}, status={}, companyId={}",
                    applicationId, status, companyId);

            // 验证申请是否属于该公司
            Integer count = applicationMapper.countApplicationByCompany(applicationId, companyId);
            if (count == null || count == 0) {
                throw new RuntimeException("申请记录不存在或无权限操作");
            }

            // 更新申请状态
            Application application = applicationMapper.selectById(applicationId);
            if (application == null) {
                throw new RuntimeException("申请记录不存在");
            }

            application.setStatus(status);
            application.setFeedback(feedback);
            application.setUpdatedAt(LocalDateTime.now());

            int result = applicationMapper.updateById(application);

            log.info("企业更新申请状态成功: applicationId={}, status={}", applicationId, status);
            return result > 0;
        } catch (Exception e) {
            log.error("企业更新申请状态失败: applicationId={}, companyId={}, error={}",
                    applicationId, companyId, e.getMessage());
            throw new RuntimeException("更新申请状态失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean setInterviewTime(Long applicationId, LocalDateTime interviewTime, Long companyId) {
        try {
            log.info("设置面试时间: applicationId={}, interviewTime={}, companyId={}",
                    applicationId, interviewTime, companyId);

            // 验证申请是否属于该公司
            Integer count = applicationMapper.countApplicationByCompany(applicationId, companyId);
            if (count == null || count == 0) {
                throw new RuntimeException("申请记录不存在或无权限操作");
            }

            // 更新面试时间
            Application application = applicationMapper.selectById(applicationId);
            if (application == null) {
                throw new RuntimeException("申请记录不存在");
            }

            application.setInterviewTime(interviewTime);
            application.setStatus("interview"); // 设置状态为面试中
            application.setUpdatedAt(LocalDateTime.now());

            int result = applicationMapper.updateById(application);

            log.info("设置面试时间成功: applicationId={}, interviewTime={}", applicationId, interviewTime);
            return result > 0;
        } catch (Exception e) {
            log.error("设置面试时间失败: applicationId={}, companyId={}, error={}",
                    applicationId, companyId, e.getMessage());
            throw new RuntimeException("设置面试时间失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean rejectApplicationWithReason(Long applicationId, String rejectionReason, Long companyId) {
        try {
            log.info("拒绝申请: applicationId={}, rejectionReason={}, companyId={}",
                    applicationId, rejectionReason, companyId);

            // 验证申请是否属于该公司
            Integer count = applicationMapper.countApplicationByCompany(applicationId, companyId);
            if (count == null || count == 0) {
                throw new RuntimeException("申请记录不存在或无权限操作");
            }

            // 更新拒绝原因
            Application application = applicationMapper.selectById(applicationId);
            if (application == null) {
                throw new RuntimeException("申请记录不存在");
            }

            application.setRejectionReason(rejectionReason);
            application.setStatus("淘汰"); // 设置状态为淘汰
            application.setUpdatedAt(LocalDateTime.now());

            int result = applicationMapper.updateById(application);

            log.info("拒绝申请成功: applicationId={}, rejectionReason={}", applicationId, rejectionReason);
            return result > 0;
        } catch (Exception e) {
            log.error("拒绝申请失败: applicationId={}, companyId={}, error={}",
                    applicationId, companyId, e.getMessage());
            throw new RuntimeException("拒绝申请失败: " + e.getMessage());
        }
    }

    @Override
    public Application findByUserAndJobAndResume(Long userId, Long jobId, Long resumeId) {
        try {
            // 根据userId获取applicantId
            Long applicantId = getApplicantIdByUserId(userId);

            QueryWrapper<Application> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("applicant_id", applicantId)
                       .eq("job_id", jobId)
                       .eq("resume_id", resumeId);
            return applicationMapper.selectOne(queryWrapper);
        } catch (Exception e) {
            log.error("查找申请记录失败: userId={}, jobId={}, resumeId={}, error={}",
                     userId, jobId, resumeId, e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional
    public boolean updateApplication(Application application) {
        try {
            application.setUpdatedAt(LocalDateTime.now());
            int result = applicationMapper.updateById(application);
            log.info("申请记录更新成功: applicationId={}", application.getApplicationId());
            return result > 0;
        } catch (Exception e) {
            log.error("更新申请记录失败: applicationId={}, error={}",
                     application.getApplicationId(), e.getMessage());
            return false;
        }
    }
}
