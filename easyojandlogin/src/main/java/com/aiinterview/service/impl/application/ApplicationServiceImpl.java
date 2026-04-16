package com.aiinterview.service.impl.application;

import com.aiinterview.model.dto.applicant.ApplicantDetailDTO;
import com.aiinterview.model.dto.application.ApplicationDetailDTO;
import com.aiinterview.constants.ApplicationStatus;
import com.aiinterview.model.entity.application.Application;
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
 * 修复：已移除所有 applicant_id 相关代码，改用 user_id
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationMapper applicationMapper;

    @Override
    @Transactional
    public Application submitApplication(Long userId, Long jobId, Long resumeId) {
        try {
            if (hasAppliedForJob(userId, jobId)) {
                throw new RuntimeException("您已投递过该职位");
            }
            Application application = new Application();
            application.setUserId(userId);
            application.setJobId(jobId);
            application.setResumeId(resumeId);
            application.setStatus("pending");
            application.setApplyTime(LocalDateTime.now());
            applicationMapper.insert(application);
            log.info("简历投递成功: 用户ID={}, 职位ID={}, 简历ID={}", userId, jobId, resumeId);
            return application;
        } catch (Exception e) {
            log.error("简历投递失败: {}", e.getMessage());
            throw new RuntimeException("简历投递失败: " + e.getMessage());
        }
    }

    @Override
    public List<Application> getApplicationsByUserId(Long userId) {
        try {
            QueryWrapper<Application> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId).orderByDesc("apply_time");
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
            QueryWrapper<Application> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("application_id", applicationId).eq("user_id", userId);
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
            application.setFeedback(reviewerNotes);
            applicationMapper.updateById(application);
            log.info("申请状态更新成功: ID={}, 状态={}", applicationId, status);
            return true;
        } catch (Exception e) {
            log.error("更新申请状态失败: {}", e.getMessage());
            throw new RuntimeException("更新申请状态失败: " + e.getMessage());
        }
    }

    @Override
    public boolean hasAppliedForJob(Long userId, Long jobId) {
        try {
            QueryWrapper<Application> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId).eq("job_id", jobId);
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

    @Override
    public Page<ApplicantDetailDTO> getApplicantsByCompany(Long companyId, int current, int size, String status, String keyword) {
        try {
            log.info("企业查询应聘者列表: companyId={}, current={}, size={}", companyId, current, size);
            Page<ApplicantDetailDTO> page = new Page<>(current, size);
            List<ApplicantDetailDTO> applicants = applicationMapper.selectApplicantsByCompany(page, companyId, status, keyword);
            page.setRecords(applicants);
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
            return detail;
        } catch (Exception e) {
            log.error("企业获取应聘者详情失败: applicationId={}, error={}", applicationId, e.getMessage());
            throw new RuntimeException("获取应聘者详情失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean updateApplicationStatusByCompany(Long applicationId, String status, String feedback, Long companyId) {
        try {
            Integer count = applicationMapper.countApplicationByCompany(applicationId, companyId);
            if (count == null || count == 0) {
                throw new RuntimeException("申请记录不存在或无权限操作");
            }
            Application application = applicationMapper.selectById(applicationId);
            if (application == null) {
                throw new RuntimeException("申请记录不存在");
            }
            application.setStatus(status);
            application.setFeedback(feedback);
            application.setUpdatedAt(LocalDateTime.now());
            applicationMapper.updateById(application);
            log.info("企业更新申请状态成功: applicationId={}, status={}", applicationId, status);
            return true;
        } catch (Exception e) {
            log.error("企业更新申请状态失败: applicationId={}, error={}", applicationId, e.getMessage());
            throw new RuntimeException("更新申请状态失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean setInterviewTime(Long applicationId, LocalDateTime interviewTime, Long companyId) {
        try {
            Integer count = applicationMapper.countApplicationByCompany(applicationId, companyId);
            if (count == null || count == 0) {
                throw new RuntimeException("申请记录不存在或无权限操作");
            }
            Application application = applicationMapper.selectById(applicationId);
            if (application == null) {
                throw new RuntimeException("申请记录不存在");
            }
            application.setInterviewTime(interviewTime);
            application.setStatus("interview");
            application.setUpdatedAt(LocalDateTime.now());
            applicationMapper.updateById(application);
            log.info("设置面试时间成功: applicationId={}", applicationId);
            return true;
        } catch (Exception e) {
            log.error("设置面试时间失败: applicationId={}, error={}", applicationId, e.getMessage());
            throw new RuntimeException("设置面试时间失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean rejectApplicationWithReason(Long applicationId, String rejectionReason, Long companyId) {
        try {
            Integer count = applicationMapper.countApplicationByCompany(applicationId, companyId);
            if (count == null || count == 0) {
                throw new RuntimeException("申请记录不存在或无权限操作");
            }
            Application application = applicationMapper.selectById(applicationId);
            if (application == null) {
                throw new RuntimeException("申请记录不存在");
            }
            application.setRejectionReason(rejectionReason);
            application.setStatus("rejected");
            application.setUpdatedAt(LocalDateTime.now());
            applicationMapper.updateById(application);
            log.info("拒绝申请成功: applicationId={}", applicationId);
            return true;
        } catch (Exception e) {
            log.error("拒绝申请失败: applicationId={}, error={}", applicationId, e.getMessage());
            throw new RuntimeException("拒绝申请失败: " + e.getMessage());
        }
    }

    @Override
    public Application findByUserAndJobAndResume(Long userId, Long jobId, Long resumeId) {
        try {
            QueryWrapper<Application> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId).eq("job_id", jobId).eq("resume_id", resumeId);
            return applicationMapper.selectOne(queryWrapper);
        } catch (Exception e) {
            log.error("查找申请记录失败: userId={}, jobId={}, resumeId={}, error={}", userId, jobId, resumeId, e.getMessage());
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
            log.error("更新申请记录失败: applicationId={}, error={}", application.getApplicationId(), e.getMessage());
            return false;
        }
    }
}
