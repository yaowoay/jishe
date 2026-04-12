package com.aiinterview.service.applicat;

import com.aiinterview.model.dto.applicant.ApplicantProfileDTO;
import com.aiinterview.model.entity.applicant.Applicant;

/**
 * 求职者信息服务接口
 */
public interface ApplicantService {
    
    /**
     * 根据用户ID获取求职者信息
     */
    ApplicantProfileDTO getApplicantByUserId(Long userId);
    
    /**
     * 创建或更新求职者信息
     */
    ApplicantProfileDTO saveOrUpdateApplicant(Long userId, ApplicantProfileDTO applicantProfileDTO);
    
    /**
     * 检查用户是否已有求职者信息
     */
    boolean hasApplicantProfile(Long userId);
    
    /**
     * 根据用户ID获取求职者实体
     */
    Applicant getApplicantEntityByUserId(Long userId);
}
