package com.aiinterview.service.impl.student;

import com.aiinterview.mapper.EarlyWarningResultMapper;
import com.aiinterview.mapper.StudentProfileMapper;
import com.aiinterview.mapper.TeacherMapper;
import com.aiinterview.model.dto.teacher.EarlyWarningDTO;
import com.aiinterview.model.entity.student.StudentProfile;
import com.aiinterview.model.entity.teacher.EarlyWarningResult;
import com.aiinterview.model.entity.teacher.Teacher;
import com.aiinterview.service.student.StudentWarningService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 学生端预警服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StudentWarningServiceImpl implements StudentWarningService {

    private final EarlyWarningResultMapper warningMapper;
    private final StudentProfileMapper studentProfileMapper;
    private final TeacherMapper teacherMapper;

    @Override
    public List<EarlyWarningDTO> getMyWarnings(Long studentId, String warningLevel, String handleStatus) {
        QueryWrapper<EarlyWarningResult> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId);
        
        if (warningLevel != null && !warningLevel.isEmpty()) {
            wrapper.eq("warning_level", warningLevel);
        }
        if (handleStatus != null && !handleStatus.isEmpty()) {
            wrapper.eq("handle_status", handleStatus);
        }
        
        wrapper.orderByDesc("detection_time");
        
        List<EarlyWarningResult> warnings = warningMapper.selectList(wrapper);
        return warnings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EarlyWarningDTO getWarningDetail(Long warningId, Long studentId) {
        EarlyWarningResult warning = warningMapper.selectById(warningId);
        if (warning == null) {
            throw new RuntimeException("预警记录不存在");
        }
        
        if (!warning.getStudentId().equals(studentId)) {
            throw new RuntimeException("无权限查看此预警");
        }
        
        return convertToDTO(warning);
    }

    @Override
    @Transactional
    public EarlyWarningDTO viewWarning(Long warningId, Long studentId) {
        EarlyWarningResult warning = warningMapper.selectById(warningId);
        if (warning == null) {
            throw new RuntimeException("预警记录不存在");
        }
        
        if (!warning.getStudentId().equals(studentId)) {
            throw new RuntimeException("无权限查看此预警");
        }
        
        // 记录学生查看时间
        if (warning.getStudentViewed() == null || !warning.getStudentViewed()) {
            warning.setStudentViewed(true);
            warning.setStudentViewTime(LocalDateTime.now());
            warningMapper.updateById(warning);
            log.info("学生查看预警: warningId={}, studentId={}", warningId, studentId);
        }
        
        return convertToDTO(warning);
    }

    @Override
    @Transactional
    public EarlyWarningDTO respondToWarning(Long warningId, Long studentId, String response) {
        EarlyWarningResult warning = warningMapper.selectById(warningId);
        if (warning == null) {
            throw new RuntimeException("预警记录不存在");
        }
        
        if (!warning.getStudentId().equals(studentId)) {
            throw new RuntimeException("无权限操作此预警");
        }
        
//        warning.setStudentResponse(response);
        warning.setStudentViewed(true);
        if (warning.getStudentViewTime() == null) {
            warning.setStudentViewTime(LocalDateTime.now());
        }
        
        warningMapper.updateById(warning);
        log.info("学生回应预警: warningId={}, studentId={}", warningId, studentId);
        
        return convertToDTO(warning);
    }

    @Override
    public Map<String, Object> getWarningStats(Long studentId) {
        Map<String, Object> stats = new HashMap<>();
        
        QueryWrapper<EarlyWarningResult> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId);
        
        long totalCount = warningMapper.selectCount(wrapper);
        long pendingCount = warningMapper.selectCount(wrapper.clone().eq("handle_status", "pending"));
        long processingCount = warningMapper.selectCount(wrapper.clone().eq("handle_status", "processing"));
        long resolvedCount = warningMapper.selectCount(wrapper.clone().eq("handle_status", "resolved"));
        long unviewedCount = warningMapper.selectCount(wrapper.clone().eq("student_viewed", false));
        
        stats.put("totalCount", totalCount);
        stats.put("pendingCount", pendingCount);
        stats.put("processingCount", processingCount);
        stats.put("resolvedCount", resolvedCount);
        stats.put("unviewedCount", unviewedCount);
        
        // 按预警级别统计
        long urgentCount = warningMapper.selectCount(wrapper.clone().eq("warning_level", "urgent"));
        long highCount = warningMapper.selectCount(wrapper.clone().eq("warning_level", "high"));
        long mediumCount = warningMapper.selectCount(wrapper.clone().eq("warning_level", "medium"));
        
        stats.put("urgentCount", urgentCount);
        stats.put("highCount", highCount);
        stats.put("mediumCount", mediumCount);
        
        return stats;
    }

    @Override
    public long getUnviewedWarningCount(Long studentId) {
        QueryWrapper<EarlyWarningResult> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId);
        wrapper.and(w -> w.eq("student_viewed", false).or().isNull("student_viewed"));
        return warningMapper.selectCount(wrapper);
    }

    private EarlyWarningDTO convertToDTO(EarlyWarningResult warning) {
        EarlyWarningDTO dto = new EarlyWarningDTO();
        BeanUtils.copyProperties(warning, dto);
        
        // 查询学生信息
        StudentProfile student = studentProfileMapper.selectById(warning.getStudentId());
        if (student != null) {
            dto.setStudentName(student.getRealName());
            dto.setStudentNo(student.getStudentNo());
            dto.setMajor(student.getMajor());
        }
        
        // 查询辅导员信息
        if (warning.getAssignedTo() != null) {
            Teacher teacher = teacherMapper.selectOne(new QueryWrapper<Teacher>().eq("user_id", warning.getAssignedTo()));
            if (teacher != null) {
                dto.setAssignedTeacherName(teacher.getRealName());
            }
        }
        
        return dto;
    }
}
