package com.aiinterview.service.impl.resume;

import com.aiinterview.exception.BusinessException;
import com.aiinterview.mapper.*;
import com.aiinterview.model.dto.resume.ResumeCreateRequest;
import com.aiinterview.model.dto.resume.ResumeUpdateRequest;
import com.aiinterview.model.dto.response.ResumeResponse;
import com.aiinterview.model.dto.resume.*;
import com.aiinterview.model.entity.*;
import com.aiinterview.model.entity.resume.Resume;
import com.aiinterview.service.resume.ResumeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.annotation.PostConstruct;

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

    private final ResumeMapper resumeMapper;

    @Override
    @Transactional
    public ResumeResponse createResume(ResumeCreateRequest request, Long userId) {
        resumer resume = new resumer();
        BeanUtils.copyProperties(request, resume);

        resume.setUserId(userId);
        resume.setIsDefault(false);
        resume.setViewCount(0);
        resume.setDownloadCount(0);
        resume.setStatus(request.getStatus() == null || request.getStatus().trim().isEmpty() ? "DRAFT" : request.getStatus().trim());
        // 保存简历时
        resume.setTemplateId(request.getTemplateId());  // 确保这行存在
        resumerMapper.insert(resume);
        saveResumeDetails(resume.getId(), request);
        log.info("保存的 templateId: {}", resume.getTemplateId());  // 添加日志
        return getResumeById(resume.getId(), userId);
    }

    @Override
    @Transactional
    public ResumeResponse updateResume(Long resumeId, ResumeUpdateRequest request, Long userId) {
        resumer existingResume = validateResumeOwnership(resumeId, userId);
        String originalStatus = existingResume.getStatus();

        BeanUtils.copyProperties(request, existingResume);
        if (request.getStatus() != null && !request.getStatus().trim().isEmpty()) {
            existingResume.setStatus(request.getStatus().trim());
        } else {
            existingResume.setStatus(originalStatus);
        }
        resumerMapper.updateById(existingResume);

        deleteResumeDetails(resumeId);
        saveResumeDetailsForUpdate(resumeId, request);

        return getResumeById(resumeId, userId);
    }

