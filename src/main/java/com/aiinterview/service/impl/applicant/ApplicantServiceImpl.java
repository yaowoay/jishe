package com.aiinterview.service.impl.applicant;

import com.aiinterview.model.dto.applicant.ApplicantProfileDTO;
import com.aiinterview.model.entity.applicant.Applicant;
import com.aiinterview.mapper.ApplicantMapper;
import com.aiinterview.service.applicat.ApplicantService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 求职者信息服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicantServiceImpl implements ApplicantService {

    private final ApplicantMapper applicantMapper;

    @Override
    public ApplicantProfileDTO getApplicantByUserId(Long userId) {
        try {
            QueryWrapper<Applicant> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            Applicant applicant = applicantMapper.selectOne(queryWrapper);

            if (applicant == null) {
                return null;
            }

            ApplicantProfileDTO dto = new ApplicantProfileDTO();
            BeanUtils.copyProperties(applicant, dto);
            return dto;
        } catch (Exception e) {
            log.error("获取求职者信息失败: userId={}, error={}", userId, e.getMessage());
            throw new RuntimeException("获取求职者信息失败");
        }
    }

    @Override
    @Transactional
    public ApplicantProfileDTO saveOrUpdateApplicant(Long userId, ApplicantProfileDTO applicantProfileDTO) {
        try {
            log.info("开始保存求职者信息: userId={}, data={}", userId, applicantProfileDTO);

            QueryWrapper<Applicant> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            Applicant existingApplicant = applicantMapper.selectOne(queryWrapper);

            log.info("查询现有记录结果: {}", existingApplicant != null ? "找到记录" : "未找到记录");

            Applicant applicant;
            if (existingApplicant != null) {
                // 更新现有记录
                applicant = existingApplicant;
                log.info("更新现有记录，ID: {}", applicant.getApplicantId());
                BeanUtils.copyProperties(applicantProfileDTO, applicant, "applicantId", "userId");
                applicantMapper.updateById(applicant);
                log.info("更新完成");
            } else {
                // 创建新记录
                applicant = new Applicant();
                log.info("创建新记录");
                BeanUtils.copyProperties(applicantProfileDTO, applicant, "applicantId");
                applicant.setUserId(userId);
                log.info("准备插入数据: {}", applicant);
                applicantMapper.insert(applicant);
                log.info("插入完成，新ID: {}", applicant.getApplicantId());
            }

            ApplicantProfileDTO result = new ApplicantProfileDTO();
            BeanUtils.copyProperties(applicant, result);
            log.info("保存成功，返回结果: {}", result);
            return result;
        } catch (Exception e) {
            log.error("保存求职者信息失败: userId={}, error={}", userId, e.getMessage(), e);
            throw new RuntimeException("保存求职者信息失败: " + e.getMessage());
        }
    }

    @Override
    public boolean hasApplicantProfile(Long userId) {
        try {
            QueryWrapper<Applicant> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            return applicantMapper.selectCount(queryWrapper) > 0;
        } catch (Exception e) {
            log.error("检查求职者信息失败: userId={}, error={}", userId, e.getMessage());
            return false;
        }
    }

    @Override
    public Applicant getApplicantEntityByUserId(Long userId) {
        try {
            QueryWrapper<Applicant> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            return applicantMapper.selectOne(queryWrapper);
        } catch (Exception e) {
            log.error("获取求职者实体失败: userId={}, error={}", userId, e.getMessage());
            throw new RuntimeException("获取求职者实体失败");
        }
    }
}
