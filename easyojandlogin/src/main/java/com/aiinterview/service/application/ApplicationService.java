package com.aiinterview.service.application;

import com.aiinterview.model.dto.applicant.ApplicantDetailDTO;
import com.aiinterview.model.dto.application.ApplicationDetailDTO;
import com.aiinterview.model.entity.application.Application;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 简历投递申请服务接口
 */
public interface ApplicationService {

    /**
     * 投递简历
     */
    Application submitApplication(Long userId, Long jobId, Long resumeId);

    /**
     * 获取用户的投递历史
     */
    List<Application> getApplicationsByUserId(Long userId);

    /**
     * 获取用户已投递的职位ID列表
     */
    List<Long> getSubmittedJobIds(Long userId);

    /**
     * 获取用户已投递的详细记录列表
     */
    List<ApplicationDetailDTO> getSubmittedApplicationDetails(Long userId);

    /**
     * 获取申请详情
     */
    Application getApplicationById(Long applicationId, Long userId);

    /**
     * 撤回申请
     */
    boolean withdrawApplication(Long applicationId, Long userId);

    /**
     * 更新申请状态
     */
    boolean updateApplicationStatus(Long applicationId, String status, String reviewerNotes);

    /**
     * 检查是否已投递过该职位
     */
    boolean hasAppliedForJob(Long userId, Long jobId);

    /**
     * 获取简历的投递次数
     */
    int getApplicationCountByResumeId(Long resumeId);

    /**
     * 企业端：分页获取应聘者列表
     */
    Page<ApplicantDetailDTO> getApplicantsByCompany(Long companyId, int current, int size, String status, String keyword);

    /**
     * 企业端：获取应聘者详细信息
     */
    ApplicantDetailDTO getApplicantDetail(Long applicationId, Long companyId);

    /**
     * 企业端：更新申请状态
     */
    boolean updateApplicationStatusByCompany(Long applicationId, String status, String feedback, Long companyId);

    /**
     * 企业端：设置面试时间
     */
    boolean setInterviewTime(Long applicationId, LocalDateTime interviewTime, Long companyId);

    /**
     * 企业端：拒绝申请并设置拒绝原因
     */
    boolean rejectApplicationWithReason(Long applicationId, String rejectionReason, Long companyId);

    /**
     * 根据用户、职位和简历查找申请记录
     */
    Application findByUserAndJobAndResume(Long userId, Long jobId, Long resumeId);

    /**
     * 更新申请记录
     */
    boolean updateApplication(Application application);
}
