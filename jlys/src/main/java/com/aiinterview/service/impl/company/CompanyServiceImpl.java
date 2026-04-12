package com.aiinterview.service.impl.company;

import com.aiinterview.model.dto.company.CompanyProfileDTO;
import com.aiinterview.model.entity.company.Company;
import com.aiinterview.mapper.CompanyMapper;
import com.aiinterview.service.company.CompanyService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 公司信息服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    
    private final CompanyMapper companyMapper;
    
    @Override
    public CompanyProfileDTO getCompanyByUserId(Long userId) {
        try {
            QueryWrapper<Company> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            Company company = companyMapper.selectOne(queryWrapper);
            
            if (company == null) {
                return null;
            }
            
            CompanyProfileDTO dto = new CompanyProfileDTO();
            BeanUtils.copyProperties(company, dto);
            return dto;
        } catch (Exception e) {
            log.error("获取公司信息失败: userId={}, error={}", userId, e.getMessage());
            throw new RuntimeException("获取公司信息失败");
        }
    }
    
    @Override
    @Transactional
    public CompanyProfileDTO saveOrUpdateCompany(Long userId, CompanyProfileDTO companyProfileDTO) {
        try {
            QueryWrapper<Company> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            Company existingCompany = companyMapper.selectOne(queryWrapper);
            
            Company company;
            if (existingCompany != null) {
                // 更新现有记录
                company = existingCompany;
                BeanUtils.copyProperties(companyProfileDTO, company, "companyId", "userId");
                companyMapper.updateById(company);
            } else {
                // 创建新记录
                company = new Company();
                BeanUtils.copyProperties(companyProfileDTO, company, "companyId");
                company.setUserId(userId);
                companyMapper.insert(company);
            }
            
            CompanyProfileDTO result = new CompanyProfileDTO();
            BeanUtils.copyProperties(company, result);
            return result;
        } catch (Exception e) {
            log.error("保存公司信息失败: userId={}, error={}", userId, e.getMessage());
            throw new RuntimeException("保存公司信息失败");
        }
    }
    
    @Override
    public boolean hasCompanyProfile(Long userId) {
        try {
            QueryWrapper<Company> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            return companyMapper.selectCount(queryWrapper) > 0;
        } catch (Exception e) {
            log.error("检查公司信息失败: userId={}, error={}", userId, e.getMessage());
            return false;
        }
    }
    
    @Override
    public Company getCompanyEntityByUserId(Long userId) {
        try {
            QueryWrapper<Company> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            return companyMapper.selectOne(queryWrapper);
        } catch (Exception e) {
            log.error("获取公司实体失败: userId={}, error={}", userId, e.getMessage());
            throw new RuntimeException("获取公司实体失败");
        }
    }
    
    @Override
    public Company getCompanyById(Long companyId) {
        try {
            return companyMapper.selectById(companyId);
        } catch (Exception e) {
            log.error("根据ID获取公司信息失败: companyId={}, error={}", companyId, e.getMessage());
            throw new RuntimeException("获取公司信息失败");
        }
    }
}
