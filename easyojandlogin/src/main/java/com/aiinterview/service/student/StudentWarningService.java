package com.aiinterview.service.student;

import com.aiinterview.model.dto.teacher.EarlyWarningDTO;

import java.util.List;
import java.util.Map;

/**
 * 学生端预警服务接口
 */
public interface StudentWarningService {

    /**
     * 获取学生的预警列表
     */
    List<EarlyWarningDTO> getMyWarnings(Long studentId, String warningLevel, String handleStatus);

    /**
     * 获取预警详情
     */
    EarlyWarningDTO getWarningDetail(Long warningId, Long studentId);

    /**
     * 学生查看预警（记录查看时间）
     */
    EarlyWarningDTO viewWarning(Long warningId, Long studentId);

    /**
     * 学生回应预警
     */
    EarlyWarningDTO respondToWarning(Long warningId, Long studentId, String response);

    /**
     * 获取学生的预警统计
     */
    Map<String, Object> getWarningStats(Long studentId);

    /**
     * 获取未查看的预警数量
     */
    long getUnviewedWarningCount(Long studentId);
}
