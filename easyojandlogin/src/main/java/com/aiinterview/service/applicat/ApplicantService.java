package com.aiinterview.service.applicat;

import com.aiinterview.model.dto.applicant.ApplicantProfileDTO;

/**
 * 求职者信息服务接口
 */
public interface ApplicantService {
    
    /**
     * 根据用户ID获取求职者信息（聚合 student_profile + resume）
     */
    ApplicantProfileDTO getApplicantByUserId(Long userId);
    
    /**
     * 保存或更新求职者信息
     * 实际会写入 student_profile和resume 表
     */
    ApplicantProfileDTO saveOrUpdateApplicant(Long userId, ApplicantProfileDTO applicantProfileDTO);
    
    /**
     * 检查用户是否已有求职者信息
     */
    boolean hasApplicantProfile(Long userId);
    
    /**
     * 根据用户ID获取求职者实体
     */
   // Applicant getApplicantEntityByUserId(Long userId);
}
