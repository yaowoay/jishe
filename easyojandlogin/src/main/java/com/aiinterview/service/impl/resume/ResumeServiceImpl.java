package com.aiinterview.service.impl.resume;

import com.aiinterview.exception.BusinessException;
import com.aiinterview.mapper.*;
import com.aiinterview.model.dto.*;
import com.aiinterview.model.dto.request.ResumeCreateRequest;
import com.aiinterview.model.dto.request.ResumeUpdateRequest;
import com.aiinterview.model.dto.response.ResumeResponse;
import com.aiinterview.model.entity.*;
import com.aiinterview.service.resume.ResumeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 简历服务实现类 (已修正版)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private final ResumerMapper resumerMapper;
    private final WorkExperienceMapper workExperienceMapper;
    private final EducationMapper educationMapper;
    private final ProjectExperienceMapper projectExperienceMapper;
    private final SkillMapper skillMapper;
    private final AdditionalInfoMapper additionalInfoMapper;

    @Override
    @Transactional
    public ResumeResponse createResume(ResumeCreateRequest request, Long userId) {
        resumer resume = new resumer();
        BeanUtils.copyProperties(request, resume);

        resume.setUserId(userId);
        resume.setIsDefault(false);
        resume.setViewCount(0);

        resumerMapper.insert(resume);
        saveResumeDetails(resume.getId(), request);

        return getResumeById(resume.getId(), userId);
    }

    @Override
    @Transactional
    public ResumeResponse updateResume(Long resumeId, ResumeUpdateRequest request, Long userId) {
        resumer existingResume = validateResumeOwnership(resumeId, userId);

        BeanUtils.copyProperties(request, existingResume);
        resumerMapper.updateById(existingResume);

        deleteResumeDetails(resumeId);
        saveResumeDetailsForUpdate(resumeId, request);

        return getResumeById(resumeId, userId);
    }

    @Override
    @Transactional
    public void deleteResume(Long resumeId, Long userId) {
        validateResumeOwnership(resumeId, userId);

        resumerMapper.deleteById(resumeId);
        deleteResumeDetails(resumeId);
    }

    @Override
    public ResumeResponse getResumeById(Long resumeId, Long userId) {
        resumer resume = validateResumeOwnership(resumeId, userId);
        return buildResumeResponse(resume);
    }

    @Override
    public List<ResumeResponse> getUserResumes(Long userId) {
        List<resumer> resumes = resumerMapper.selectList(
                new QueryWrapper<resumer>().eq("user_id", userId)
        );
        return resumes.stream()
                .map(this::buildResumeResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PageImpl<ResumeResponse> getUserResumesPage(Long userId, Pageable pageable) {
        QueryWrapper<resumer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("updated_at"); // P0: update_time -> updated_at

        // P0: 修正分页页码（Spring Pageable 从0开始，MP Page 从1开始）
        IPage<resumer> page = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        IPage<resumer> result = resumerMapper.selectPage(page, queryWrapper);

        List<ResumeResponse> responses = result.getRecords().stream()
                .map(this::buildResumeResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, result.getTotal());
    }

    @Override
    @Transactional
    public void setDefaultResume(Long resumeId, Long userId) {
        validateResumeOwnership(resumeId, userId);

        // 取消其他默认简历
        resumer updateresumer = new resumer();
        updateresumer.setIsDefault(false);
        resumerMapper.update(updateresumer,
                new QueryWrapper<resumer>().eq("user_id", userId).eq("is_default", true)
        );

        // 设置新的默认简历
        resumer resume = new resumer();
        resume.setId(resumeId);
        resume.setIsDefault(true);
        resumerMapper.updateById(resume);
    }

    @Override
    @Transactional
    public ResumeResponse copyResume(Long resumeId, Long userId) {
        resumer originalResume = validateResumeOwnership(resumeId, userId);

        resumer newResume = new resumer();
        BeanUtils.copyProperties(originalResume, newResume);
        newResume.setId(null);
        newResume.setName(originalResume.getName() + " - 副本");
        newResume.setIsDefault(false);
        newResume.setShareCode(null); // P0: shareUrl -> shareCode
        newResume.setViewCount(0);

        resumerMapper.insert(newResume);
        copyResumeDetails(resumeId, newResume.getId());

        return getResumeById(newResume.getId(), userId);
    }

    @Override
    public String generateShareCode(Long resumeId, Long userId) {
        validateResumeOwnership(resumeId, userId);

        String shareCode = UUID.randomUUID().toString().replace("-", "");

        resumer resume = new resumer();
        resume.setId(resumeId);
        resume.setShareCode(shareCode); // P0: shareUrl -> shareCode
        resumerMapper.updateById(resume);

        return shareCode;
    }

    @Override
    public ResumeResponse getResumeByShareCode(String shareCode) {
        // P0: Mapper 方法名和字段名修正
        resumer resume = resumerMapper.selectByShareCode(shareCode);
        if (resume == null) {
            throw new BusinessException(400, "分享链接无效");
        }

        resumerMapper.updateViewCount(resume.getId());
        return buildResumeResponse(resume);
    }

    @Override
    public PageImpl<ResumeResponse> searchResumes(String keyword, Pageable pageable) {
        // P0: 修正查询逻辑以适配新字段
        QueryWrapper<resumer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", false);
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                    .like("name", keyword)
                    .or().like("full_name", keyword)
                    .or().like("position", keyword)
                    .or().like("profile", keyword)
            );
        }
        queryWrapper.orderByDesc("updated_at");

        // 手动分页（如果 Mapper 不支持 Page 参数）
        long total = resumerMapper.selectCount(queryWrapper);
        queryWrapper.last("LIMIT " + pageable.getOffset() + "," + pageable.getPageSize());

        List<resumer> resumes = resumerMapper.selectList(queryWrapper);
        List<ResumeResponse> responses = resumes.stream()
                .map(this::buildResumeResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, total);
    }

    @Override
    public List<ResumeResponse> getPopularResumes(int limit) {
        // P0: 修正排序字段
        List<resumer> resumes = resumerMapper.selectList(
                new QueryWrapper<resumer>()
                        .eq("is_deleted", false)
                        .orderByDesc("view_count")
                        .last("LIMIT " + limit)
        );
        return resumes.stream()
                .map(this::buildResumeResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResumeResponse> getRecentResumes(Long userId, int limit) {
        QueryWrapper<resumer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId); // P0: user_key -> user_id
        queryWrapper.orderByDesc("updated_at"); // P0: update_time -> updated_at
        queryWrapper.last("LIMIT " + limit);

        List<resumer> resumes = resumerMapper.selectList(queryWrapper);
        return resumes.stream()
                .map(this::buildResumeResponse)
                .collect(Collectors.toList());
    }

    /**
     * 验证简历所有权
     */
    private resumer validateResumeOwnership(Long resumeId, Long userId) {
        resumer resumer = resumerMapper.selectById(resumeId);
        if (resumer == null || resumer.getIsDeleted()) {
            throw new BusinessException(404, "简历不存在");
        }
        // P0: 比较 userId
        if (!userId.equals(resumer.getUserId())) {
            throw new BusinessException(403, "无权限操作此简历");
        }
        return resumer;
    }

    /**
     * 构建简历响应
     */
    private ResumeResponse buildResumeResponse(resumer resume) {
        ResumeResponse response = new ResumeResponse();
        BeanUtils.copyProperties(resume, response);

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

    //以下非软件杯
    @Override
    public List<resumer> getResumesByUserId(Long userId) {
        QueryWrapper<resumer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId) // P0: user_key -> user_id
                .eq("is_deleted", false)
                .orderByDesc("updated_at"); // P0: update_time -> updated_at
        return resumerMapper.selectList(queryWrapper);
    }

    @Override
    public String getResumeFilePath(Long resumeId, Long userId) {
        resumer resume = resumerMapper.selectById(resumeId);
        if (resume == null || resume.getIsDeleted()) {
            return null;
        }
        // 如果提供了userId，验证权限
        if (userId != null && !resume.getUserId().equals(userId)) {
            return null;
        }
        return resume.getShareCode(); // P0: shareUrl -> shareCode
    }
}