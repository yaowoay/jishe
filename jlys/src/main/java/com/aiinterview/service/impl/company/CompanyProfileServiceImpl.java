package com.aiinterview.service.impl.company;

import com.aiinterview.mapper.CompanyMapper;
import com.aiinterview.mapper.UserMapper;
import com.aiinterview.model.dto.CompanyProfileRequest;
import com.aiinterview.model.entity.company.Company;
import com.aiinterview.model.entity.user.User;
import com.aiinterview.service.company.CompanyProfileService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 企业档案服务实现类
 */
@Service
@RequiredArgsConstructor
public class CompanyProfileServiceImpl implements CompanyProfileService {
    
    private final CompanyMapper companyMapper;
    private final UserMapper userMapper;
    
    @Override
    @Transactional
    public Company completeProfile(Long userId, CompanyProfileRequest request) {
        // 检查公司名称是否已存在
        QueryWrapper<Company> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("company_name", request.getCompanyName());
        queryWrapper.ne("user_id", userId); // 排除当前用户
        Company existingCompany = companyMapper.selectOne(queryWrapper);
        if (existingCompany != null) {
            throw new RuntimeException("公司名称已存在");
        }
        
        // 查找是否已有档案记录
        QueryWrapper<Company> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("user_id", userId);
        Company company = companyMapper.selectOne(userQueryWrapper);
        
        if (company == null) {
            // 创建新档案
            company = new Company();
            company.setUserId(userId.intValue());
            company.setCompanyName(request.getCompanyName());
            company.setIndustry(request.getIndustry());
            company.setAddress(request.getAddress());
            company.setScale(request.getScale());
            company.setWebsite(request.getWebsite());
            company.setContactPhone(request.getContactPhone());
            company.setLogoUrl(request.getLogoUrl());
            company.setDescription(request.getDescription());
            company.setVerifyStatus("pending");
            company.setCreditScore(60);
            company.setProfileCompletion(100); // 设置为已完成
            
            companyMapper.insert(company);
        } else {
            // 更新现有档案
            company.setCompanyName(request.getCompanyName());
            company.setIndustry(request.getIndustry());
            company.setAddress(request.getAddress());
            company.setScale(request.getScale());
            company.setWebsite(request.getWebsite());
            company.setContactPhone(request.getContactPhone());
            company.setLogoUrl(request.getLogoUrl());
            company.setDescription(request.getDescription());
            
            companyMapper.updateById(company);
        }
        
        // 更新用户表的profile_completed字段
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", userId);
        updateWrapper.set("profile_completed", 1);
        userMapper.update(null, updateWrapper);
        
        return company;
    }
    
    @Override
    @Transactional
    public Company updateProfile(Long userId, CompanyProfileRequest request) {
        // 查找现有档案记录
        QueryWrapper<Company> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("user_id", userId);
        Company company = companyMapper.selectOne(userQueryWrapper);
        
        if (company == null) {
            throw new RuntimeException("档案不存在，请先完善档案");
        }
        
        // 检查公司名称是否已被其他用户使用
        if (!request.getCompanyName().equals(company.getCompanyName())) {
            QueryWrapper<Company> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("company_name", request.getCompanyName());
            queryWrapper.ne("user_id", userId);
            Company existingCompany = companyMapper.selectOne(queryWrapper);
            if (existingCompany != null) {
                throw new RuntimeException("公司名称已存在");
            }
        }
        
        // 更新档案信息
        company.setCompanyName(request.getCompanyName());
        company.setIndustry(request.getIndustry());
        company.setAddress(request.getAddress());
        company.setScale(request.getScale());
        company.setWebsite(request.getWebsite());
        company.setContactPhone(request.getContactPhone());
        company.setLogoUrl(request.getLogoUrl());
        company.setDescription(request.getDescription());
        company.setProfileCompletion(100); // 设置为已完成
        
        companyMapper.updateById(company);
        
        // 更新用户表的profile_completed字段
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", userId);
        updateWrapper.set("profile_completed", 1);
        userMapper.update(null, updateWrapper);
        
        return company;
    }
    
    @Override
    public Company getByUserId(Long userId) {
        QueryWrapper<Company> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return companyMapper.selectOne(queryWrapper);
    }
    
    @Override
    public boolean isProfileCompleted(Long userId) {
        User user = userMapper.selectById(userId);
        return user != null && user.getProfileCompleted() != null && user.getProfileCompleted() == 1;
    }
}