package com.aiinterview.service.company;

import com.aiinterview.model.dto.company.CompanyProfileDTO;
import com.aiinterview.model.entity.company.Company;

/**
 * 公司信息服务接口
 */
public interface CompanyService {
    
    /**
     * 根据用户ID获取公司信息
     */
    CompanyProfileDTO getCompanyByUserId(Long userId);
    
    /**
     * 创建或更新公司信息
     */
    CompanyProfileDTO saveOrUpdateCompany(Long userId, CompanyProfileDTO companyProfileDTO);
    
    /**
     * 检查用户是否已有公司信息
     */
    boolean hasCompanyProfile(Long userId);
    
    /**
     * 根据用户ID获取公司实体
     */
    Company getCompanyEntityByUserId(Long userId);
    
    /**
     * 根据公司ID获取公司信息
     */
    Company getCompanyById(Long companyId);
}
