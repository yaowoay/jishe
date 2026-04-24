package com.aiinterview.service.cooperation.impl;

import com.aiinterview.mapper.*;
import com.aiinterview.model.dto.cooperation.*;
import com.aiinterview.model.entity.company.Company;
import com.aiinterview.model.entity.cooperation.*;
import com.aiinterview.model.entity.teacher.Teacher;
import com.aiinterview.service.cooperation.CooperationService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 校企合作服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CooperationServiceImpl implements CooperationService {

    private final CooperationProjectMapper projectMapper;
    private final TrainingBaseMapper trainingBaseMapper;
    private final OrderClassMapper orderClassMapper;
    private final CooperationCaseMapper caseMapper;
    private final CompanyMapper companyMapper;
    private final TeacherMapper teacherMapper;

    @Override
    @Transactional
    public CooperationProjectDTO createProject(Long companyId, CooperationProjectDTO projectDTO) {
        try {
            // 验证企业是否存在
            Company company = companyMapper.selectById(companyId);
            if (company == null) {
                throw new RuntimeException("企业不存在");
            }

            CooperationProject project = new CooperationProject();
            BeanUtils.copyProperties(projectDTO, project);
            project.setCompanyId(companyId);
            project.setStatus("draft");

            projectMapper.insert(project);

            return convertToProjectDTO(project);
        } catch (Exception e) {
            log.error("创建合作项目失败: companyId={}, error={}", companyId, e.getMessage());
            throw new RuntimeException("创建合作项目失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public CooperationProjectDTO updateProject(Long companyId, Long projectId, CooperationProjectDTO projectDTO) {
        CooperationProject project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }
        if (!project.getCompanyId().equals(companyId)) {
            throw new RuntimeException("无权限操作此项目");
        }
        if (!"draft".equals(project.getStatus())) {
            throw new RuntimeException("只能修改草稿状态的项目");
        }

        BeanUtils.copyProperties(projectDTO, project, "id", "companyId", "status", "submitTime", "reviewTime", "reviewedBy", "reviewComment");
        projectMapper.updateById(project);

        return convertToProjectDTO(project);
    }

    @Override
    @Transactional
    public CooperationProjectDTO submitProject(Long companyId, Long projectId) {
        CooperationProject project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }
        if (!project.getCompanyId().equals(companyId)) {
            throw new RuntimeException("无权限操作此项目");
        }
        if (!"draft".equals(project.getStatus())) {
            throw new RuntimeException("只能提交草稿状态的项目");
        }

        project.setStatus("submitted");
        project.setSubmitTime(LocalDateTime.now());
        projectMapper.updateById(project);

        return convertToProjectDTO(project);
    }

    @Override
    public CooperationProjectDTO getProjectDetail(Long projectId) {
        CooperationProject project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }
        return convertToProjectDTO(project);
    }

    @Override
    public IPage<CooperationProjectDTO> getProjectList(Long companyId, String status, String projectType, Integer page, Integer size) {
        Page<CooperationProject> pageParam = new Page<>(page, size);
        QueryWrapper<CooperationProject> queryWrapper = new QueryWrapper<>();

        if (companyId != null) {
            queryWrapper.eq("company_id", companyId);
        }
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }
        if (projectType != null && !projectType.isEmpty()) {
            queryWrapper.eq("project_type", projectType);
        }

        queryWrapper.orderByDesc("created_at");

        IPage<CooperationProject> projectPage = projectMapper.selectPage(pageParam, queryWrapper);

        IPage<CooperationProjectDTO> dtoPage = new Page<>(projectPage.getCurrent(), projectPage.getSize(), projectPage.getTotal());
        List<CooperationProjectDTO> dtoList = projectPage.getRecords().stream()
                .map(this::convertToProjectDTO)
                .collect(Collectors.toList());
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    @Override
    @Transactional
    public CooperationProjectDTO reviewProject(Long teacherId, Long projectId, String status, String comment) {
        CooperationProject project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }
        if (!"submitted".equals(project.getStatus()) && !"reviewing".equals(project.getStatus())) {
            throw new RuntimeException("项目状态不允许审核");
        }

        project.setStatus(status);
        project.setReviewTime(LocalDateTime.now());
        project.setReviewedBy(teacherId);
        project.setReviewComment(comment);

        projectMapper.updateById(project);

        return convertToProjectDTO(project);
    }

    @Override
    @Transactional
    public void deleteProject(Long companyId, Long projectId) {
        CooperationProject project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }
        if (!project.getCompanyId().equals(companyId)) {
            throw new RuntimeException("无权限操作此项目");
        }
        if (!"draft".equals(project.getStatus())) {
            throw new RuntimeException("只能删除草稿状态的项目");
        }

        projectMapper.deleteById(projectId);
    }

    // ==================== 实训基地管理 ====================

    @Override
    @Transactional
    public TrainingBaseDTO createTrainingBase(Long projectId, TrainingBaseDTO baseDTO) {
        try {
            CooperationProject project = projectMapper.selectById(projectId);
            if (project == null) {
                throw new RuntimeException("项目不存在");
            }

            TrainingBase base = new TrainingBase();
            BeanUtils.copyProperties(baseDTO, base);
            base.setProjectId(projectId);
            base.setConstructionStatus("planning");

            trainingBaseMapper.insert(base);

            return convertToTrainingBaseDTO(base);
        } catch (Exception e) {
            log.error("创建实训基地失败: projectId={}, error={}", projectId, e.getMessage());
            throw new RuntimeException("创建实训基地失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public TrainingBaseDTO updateTrainingBase(Long baseId, TrainingBaseDTO baseDTO) {
        TrainingBase base = trainingBaseMapper.selectById(baseId);
        if (base == null) {
            throw new RuntimeException("实训基地不存在");
        }

        BeanUtils.copyProperties(baseDTO, base, "id", "projectId", "createdAt");
        trainingBaseMapper.updateById(base);

        return convertToTrainingBaseDTO(base);
    }

    @Override
    public TrainingBaseDTO getTrainingBaseDetail(Long baseId) {
        TrainingBase base = trainingBaseMapper.selectById(baseId);
        if (base == null) {
            throw new RuntimeException("实训基地不存在");
        }
        return convertToTrainingBaseDTO(base);
    }

    @Override
    public List<TrainingBaseDTO> getTrainingBaseList(Long projectId, String constructionStatus) {
        QueryWrapper<TrainingBase> queryWrapper = new QueryWrapper<>();

        if (projectId != null) {
            queryWrapper.eq("project_id", projectId);
        }
        if (constructionStatus != null && !constructionStatus.isEmpty()) {
            queryWrapper.eq("construction_status", constructionStatus);
        }

        queryWrapper.orderByDesc("created_at");

        List<TrainingBase> bases = trainingBaseMapper.selectList(queryWrapper);
        return bases.stream()
                .map(this::convertToTrainingBaseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteTrainingBase(Long baseId) {
        TrainingBase base = trainingBaseMapper.selectById(baseId);
        if (base == null) {
            throw new RuntimeException("实训基地不存在");
        }
        trainingBaseMapper.deleteById(baseId);
    }

    // ==================== 订单班管理 ====================

    @Override
    @Transactional
    public OrderClassDTO createOrderClass(Long projectId, OrderClassDTO classDTO) {
        try {
            CooperationProject project = projectMapper.selectById(projectId);
            if (project == null) {
                throw new RuntimeException("项目不存在");
            }

            OrderClass orderClass = new OrderClass();
            BeanUtils.copyProperties(classDTO, orderClass);
            orderClass.setProjectId(projectId);
            orderClass.setClassStatus("recruiting");
            orderClass.setEnrolledCount(0);

            orderClassMapper.insert(orderClass);

            return convertToOrderClassDTO(orderClass);
        } catch (Exception e) {
            log.error("创建订单班失败: projectId={}, error={}", projectId, e.getMessage());
            throw new RuntimeException("创建订单班失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public OrderClassDTO updateOrderClass(Long classId, OrderClassDTO classDTO) {
        OrderClass orderClass = orderClassMapper.selectById(classId);
        if (orderClass == null) {
            throw new RuntimeException("订单班不存在");
        }

        BeanUtils.copyProperties(classDTO, orderClass, "id", "projectId", "createdAt");
        orderClassMapper.updateById(orderClass);

        return convertToOrderClassDTO(orderClass);
    }

    @Override
    public OrderClassDTO getOrderClassDetail(Long classId) {
        OrderClass orderClass = orderClassMapper.selectById(classId);
        if (orderClass == null) {
            throw new RuntimeException("订单班不存在");
        }
        return convertToOrderClassDTO(orderClass);
    }

    @Override
    public List<OrderClassDTO> getOrderClassList(Long projectId, String classStatus) {
        QueryWrapper<OrderClass> queryWrapper = new QueryWrapper<>();

        if (projectId != null) {
            queryWrapper.eq("project_id", projectId);
        }
        if (classStatus != null && !classStatus.isEmpty()) {
            queryWrapper.eq("class_status", classStatus);
        }

        queryWrapper.orderByDesc("created_at");

        List<OrderClass> classes = orderClassMapper.selectList(queryWrapper);
        return classes.stream()
                .map(this::convertToOrderClassDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteOrderClass(Long classId) {
        OrderClass orderClass = orderClassMapper.selectById(classId);
        if (orderClass == null) {
            throw new RuntimeException("订单班不存在");
        }
        orderClassMapper.deleteById(classId);
    }

    // ==================== 合作案例库 ====================

    @Override
    @Transactional
    public CooperationCaseDTO createCase(CooperationCaseDTO caseDTO) {
        try {
            CooperationCase cooperationCase = new CooperationCase();
            BeanUtils.copyProperties(caseDTO, cooperationCase);
            cooperationCase.setStatus("draft");
            cooperationCase.setViewCount(0);
            cooperationCase.setIsFeatured(false);

            caseMapper.insert(cooperationCase);

            return convertToCaseDTO(cooperationCase);
        } catch (Exception e) {
            log.error("创建合作案例失败: error={}", e.getMessage());
            throw new RuntimeException("创建合作案例失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public CooperationCaseDTO updateCase(Long caseId, CooperationCaseDTO caseDTO) {
        CooperationCase cooperationCase = caseMapper.selectById(caseId);
        if (cooperationCase == null) {
            throw new RuntimeException("案例不存在");
        }

        BeanUtils.copyProperties(caseDTO, cooperationCase, "id", "viewCount", "createdAt");
        caseMapper.updateById(cooperationCase);

        return convertToCaseDTO(cooperationCase);
    }

    @Override
    public CooperationCaseDTO getCaseDetail(Long caseId) {
        CooperationCase cooperationCase = caseMapper.selectById(caseId);
        if (cooperationCase == null) {
            throw new RuntimeException("案例不存在");
        }
        return convertToCaseDTO(cooperationCase);
    }

    @Override
    public IPage<CooperationCaseDTO> getCaseList(String cooperationType, Boolean isFeatured, Integer page, Integer size) {
        Page<CooperationCase> pageParam = new Page<>(page, size);
        QueryWrapper<CooperationCase> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("status", "published");

        if (cooperationType != null && !cooperationType.isEmpty()) {
            queryWrapper.eq("cooperation_type", cooperationType);
        }
        if (isFeatured != null) {
            queryWrapper.eq("is_featured", isFeatured);
        }

        queryWrapper.orderByDesc("is_featured", "view_count", "created_at");

        IPage<CooperationCase> casePage = caseMapper.selectPage(pageParam, queryWrapper);

        IPage<CooperationCaseDTO> dtoPage = new Page<>(casePage.getCurrent(), casePage.getSize(), casePage.getTotal());
        List<CooperationCaseDTO> dtoList = casePage.getRecords().stream()
                .map(this::convertToCaseDTO)
                .collect(Collectors.toList());
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    @Override
    @Transactional
    public CooperationCaseDTO publishCase(Long caseId) {
        CooperationCase cooperationCase = caseMapper.selectById(caseId);
        if (cooperationCase == null) {
            throw new RuntimeException("案例不存在");
        }

        cooperationCase.setStatus("published");
        caseMapper.updateById(cooperationCase);

        return convertToCaseDTO(cooperationCase);
    }

    @Override
    @Transactional
    public void deleteCase(Long caseId) {
        CooperationCase cooperationCase = caseMapper.selectById(caseId);
        if (cooperationCase == null) {
            throw new RuntimeException("案例不存在");
        }
        caseMapper.deleteById(caseId);
    }

    @Override
    @Transactional
    public void incrementCaseViewCount(Long caseId) {
        CooperationCase cooperationCase = caseMapper.selectById(caseId);
        if (cooperationCase != null) {
            cooperationCase.setViewCount(cooperationCase.getViewCount() + 1);
            caseMapper.updateById(cooperationCase);
        }
    }

    // ==================== 统计分析 ====================

    @Override
    public Map<String, Object> getCooperationStats(Long companyId) {
        Map<String, Object> stats = new HashMap<>();

        QueryWrapper<CooperationProject> projectWrapper = new QueryWrapper<>();
        if (companyId != null) {
            projectWrapper.eq("company_id", companyId);
        }

        // 项目统计
        long totalProjects = projectMapper.selectCount(projectWrapper);
        stats.put("totalProjects", totalProjects);

        long draftProjects = projectMapper.selectCount(projectWrapper.clone().eq("status", "draft"));
        stats.put("draftProjects", draftProjects);

        long submittedProjects = projectMapper.selectCount(projectWrapper.clone().eq("status", "submitted"));
        stats.put("submittedProjects", submittedProjects);

        long approvedProjects = projectMapper.selectCount(projectWrapper.clone().eq("status", "approved"));
        stats.put("approvedProjects", approvedProjects);

        long ongoingProjects = projectMapper.selectCount(projectWrapper.clone().eq("status", "ongoing"));
        stats.put("ongoingProjects", ongoingProjects);

        // 实训基地统计
        long totalBases = trainingBaseMapper.selectCount(new QueryWrapper<>());
        stats.put("totalBases", totalBases);

        long operatingBases = trainingBaseMapper.selectCount(
            new QueryWrapper<TrainingBase>().eq("construction_status", "operating")
        );
        stats.put("operatingBases", operatingBases);

        // 订单班统计
        long totalClasses = orderClassMapper.selectCount(new QueryWrapper<>());
        stats.put("totalClasses", totalClasses);

        long trainingClasses = orderClassMapper.selectCount(
            new QueryWrapper<OrderClass>().eq("class_status", "training")
        );
        stats.put("trainingClasses", trainingClasses);

        // 案例统计
        long totalCases = caseMapper.selectCount(
            new QueryWrapper<CooperationCase>().eq("status", "published")
        );
        stats.put("totalCases", totalCases);

        return stats;
    }

    // ==================== 私有转换方法 ====================

    private CooperationProjectDTO convertToProjectDTO(CooperationProject project) {
        CooperationProjectDTO dto = new CooperationProjectDTO();
        BeanUtils.copyProperties(project, dto);

        // 查询企业信息
        Company company = companyMapper.selectById(project.getCompanyId());
        if (company != null) {
            dto.setCompanyName(company.getCompanyName());
        }

        // 查询审核人信息
        if (project.getReviewedBy() != null) {
            Teacher teacher = teacherMapper.selectOne(
                new QueryWrapper<Teacher>().eq("user_id", project.getReviewedBy())
            );
            if (teacher != null) {
                dto.setReviewerName(teacher.getRealName());
            }
        }

        return dto;
    }

    private TrainingBaseDTO convertToTrainingBaseDTO(TrainingBase base) {
        TrainingBaseDTO dto = new TrainingBaseDTO();
        BeanUtils.copyProperties(base, dto);

        // 查询项目信息
        CooperationProject project = projectMapper.selectById(base.getProjectId());
        if (project != null) {
            dto.setProjectName(project.getProjectName());
        }

        return dto;
    }

    private OrderClassDTO convertToOrderClassDTO(OrderClass orderClass) {
        OrderClassDTO dto = new OrderClassDTO();
        BeanUtils.copyProperties(orderClass, dto);

        // 查询项目信息
        CooperationProject project = projectMapper.selectById(orderClass.getProjectId());
        if (project != null) {
            dto.setProjectName(project.getProjectName());
        }

        // 查询企业信息
        Company company = companyMapper.selectById(orderClass.getCompanyId());
        if (company != null) {
            dto.setCompanyName(company.getCompanyName());
        }

        return dto;
    }

    private CooperationCaseDTO convertToCaseDTO(CooperationCase cooperationCase) {
        CooperationCaseDTO dto = new CooperationCaseDTO();
        BeanUtils.copyProperties(cooperationCase, dto);
        return dto;
    }
}
