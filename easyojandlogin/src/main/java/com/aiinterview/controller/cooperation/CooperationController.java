package com.aiinterview.controller.cooperation;

import com.aiinterview.common.BaseResponse;
import com.aiinterview.common.ResultUtils;
import com.aiinterview.mapper.CompanyMapper;
import com.aiinterview.model.dto.cooperation.*;
import com.aiinterview.model.entity.company.Company;
import com.aiinterview.service.cooperation.CooperationService;
import com.aiinterview.utils.JwtUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 校企合作控制器
 */
@Api(tags = "校企合作管理")
@RestController
@RequestMapping("/cooperation")
@RequiredArgsConstructor
public class CooperationController {

    private final CooperationService cooperationService;
    private final JwtUtils jwtUtils;
    private final CompanyMapper companyMapper;

    // ==================== 合作项目管理 ====================

    @ApiOperation("创建合作项目")
    @PostMapping("/projects")
    public BaseResponse<CooperationProjectDTO> createProject(
            @RequestBody CooperationProjectDTO projectDTO,
            HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        String role = getCurrentRole(request);
        if (userId == null) {
            return ResultUtils.error(401, "未登录或登录已过期");
        }
        if (!"company".equals(role)) {
            return ResultUtils.error(403, "仅企业用户可创建合作项目");
        }
        Long companyId = getCompanyIdByUserId(userId);
        CooperationProjectDTO result = cooperationService.createProject(companyId, projectDTO);
        return ResultUtils.success(result);
    }

