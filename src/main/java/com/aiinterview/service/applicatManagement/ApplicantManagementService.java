package com.aiinterview.service.applicatManagement;

import com.aiinterview.model.dto.applicant.ApplicantDetailDTO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.Map;

/**
 * 应聘者管理服务接口 - 基于 route_application.py 重新设计
 */
public interface ApplicantManagementService {

    // ==================== 求职者端功能 ====================

    /**
     * 获取求职者的最近面试记录 (对应 get_application)
     * @param userId 用户ID
     * @param limit 限制数量，默认5条
     * @return 最近面试记录列表
     */
    List<Map<String, Object>> getRecentInterviews(Long userId, Integer limit);

    /**
     * 获取求职者的全部面试记录 (对应 get_all_interviews)
     * @param userId 用户ID
     * @return 全部面试记录列表
     */
    List<Map<String, Object>> getAllInterviews(Long userId);

    /**
     * 进入面试房间 (对应 interview_room)
     * @param userId 用户ID
     * @param applicationId 申请ID
     * @return 面试房间信息
     */
    Map<String, Object> enterInterviewRoom(Long userId, Long applicationId);

    // ==================== 企业端功能 ====================

    /**
     * 分页获取公司的应聘者列表
     */
    Page<ApplicantDetailDTO> getApplicantsByCompany(Long companyId, int current, int size, String status, String keyword);

    /**
     * 获取职位详情和申请者信息 (对应 job_detail)
     * @param jobId 职位ID
     * @param companyId 公司ID (用于权限验证)
     * @return 职位详情和申请者列表
     */
    Map<String, Object> getJobDetailWithApplicants(Long jobId, Long companyId);

    /**
     * 获取候选人列表 (对应 get_candidates)
     * @param jobId 职位ID
     * @param showAll 是否显示所有候选人(包括低分)
     * @param companyId 公司ID
     * @return 候选人列表
     */
    List<Map<String, Object>> getCandidates(Long jobId, Boolean showAll, Long companyId);

    /**
     * 获取应聘者详细信息
     */
    ApplicantDetailDTO getApplicantDetail(Long applicationId, Long companyId);

    // ==================== 状态管理功能 ====================

    /**
     * 标记申请为已查看 (对应 mark_viewed)
     * @param applicationId 申请ID
     * @param companyId 公司ID
     * @return 操作结果
     */
    boolean markApplicationAsViewed(Long applicationId, Long companyId);

    /**
     * 发送面试邀请 (对应 send_invitation)
     * @param applicationId 申请ID
     * @param interviewTime 面试时间
     * @param notes 备注
     * @param companyId 公司ID
     * @return 操作结果
     */
    boolean sendInterviewInvitation(Long applicationId, String interviewTime, String notes, Long companyId);

    /**
     * 拒绝申请 (对应 reject)
     * @param applicationId 申请ID
     * @param reason 拒绝原因
     * @param companyId 公司ID
     * @return 操作结果
     */
    boolean rejectApplication(Long applicationId, String reason, Long companyId);

    /**
     * 录用应聘者 (对应 accept)
     * @param applicationId 申请ID
     * @param feedback 录用反馈
     * @param companyId 公司ID
     * @return 操作结果
     */
    boolean acceptApplication(Long applicationId, String feedback, Long companyId);

    /**
     * 更新申请状态 (通用方法)
     */
    boolean updateApplicationStatus(Long applicationId, String status, String feedback, Long companyId);

    // ==================== 批量操作和统计 ====================

    /**
     * 批量操作申请
     */
    boolean batchUpdateApplications(List<Long> applicationIds, String status, String feedback, Long companyId);

    /**
     * 获取申请统计信息
     */
    Map<String, Object> getApplicationStatistics(Long companyId);

    /**
     * 获取面试统计信息
     * @param companyId 公司ID
     * @return 面试统计数据
     */
    Map<String, Object> getInterviewStatistics(Long companyId);

    // ==================== 兼容性方法 ====================

    /**
     * 录用应聘者 (兼容旧接口)
     */
    boolean hireApplicant(Long applicationId, String feedback, Long companyId);

    /**
     * 拒绝录用 (兼容旧接口)
     */
    boolean rejectHire(Long applicationId, String reason, Long companyId);
}
