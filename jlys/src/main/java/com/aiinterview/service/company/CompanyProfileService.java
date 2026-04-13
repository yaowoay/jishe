package com.aiinterview.service.company;

import com.aiinterview.model.dto.CompanyProfileRequest;
import com.aiinterview.model.entity.company.Company;

/**
 * 企业档案服务接口
 */
public interface CompanyProfileService {
    
    /**
     * 完善企业档案
     */
    Company completeProfile(Long userId, CompanyProfileRequest request);
    
    /**
     * 更新企业档案
     */
    Company updateProfile(Long userId, CompanyProfileRequest request);
    
    /**
     * 根据用户ID获取企业档案
     */
    Company getByUserId(Long userId);
    
    /**
     * 检查企业档案是否已完善
     */
    boolean isProfileCompleted(Long userId);
}