package com.coldwind.easyoj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.coldwind.easyoj.exception.BusinessException;
import com.coldwind.easyoj.mapper.*;
import com.coldwind.easyoj.model.dto.*;
import com.coldwind.easyoj.model.dto.request.ResumeCreateRequest;
import com.coldwind.easyoj.model.dto.request.ResumeUpdateRequest;
import com.coldwind.easyoj.model.dto.response.ResumeResponse;
import com.coldwind.easyoj.model.entity.*;
import com.coldwind.easyoj.service.ResumeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 简历服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private final ResumeMapper resumeMapper;
    private final WorkExperienceMapper workExperienceMapper;
    private final EducationMapper educationMapper;
    private final ProjectExperienceMapper projectExperienceMapper;
    private final SkillMapper skillMapper;
    private final AdditionalInfoMapper additionalInfoMapper;
    
    @Override
    @Transactional
    public ResumeResponse createResume(ResumeCreateRequest request, String userKey) {
        // 创建简历主表
        Resume resume = new Resume();
        BeanUtils.copyProperties(request, resume);
        resume.setUserKey(userKey);
        resume.setIsDefault(false);
        resume.setViewCount(0);
        
        resumeMapper.insert(resume);
        
        // 保存详细信息
        saveResumeDetails(resume.getId(), request);
        
        return getResumeById(resume.getId(), userKey);
    }
    
    @Override
    @Transactional
    public ResumeResponse updateResume(Long resumeId, ResumeUpdateRequest request, String userKey) {
        // 验证权限
        Resume existingResume = validateResumeOwnership(resumeId, userKey);
        
        // 更新简历主表
        BeanUtils.copyProperties(request, existingResume);
        resumeMapper.updateById(existingResume);
        
        // 删除旧的详细信息
        deleteResumeDetails(resumeId);
        
        // 保存新的详细信息
        saveResumeDetailsForUpdate(resumeId, request);
        
        return getResumeById(resumeId, userKey);
    }
    
    @Override
    @Transactional
    public void deleteResume(Long resumeId, String userKey) {
        validateResumeOwnership(resumeId, userKey);
        
        // 软删除简历
        resumeMapper.deleteById(resumeId);
        
        // 删除详细信息
        deleteResumeDetails(resumeId);
    }
    
    @Override
    public ResumeResponse getResumeById(Long resumeId, String userKey) {
        Resume resume = validateResumeOwnership(resumeId, userKey);
        return buildResumeResponse(resume);
    }
    
    @Override
    public List<ResumeResponse> getUserResumes(String userKey) {
        List<Resume> resumes = resumeMapper.selectByUserKey(userKey);
        return resumes.stream()
                .map(this::buildResumeResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public org.springframework.data.domain.Page<ResumeResponse> getUserResumesPage(String userKey, Pageable pageable) {
        QueryWrapper<Resume> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_key", userKey);
        queryWrapper.orderByDesc("update_time");
        
        IPage<Resume> page = new Page<>(pageable.getPageNumber(), pageable.getPageSize());
        IPage<Resume> result = resumeMapper.selectPage(page, queryWrapper);
        
        List<ResumeResponse> responses = result.getRecords().stream()
                .map(this::buildResumeResponse)
                .collect(Collectors.toList());
        
        return new PageImpl<>(responses, pageable, result.getTotal());
    }
    
    @Override
    @Transactional
    public void setDefaultResume(Long resumeId, String userKey) {
        validateResumeOwnership(resumeId, userKey);
        
        // 取消其他默认简历
        QueryWrapper<Resume> updateWrapper = new QueryWrapper<>();
        updateWrapper.eq("user_key", userKey);
        updateWrapper.eq("is_default", true);
        
        Resume updateResume = new Resume();
        updateResume.setIsDefault(false);
        resumeMapper.update(updateResume, updateWrapper);
        
        // 设置新的默认简历
        Resume resume = new Resume();
        resume.setId(resumeId);
        resume.setIsDefault(true);
        resumeMapper.updateById(resume);
    }
    
    @Override
    @Transactional
    public ResumeResponse copyResume(Long resumeId, String userKey) {
        Resume originalResume = validateResumeOwnership(resumeId, userKey);
        
        // 创建新简历
        Resume newResume = new Resume();
        BeanUtils.copyProperties(originalResume, newResume);
        newResume.setId(null);
        newResume.setName(originalResume.getName() + " - 副本");
        newResume.setIsDefault(false);
        newResume.setShareUrl(null);
        newResume.setViewCount(0);
        
        resumeMapper.insert(newResume);
        
        // 复制详细信息
        copyResumeDetails(resumeId, newResume.getId());
        
        return getResumeById(newResume.getId(), userKey);
    }

    @Override
    public String generateShareUrl(Long resumeId, String userKey) {
        validateResumeOwnership(resumeId, userKey);
        
        String shareUrl = UUID.randomUUID().toString().replace("-", "");
        
        Resume resume = new Resume();
        resume.setId(resumeId);
        resume.setShareUrl(shareUrl);
        resumeMapper.updateById(resume);
        
        return shareUrl;
    }

    @Override
    public ResumeResponse getResumeByShareUrl(String shareUrl) {
        Resume resume = resumeMapper.selectByShareUrl(shareUrl);
        if (resume == null) {
            throw new BusinessException(400, "分享链接无效");
        }
        
        // 更新查看次数
        resumeMapper.updateViewCount(resume.getId());
        
        return buildResumeResponse(resume);
    }

    @Override
    public org.springframework.data.domain.Page<ResumeResponse> searchResumes(String keyword, Pageable pageable) {
        List<Resume> resumes = resumeMapper.searchResumes(keyword, null);
        
        List<ResumeResponse> responses = resumes.stream()
                .map(this::buildResumeResponse)
                .collect(Collectors.toList());
        
        return new PageImpl<>(responses, pageable, resumes.size());
    }

    @Override
    public List<ResumeResponse> getPopularResumes(int limit) {
        List<Resume> resumes = resumeMapper.selectPopularResumes(limit);
        return resumes.stream()
                .map(this::buildResumeResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResumeResponse> getRecentResumes(String userKey, int limit) {
        QueryWrapper<Resume> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_key", userKey);
        queryWrapper.orderByDesc("update_time");
        queryWrapper.last("LIMIT " + limit);
        
        List<Resume> resumes = resumeMapper.selectList(queryWrapper);
        return resumes.stream()
                .map(this::buildResumeResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 验证简历所有权
     */
    private Resume validateResumeOwnership(Long resumeId, String userKey) {
        Resume resume = resumeMapper.selectById(resumeId);
        if (resume == null) {
            throw new BusinessException(400, "简历不存在");
        }
        if (!userKey.equals(resume.getUserKey())) {
            throw new BusinessException(403, "无权限操作此简历");
        }
        return resume;
    }
    
    /**
     * 构建简历响应
     */
    private ResumeResponse buildResumeResponse(Resume resume) {
        ResumeResponse response = new ResumeResponse();
        BeanUtils.copyProperties(resume, response);
        
        // 获取详细信息
        response.setWorkExperiences(getWorkExperiences(resume.getId()));
        response.setEducations(getEducations(resume.getId()));
        response.setProjectExperiences(getProjectExperiences(resume.getId()));
        response.setSkills(getSkills(resume.getId()));
        response.setAdditionalInfos(getAdditionalInfos(resume.getId()));
        
        return response;
    }
    
    /**
     * 保存简历详细信息
     */
    private void saveResumeDetails(Long resumeId, ResumeCreateRequest request) {
        if (request.getWorkExperiences() != null) {
            for (WorkExperienceDTO dto : request.getWorkExperiences()) {
                WorkExperience entity = new WorkExperience();
                BeanUtils.copyProperties(dto, entity);
                entity.setResumeId(resumeId);
                workExperienceMapper.insert(entity);
            }
        }
        
        if (request.getEducations() != null) {
            for (EducationDTO dto : request.getEducations()) {
                Education entity = new Education();
                BeanUtils.copyProperties(dto, entity);
                entity.setResumeId(resumeId);
                educationMapper.insert(entity);
            }
        }
        
        if (request.getProjectExperiences() != null) {
            for (ProjectExperienceDTO dto : request.getProjectExperiences()) {
                ProjectExperience entity = new ProjectExperience();
                BeanUtils.copyProperties(dto, entity);
                entity.setResumeId(resumeId);
                projectExperienceMapper.insert(entity);
            }
        }
        
        if (request.getSkills() != null) {
            for (SkillDTO dto : request.getSkills()) {
                Skill entity = new Skill();
                BeanUtils.copyProperties(dto, entity);
                entity.setResumeId(resumeId);
                skillMapper.insert(entity);
            }
        }
        
        if (request.getAdditionalInfos() != null) {
            for (AdditionalInfoDTO dto : request.getAdditionalInfos()) {
                AdditionalInfo entity = new AdditionalInfo();
                BeanUtils.copyProperties(dto, entity);
                entity.setResumeId(resumeId);
                additionalInfoMapper.insert(entity);
            }
        }
    }
    
    /**
     * 保存简历详细信息（更新用）
     */
    private void saveResumeDetailsForUpdate(Long resumeId, ResumeUpdateRequest request) {
        if (request.getWorkExperiences() != null) {
            for (WorkExperienceDTO dto : request.getWorkExperiences()) {
                WorkExperience entity = new WorkExperience();
                BeanUtils.copyProperties(dto, entity);
                entity.setResumeId(resumeId);
                workExperienceMapper.insert(entity);
            }
        }
        
        if (request.getEducations() != null) {
            for (EducationDTO dto : request.getEducations()) {
                Education entity = new Education();
                BeanUtils.copyProperties(dto, entity);
                entity.setResumeId(resumeId);
                educationMapper.insert(entity);
            }
        }
        
        if (request.getProjectExperiences() != null) {
            for (ProjectExperienceDTO dto : request.getProjectExperiences()) {
                ProjectExperience entity = new ProjectExperience();
                BeanUtils.copyProperties(dto, entity);
                entity.setResumeId(resumeId);
                projectExperienceMapper.insert(entity);
            }
        }
        
        if (request.getSkills() != null) {
            for (SkillDTO dto : request.getSkills()) {
                Skill entity = new Skill();
                BeanUtils.copyProperties(dto, entity);
                entity.setResumeId(resumeId);
                skillMapper.insert(entity);
            }
        }
        
        if (request.getAdditionalInfos() != null) {
            for (AdditionalInfoDTO dto : request.getAdditionalInfos()) {
                AdditionalInfo entity = new AdditionalInfo();
                BeanUtils.copyProperties(dto, entity);
                entity.setResumeId(resumeId);
                additionalInfoMapper.insert(entity);
            }
        }
    }
    
    /**
     * 删除简历详细信息
     */
    private void deleteResumeDetails(Long resumeId) {
        workExperienceMapper.deleteByResumeId(resumeId);
        educationMapper.deleteByResumeId(resumeId);
        projectExperienceMapper.deleteByResumeId(resumeId);
        skillMapper.deleteByResumeId(resumeId);
        additionalInfoMapper.deleteByResumeId(resumeId);
    }
    
    /**
     * 复制简历详细信息
     */
    private void copyResumeDetails(Long sourceResumeId, Long targetResumeId) {
        // 复制工作经历
        List<WorkExperience> workExperiences = workExperienceMapper.selectByResumeId(sourceResumeId);
        for (WorkExperience entity : workExperiences) {
            entity.setId(null);
            entity.setResumeId(targetResumeId);
            workExperienceMapper.insert(entity);
        }
        
        // 复制教育经历
        List<Education> educations = educationMapper.selectByResumeId(sourceResumeId);
        for (Education entity : educations) {
            entity.setId(null);
            entity.setResumeId(targetResumeId);
            educationMapper.insert(entity);
        }
        
        // 复制项目经验
        List<ProjectExperience> projectExperiences = projectExperienceMapper.selectByResumeId(sourceResumeId);
        for (ProjectExperience entity : projectExperiences) {
            entity.setId(null);
            entity.setResumeId(targetResumeId);
            projectExperienceMapper.insert(entity);
        }
        
        // 复制技能
        List<Skill> skills = skillMapper.selectByResumeId(sourceResumeId);
        for (Skill entity : skills) {
            entity.setId(null);
            entity.setResumeId(targetResumeId);
            skillMapper.insert(entity);
        }
        
        // 复制其他信息
        List<AdditionalInfo> additionalInfos = additionalInfoMapper.selectByResumeId(sourceResumeId);
        for (AdditionalInfo entity : additionalInfos) {
            entity.setId(null);
            entity.setResumeId(targetResumeId);
            additionalInfoMapper.insert(entity);
        }
    }
    
    /**
     * 获取工作经历
     */
    private List<WorkExperienceDTO> getWorkExperiences(Long resumeId) {
        List<WorkExperience> entities = workExperienceMapper.selectByResumeId(resumeId);
        return entities.stream().map(entity -> {
            WorkExperienceDTO dto = new WorkExperienceDTO();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        }).collect(Collectors.toList());
    }
    
    /**
     * 获取教育经历
     */
    private List<EducationDTO> getEducations(Long resumeId) {
        List<Education> entities = educationMapper.selectByResumeId(resumeId);
        return entities.stream().map(entity -> {
            EducationDTO dto = new EducationDTO();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        }).collect(Collectors.toList());
    }
    
    /**
     * 获取项目经验
     */
    private List<ProjectExperienceDTO> getProjectExperiences(Long resumeId) {
        List<ProjectExperience> entities = projectExperienceMapper.selectByResumeId(resumeId);
        return entities.stream().map(entity -> {
            ProjectExperienceDTO dto = new ProjectExperienceDTO();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        }).collect(Collectors.toList());
    }
    
    /**
     * 获取技能
     */
    private List<SkillDTO> getSkills(Long resumeId) {
        List<Skill> entities = skillMapper.selectByResumeId(resumeId);
        return entities.stream().map(entity -> {
            SkillDTO dto = new SkillDTO();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        }).collect(Collectors.toList());
    }
    
    /**
     * 获取其他信息
     */
    private List<AdditionalInfoDTO> getAdditionalInfos(Long resumeId) {
        List<AdditionalInfo> entities = additionalInfoMapper.selectByResumeId(resumeId);
        return entities.stream().map(entity -> {
            AdditionalInfoDTO dto = new AdditionalInfoDTO();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        }).collect(Collectors.toList());
    }
}