    @ApiOperation("更新合作项目")
    @PutMapping("/projects/{projectId}")
    public BaseResponse<CooperationProjectDTO> updateProject(
            @PathVariable Long projectId,
            @RequestBody CooperationProjectDTO projectDTO,
            HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ResultUtils.error(401, "未登录或登录已过期");
        }
        Long companyId = getCompanyIdByUserId(userId);
        CooperationProjectDTO result = cooperationService.updateProject(companyId, projectId, projectDTO);
        return ResultUtils.success(result);
    }

    @ApiOperation("提交合作项目")
    @PostMapping("/projects/{projectId}/submit")
    public BaseResponse<CooperationProjectDTO> submitProject(
            @PathVariable Long projectId,
            HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ResultUtils.error(401, "未登录或登录已过期");
        }
        Long companyId = getCompanyIdByUserId(userId);
        CooperationProjectDTO result = cooperationService.submitProject(companyId, projectId);
        return ResultUtils.success(result);
    }

    @ApiOperation("获取项目详情")
    @GetMapping("/projects/{projectId}")
    public BaseResponse<CooperationProjectDTO> getProjectDetail(@PathVariable Long projectId) {
        CooperationProjectDTO result = cooperationService.getProjectDetail(projectId);
        return ResultUtils.success(result);
    }

    @ApiOperation("获取项目列表")
    @GetMapping("/projects")
    public BaseResponse<IPage<CooperationProjectDTO>> getProjectList(
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String projectType,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        Long currentUserId = getCurrentUserId(request);
        String role = getCurrentRole(request);
        Long finalCompanyId = companyId;
        if ("company".equals(role)) {
            if (currentUserId == null) {
                return ResultUtils.error(401, "未登录或登录已过期");
            }
            finalCompanyId = getCompanyIdByUserId(currentUserId);
        }
        IPage<CooperationProjectDTO> result = cooperationService.getProjectList(finalCompanyId, status, projectType, page, size);
        return ResultUtils.success(result);
    }

    @ApiOperation("审核项目（教师端）")
    @PostMapping("/projects/{projectId}/review")
    public BaseResponse<CooperationProjectDTO> reviewProject(
            @PathVariable Long projectId,
            @RequestParam String status,
            @RequestParam(required = false) String comment,
            HttpServletRequest request) {
        Long teacherId = getCurrentUserId(request);
        String role = getCurrentRole(request);
        if (teacherId == null) {
            return ResultUtils.error(401, "未登录或登录已过期");
        }
        if (!"teacher".equals(role)) {
            return ResultUtils.error(403, "仅教师可审核项目");
        }
        CooperationProjectDTO result = cooperationService.reviewProject(teacherId, projectId, status, comment);
        return ResultUtils.success(result);
    }

    @ApiOperation("删除项目")
    @DeleteMapping("/projects/{projectId}")
    public BaseResponse<Void> deleteProject(
            @PathVariable Long projectId,
            HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ResultUtils.error(401, "未登录或登录已过期");
        }
        Long companyId = getCompanyIdByUserId(userId);
        cooperationService.deleteProject(companyId, projectId);
        return ResultUtils.success(null);
    }

    // ==================== 实训基地管理 ====================

    @ApiOperation("创建实训基地")
    @PostMapping("/training-bases")
    public BaseResponse<TrainingBaseDTO> createTrainingBase(
            @RequestParam Long projectId,
            @RequestBody TrainingBaseDTO baseDTO) {
        TrainingBaseDTO result = cooperationService.createTrainingBase(projectId, baseDTO);
        return ResultUtils.success(result);
    }

    @ApiOperation("更新实训基地")
    @PutMapping("/training-bases/{baseId}")
    public BaseResponse<TrainingBaseDTO> updateTrainingBase(
            @PathVariable Long baseId,
            @RequestBody TrainingBaseDTO baseDTO) {
        TrainingBaseDTO result = cooperationService.updateTrainingBase(baseId, baseDTO);
        return ResultUtils.success(result);
    }

    @ApiOperation("获取实训基地详情")
    @GetMapping("/training-bases/{baseId}")
    public BaseResponse<TrainingBaseDTO> getTrainingBaseDetail(@PathVariable Long baseId) {
        TrainingBaseDTO result = cooperationService.getTrainingBaseDetail(baseId);
        return ResultUtils.success(result);
    }

    @ApiOperation("获取实训基地列表")
    @GetMapping("/training-bases")
    public BaseResponse<List<TrainingBaseDTO>> getTrainingBaseList(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) String constructionStatus) {
        List<TrainingBaseDTO> result = cooperationService.getTrainingBaseList(projectId, constructionStatus);
        return ResultUtils.success(result);
    }

    @ApiOperation("删除实训基地")
    @DeleteMapping("/training-bases/{baseId}")
    public BaseResponse<Void> deleteTrainingBase(@PathVariable Long baseId) {
        cooperationService.deleteTrainingBase(baseId);
        return ResultUtils.success(null);
    }

    // ==================== 订单班管理 ====================

    @ApiOperation("创建订单班")
    @PostMapping("/order-classes")
    public BaseResponse<OrderClassDTO> createOrderClass(
            @RequestParam Long projectId,
            @RequestBody OrderClassDTO classDTO) {
        OrderClassDTO result = cooperationService.createOrderClass(projectId, classDTO);
        return ResultUtils.success(result);
    }

    @ApiOperation("更新订单班")
    @PutMapping("/order-classes/{classId}")
    public BaseResponse<OrderClassDTO> updateOrderClass(
            @PathVariable Long classId,
            @RequestBody OrderClassDTO classDTO) {
        OrderClassDTO result = cooperationService.updateOrderClass(classId, classDTO);
        return ResultUtils.success(result);
    }

    @ApiOperation("获取订单班详情")
    @GetMapping("/order-classes/{classId}")
    public BaseResponse<OrderClassDTO> getOrderClassDetail(@PathVariable Long classId) {
        OrderClassDTO result = cooperationService.getOrderClassDetail(classId);
        return ResultUtils.success(result);
    }

    @ApiOperation("获取订单班列表")
    @GetMapping("/order-classes")
    public BaseResponse<List<OrderClassDTO>> getOrderClassList(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) String classStatus) {
        List<OrderClassDTO> result = cooperationService.getOrderClassList(projectId, classStatus);
        return ResultUtils.success(result);
    }

    @ApiOperation("删除订单班")
    @DeleteMapping("/order-classes/{classId}")
    public BaseResponse<Void> deleteOrderClass(@PathVariable Long classId) {
        cooperationService.deleteOrderClass(classId);
        return ResultUtils.success(null);
    }

    // ==================== 合作案例库 ====================

    @ApiOperation("创建合作案例")
    @PostMapping("/cases")
    public BaseResponse<CooperationCaseDTO> createCase(@RequestBody CooperationCaseDTO caseDTO) {
        CooperationCaseDTO result = cooperationService.createCase(caseDTO);
        return ResultUtils.success(result);
    }

    @ApiOperation("更新合作案例")
    @PutMapping("/cases/{caseId}")
    public BaseResponse<CooperationCaseDTO> updateCase(
            @PathVariable Long caseId,
            @RequestBody CooperationCaseDTO caseDTO) {
        CooperationCaseDTO result = cooperationService.updateCase(caseId, caseDTO);
        return ResultUtils.success(result);
    }

    @ApiOperation("获取案例详情")
    @GetMapping("/cases/{caseId}")
    public BaseResponse<CooperationCaseDTO> getCaseDetail(@PathVariable Long caseId) {
        cooperationService.incrementCaseViewCount(caseId);
        CooperationCaseDTO result = cooperationService.getCaseDetail(caseId);
        return ResultUtils.success(result);
    }

    @ApiOperation("获取案例列表")
    @GetMapping("/cases")
    public BaseResponse<IPage<CooperationCaseDTO>> getCaseList(
            @RequestParam(required = false) String cooperationType,
            @RequestParam(required = false) Boolean isFeatured,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        IPage<CooperationCaseDTO> result = cooperationService.getCaseList(cooperationType, isFeatured, page, size);
        return ResultUtils.success(result);
    }

    @ApiOperation("发布案例")
    @PostMapping("/cases/{caseId}/publish")
    public BaseResponse<CooperationCaseDTO> publishCase(@PathVariable Long caseId) {
        CooperationCaseDTO result = cooperationService.publishCase(caseId);
        return ResultUtils.success(result);
    }

    @ApiOperation("删除案例")
    @DeleteMapping("/cases/{caseId}")
    public BaseResponse<Void> deleteCase(@PathVariable Long caseId) {
        cooperationService.deleteCase(caseId);
        return ResultUtils.success(null);
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        try {
            return jwtUtils.getUserIdFromToken(token.substring(7));
        } catch (Exception e) {
            return null;
        }
    }

    private String getCurrentRole(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        try {
            return jwtUtils.getRoleFromToken(token.substring(7));
        } catch (Exception e) {
            return null;
        }
    }

    private Long getCompanyIdByUserId(Long userId) {
        QueryWrapper<Company> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        Company company = companyMapper.selectOne(queryWrapper);
        if (company == null) {
            throw new RuntimeException("未找到企业信息，请先完善企业资料");
        }
        return company.getCompanyId();
    }



    // ==================== 统计分析 ====================

    @ApiOperation("获取合作统计")
    @GetMapping("/stats")
    public BaseResponse<Map<String, Object>> getCooperationStats(
            @RequestParam(required = false) Long companyId) {
        Map<String, Object> result = cooperationService.getCooperationStats(companyId);
        return ResultUtils.success(result);
    }
}