/*    @Override
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
    }*/

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

    @Override
    public List<resumer> getResumersByUserId(Long userId) {
        try {
            QueryWrapper<resumer> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                    .eq("is_deleted", false)
                    .orderByDesc("updated_at");
            return resumerMapper.selectList(queryWrapper);
        } catch (Exception e) {
            log.error("获取在线简历列表失败: userId={}, error={}", userId, e.getMessage());
            throw new RuntimeException("获取在线简历列表失败: " + e.getMessage());
        }
    }

    @Override
    public List<Resume> getUploadedResumesByUserId(Long userId) {
        try {
            QueryWrapper<Resume> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                    .orderByDesc("upload_date");
            return resumeMapper.selectList(queryWrapper);
        } catch (Exception e) {
            log.error("获取上传简历列表失败: userId={}, error={}", userId, e.getMessage());
            throw new RuntimeException("获取上传简历列表失败: " + e.getMessage());
        }
    }

    @Override
    public Resume getUploadedResumeById(Long resumeId, Long userId) {
        try {
            QueryWrapper<Resume> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("resume_id", resumeId)
                    .eq("user_id", userId);
            return resumeMapper.selectOne(queryWrapper);
        } catch (Exception e) {
            log.error("获取上传简历详情失败: resumeId={}, error={}", resumeId, e.getMessage());
            throw new RuntimeException("获取上传简历详情失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean deleteUploadedResume(Long resumeId, Long userId) {
        try {
            Resume resume = getUploadedResumeById(resumeId, userId);
            if (resume == null) {
                throw new RuntimeException("简历不存在或无权限删除");
            }

            // 删除物理文件
            deletePhysicalFile(resume.getFileUrl());

            // 删除数据库记录
            resumeMapper.deleteById(resumeId);

            log.info("上传简历删除成功: resumeId={}", resumeId);
            return true;
        } catch (Exception e) {
            log.error("删除上传简历失败: resumeId={}, error={}", resumeId, e.getMessage());
            return false;
        }
    }

    // 直接引用配置文件中的属性
    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.upload.url-prefix:http://localhost:8080/uploads}")
    private String urlPrefix;

    @Value("${file.upload.max-size:50MB}")
    private String maxSize;

    @Value("${file.upload.allowed-types:jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx}")
    private String allowedTypes;

    /**
     * 初始化上传路径
     */
    @PostConstruct
    public void initUploadPath() {
        try {
            // 确保路径以分隔符结尾
            if (!uploadPath.endsWith(File.separator)) {
                uploadPath = uploadPath + File.separator;
            }

            // 创建上传目录
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                if (created) {
                    log.info("创建上传目录成功: {}", uploadDir.getAbsolutePath());
                } else {
                    log.error("创建上传目录失败: {}", uploadDir.getAbsolutePath());
                    throw new RuntimeException("无法创建上传目录: " + uploadPath);
                }
            } else {
                log.info("上传目录已存在: {}", uploadDir.getAbsolutePath());
            }

            // 检查目录权限
            if (!uploadDir.canWrite()) {
                log.error("上传目录没有写权限: {}", uploadDir.getAbsolutePath());
                throw new RuntimeException("上传目录没有写权限: " + uploadPath);
            }

            log.info("文件上传服务初始化完成");
            log.info("上传路径: {}", uploadDir.getAbsolutePath());
            log.info("访问前缀: {}", urlPrefix);
            log.info("文件大小限制: {}", maxSize);
            log.info("允许的文件类型: {}", allowedTypes);

        } catch (Exception e) {
            log.error("文件上传服务初始化失败: {}", e.getMessage());
            throw new RuntimeException("文件上传服务初始化失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Resume uploadResume(MultipartFile file, Long userId,String filename) {
        try {
            // 验证文件
            validateFile(file);

            // 创建上传目录
            createUploadDirectory();

            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
//            String filename = generateFilename(fileExtension);

            // 保存文件到自定义路径
            String filePath = uploadPath + filename;
            Path path = Paths.get(filePath);

            log.info("保存文件到: {}", path.toAbsolutePath());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            // 创建简历记录
            Resume resume = new Resume();
            resume.setUserId(userId); // P0: 修正为 userId
            resume.setFileUrl(filePath);
            resume.setFilename(filename);
            resume.setUploadDate(LocalDateTime.now());
            resume.setParsedData("{}"); // 初始化为空JSON

            resumeMapper.insert(resume);

            log.info("简历上传成功: 用户ID={}, 文件名={}", userId, filename);
            return resume;

        } catch (IOException e) {
            log.error("文件保存失败: {}", e.getMessage());
            throw new RuntimeException("文件保存失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("简历上传失败: {}", e.getMessage());
            throw new RuntimeException("简历上传失败: " + e.getMessage());
        }
    }

//    @Override
//    public List<Resume> getResumesByUserId(Long userId) {
//        try {
//            QueryWrapper<Resume> queryWrapper = new QueryWrapper<>();
//            queryWrapper.eq("user_id", userId) // P0: 修正为 user_id
//                    .orderByDesc("upload_date");
//            return resumeMapper.selectList(queryWrapper);
//        } catch (Exception e) {
//            log.error("获取简历列表失败: {}", e.getMessage());
//            throw new RuntimeException("获取简历列表失败");
//        }
//    }

    private Resume getResumeEntityById(Long resumeId, Long userId) {
        try {
            QueryWrapper<Resume> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("resume_id", resumeId)
                    .eq("user_id", userId); // P0: 修正为 user_id
            return resumeMapper.selectOne(queryWrapper);
        } catch (Exception e) {
            log.error("获取简历详情失败: {}", e.getMessage());
            throw new RuntimeException("获取简历详情失败");
        }
    }

    @Override
    public ResumeResponse getResumeById(Long resumeId, Long userId) {
        resumer resume = validateResumeOwnership(resumeId, userId);
        return buildResumeResponse(resume);
    }

    @Override
    @Transactional
    public void deleteResume(Long resumeId, Long userId) {
        validateResumeOwnership(resumeId, userId);

        resumerMapper.deleteById(resumeId);
        deleteResumeDetails(resumeId);

        log.info("简历删除成功: ID={}", resumeId);
    }

    @Override
    public String analyzeResume(Long resumeId, Long userId) {
        try {
            Resume resume = getResumeEntityById(resumeId, userId);
            if (resume == null) {
                throw new RuntimeException("简历不存在");
            }

            // TODO: 实现AI简历分析逻辑
            String analysisResult = "简历分析功能正在开发中...";

            // 更新分析状态
            updateAnalysisStatus(resumeId, true, analysisResult);

            return analysisResult;
        } catch (Exception e) {
            log.error("简历分析失败: {}", e.getMessage());
            throw new RuntimeException("简历分析失败: " + e.getMessage());
        }
    }

    @Override
    public String getResumeFilePath(Long resumeId, Long userId) {
        Resume resume;
        if (userId != null) {
            // 验证用户权限
            resume = getResumeEntityById(resumeId, userId);
        } else {
            // 不验证用户权限，直接通过ID获取
            resume = resumeMapper.selectById(resumeId);
        }

        if (resume != null && resume.getFileUrl() != null) {
            // 如果fileUrl是完整路径，直接返回
            if (resume.getFileUrl().contains(File.separator) || resume.getFileUrl().contains("/")) {
                return resume.getFileUrl();
            }
            // 如果fileUrl只是文件名，构建完整路径
            return uploadPath + resume.getFileUrl();
        }
        return null;
    }

    @Override
    @Transactional
    public boolean updateAnalysisStatus(Long resumeId, boolean analyzed, String analysisResult) {
        try {
            Resume resume = resumeMapper.selectById(resumeId);
            if (resume != null) {
                // 使用parsed_data字段存储分析结果
                if (analyzed && analysisResult != null) {
                    resume.setParsedData(analysisResult);
                    resumeMapper.updateById(resume);
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("更新分析状态失败: {}", e.getMessage());
            return false;
        }
    }

    // 私有方法
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new RuntimeException("文件名不能为空");
        }

        // 检查文件类型
        String fileExtension = getFileExtension(originalFilename).toLowerCase();
        if (!isAllowedFileType(fileExtension)) {
            throw new RuntimeException("不支持的文件类型，只支持 .doc, .docx, .pdf, .jpg, .jpeg, .png, .gif");
        }

        // 检查文件大小（10MB）
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new RuntimeException("文件大小不能超过10MB");
        }
    }

    private void createUploadDirectory() {
        File directory = new File(uploadPath);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                throw new RuntimeException("创建上传目录失败");
            }
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    private boolean isAllowedFileType(String extension) {
        String[] allowedTypes = {".doc", ".docx", ".pdf", ".jpg", ".jpeg", ".png", ".gif"};
        for (String type : allowedTypes) {
            if (type.equals(extension)) {
                return true;
            }
        }
        return false;
    }

    private String generateFilename(String extension) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return "resume_" + timestamp + "_" + uuid + extension;
    }

    private void deletePhysicalFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.warn("删除物理文件失败: {}", e.getMessage());
        }
    }
}