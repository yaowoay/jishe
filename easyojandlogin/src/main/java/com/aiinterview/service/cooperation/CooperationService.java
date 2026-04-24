package com.aiinterview.service.cooperation;

import com.aiinterview.model.dto.cooperation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;
import java.util.Map;

/**
 * 校企合作服务接口
 */
public interface CooperationService {

    // ==================== 合作项目管理 ====================
    
    /**
     * 创建合作项目申请
     */
    CooperationProjectDTO createProject(Long companyId, CooperationProjectDTO projectDTO);

    /**
     * 更新合作项目
     */
    CooperationProjectDTO updateProject(Long companyId, Long projectId, CooperationProjectDTO projectDTO);

    /**
     * 提交合作项目申请
     */
    CooperationProjectDTO submitProject(Long companyId, Long projectId);

    /**
     * 获取合作项目详情
     */
    CooperationProjectDTO getProjectDetail(Long projectId);

    /**
     * 分页查询合作项目列表
     */
    IPage<CooperationProjectDTO> getProjectList(Long companyId, String status, String projectType, Integer page, Integer size);

    /**
     * 审核合作项目（教师端）
     */
    CooperationProjectDTO reviewProject(Long teacherId, Long projectId, String status, String comment);

    /**
     * 删除合作项目
     */
    void deleteProject(Long companyId, Long projectId);

    // ==================== 实训基地管理 ====================
    
    /**
     * 创建实训基地
     */
    TrainingBaseDTO createTrainingBase(Long projectId, TrainingBaseDTO baseDTO);

    /**
     * 更新实训基地信息
     */
    TrainingBaseDTO updateTrainingBase(Long baseId, TrainingBaseDTO baseDTO);

    /**
     * 获取实训基地详情
     */
    TrainingBaseDTO getTrainingBaseDetail(Long baseId);

    /**
     * 查询实训基地列表
     */
    List<TrainingBaseDTO> getTrainingBaseList(Long projectId, String constructionStatus);

    /**
     * 删除实训基地
     */
    void deleteTrainingBase(Long baseId);

    // ==================== 订单班管理 ====================
    
    /**
     * 创建订单班
     */
    OrderClassDTO createOrderClass(Long projectId, OrderClassDTO classDTO);

    /**
     * 更新订单班信息
     */
    OrderClassDTO updateOrderClass(Long classId, OrderClassDTO classDTO);

    /**
     * 获取订单班详情
     */
    OrderClassDTO getOrderClassDetail(Long classId);

    /**
     * 查询订单班列表
     */
    List<OrderClassDTO> getOrderClassList(Long projectId, String classStatus);

    /**
     * 删除订单班
     */
    void deleteOrderClass(Long classId);

    // ==================== 合作案例库 ====================
    
    /**
     * 创建合作案例
     */
    CooperationCaseDTO createCase(CooperationCaseDTO caseDTO);

    /**
     * 更新合作案例
     */
    CooperationCaseDTO updateCase(Long caseId, CooperationCaseDTO caseDTO);

    /**
     * 获取案例详情
     */
    CooperationCaseDTO getCaseDetail(Long caseId);

    /**
     * 分页查询案例列表
     */
    IPage<CooperationCaseDTO> getCaseList(String cooperationType, Boolean isFeatured, Integer page, Integer size);

    /**
     * 发布案例
     */
    CooperationCaseDTO publishCase(Long caseId);

    /**
     * 删除案例
     */
    void deleteCase(Long caseId);

    /**
     * 增加案例浏览量
     */
    void incrementCaseViewCount(Long caseId);

    // ==================== 统计分析 ====================
    
    /**
     * 获取合作统计数据
     */
    Map<String, Object> getCooperationStats(Long companyId);
}
