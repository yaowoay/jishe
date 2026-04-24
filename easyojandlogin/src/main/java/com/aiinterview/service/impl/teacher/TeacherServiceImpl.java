package com.aiinterview.service.impl.teacher;

import com.aiinterview.mapper.ActivityMapper;
import com.aiinterview.mapper.AssistanceRecordMapper;
import com.aiinterview.mapper.CompanyMapper;
import com.aiinterview.mapper.CooperationApplicationMapper;
import com.aiinterview.mapper.EarlyWarningResultMapper;
import com.aiinterview.mapper.EmploymentLedgerMapper;
import com.aiinterview.mapper.JobMapper;
import com.aiinterview.mapper.StudentProfileMapper;
import com.aiinterview.mapper.TeacherMapper;
import com.aiinterview.model.dto.teacher.AssistanceRecordDTO;
import com.aiinterview.model.dto.teacher.EmploymentAuditDTO;
import com.aiinterview.model.dto.teacher.StudentQueryDTO;
import com.aiinterview.model.dto.teacher.TeacherDashboardDTO;
import com.aiinterview.model.dto.teacher.TeacherProfileDTO;
import com.aiinterview.model.entity.company.Company;
import com.aiinterview.model.entity.job.Job;
import com.aiinterview.model.entity.teacher.AssistanceRecord;
import com.aiinterview.model.entity.teacher.EmploymentLedger;
import com.aiinterview.model.entity.student.StudentProfile;
import com.aiinterview.model.entity.teacher.Teacher;
import com.aiinterview.service.teacher.TeacherService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 教师端服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final TeacherMapper teacherMapper;
    private final StudentProfileMapper studentProfileMapper;
    private final EmploymentLedgerMapper employmentLedgerMapper;
    private final CompanyMapper companyMapper;
    private final JobMapper jobMapper;
    private final AssistanceRecordMapper assistanceRecordMapper;
    private final EarlyWarningResultMapper earlyWarningResultMapper;
    private final ActivityMapper activityMapper;
    private final CooperationApplicationMapper cooperationApplicationMapper;

    @Override
    public TeacherProfileDTO getTeacherProfile(Long userId) {
        Teacher teacher = getTeacherEntityByUserId(userId);
        if (teacher == null) {
            return null;
        }
        TeacherProfileDTO dto = new TeacherProfileDTO();
        BeanUtils.copyProperties(teacher, dto);
        return dto;
    }

    @Override
    @Transactional
    public TeacherProfileDTO saveOrUpdateTeacherProfile(Long userId, TeacherProfileDTO teacherProfileDTO) {
        try {
            Teacher teacher = getTeacherEntityByUserId(userId);
            if (teacher == null) {
                teacher = new Teacher();
                BeanUtils.copyProperties(teacherProfileDTO, teacher);
                teacher.setUserId(userId);
                if (teacher.getStatus() == null) {
                    teacher.setStatus("active");
                }
                teacherMapper.insert(teacher);
            } else {
                BeanUtils.copyProperties(teacherProfileDTO, teacher, "teacherId", "userId", "status");
                teacherMapper.updateById(teacher);
            }
            TeacherProfileDTO result = new TeacherProfileDTO();
            BeanUtils.copyProperties(teacher, result);
            return result;
        } catch (Exception e) {
            log.error("保存教师信息失败: userId={}, error={}", userId, e.getMessage());
            throw new RuntimeException("保存教师信息失败");
        }
    }

    @Override
    public TeacherDashboardDTO getDashboard(Long userId) {
        TeacherDashboardDTO dto = new TeacherDashboardDTO();
        dto.setTotalStudents((long) studentProfileMapper.selectCount(new QueryWrapper<>()));
        dto.setEmployedStudents((long) employmentLedgerMapper.selectCount(new QueryWrapper<EmploymentLedger>().eq("employment_status", "已就业")));
        dto.setPendingStudents((long) employmentLedgerMapper.selectCount(new QueryWrapper<EmploymentLedger>().in("employment_status", "待就业", "求职中", "未落实")));
        dto.setWarningStudents((long) earlyWarningResultMapper.selectCount(new QueryWrapper<com.aiinterview.model.entity.teacher.EarlyWarningResult>().in("handle_status", "pending", "processing")));
        dto.setTotalCompanies((long) companyMapper.selectCount(new QueryWrapper<>()));
        dto.setPendingCompanyVerifications((long) companyMapper.selectCount(new QueryWrapper<Company>().eq("verify_status", "pending")));
        dto.setTotalActivities((long) activityMapper.selectCount(new QueryWrapper<>()));
        dto.setCooperationApplications((long) cooperationApplicationMapper.selectCount(new QueryWrapper<>()));
        return dto;
    }

    @Override
    public List<StudentProfile> getStudents(StudentQueryDTO queryDTO, Long userId) {
        QueryWrapper<StudentProfile> queryWrapper = new QueryWrapper<>();
        if (queryDTO.getCollegeId() != null) {
            queryWrapper.eq("college_id", queryDTO.getCollegeId());
        }
        if (queryDTO.getMajorId() != null) {
            queryWrapper.eq("major_id", queryDTO.getMajorId());
        }
        if (queryDTO.getGraduationYear() != null) {
            queryWrapper.eq("graduation_year", queryDTO.getGraduationYear());
        }
        if (queryDTO.getClassName() != null && !queryDTO.getClassName().trim().isEmpty()) {
            queryWrapper.like("class_name", queryDTO.getClassName());
        }
        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().trim().isEmpty()) {
            queryWrapper.and(wrapper -> wrapper.like("real_name", queryDTO.getKeyword()).or().like("student_no", queryDTO.getKeyword()));
        }
        queryWrapper.orderByDesc("graduation_year").orderByAsc("student_no");
        return studentProfileMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional
    public EmploymentLedger auditEmployment(Long userId, EmploymentAuditDTO auditDTO) {
        EmploymentLedger ledger = employmentLedgerMapper.selectById(auditDTO.getLedgerId());
        if (ledger == null) {
            throw new RuntimeException("就业台账不存在");
        }
        ledger.setVerifyStatus(auditDTO.getVerifyStatus());
        ledger.setEmploymentStatus(auditDTO.getEmploymentStatus());
        ledger.setCompanyName(auditDTO.getCompanyName());
        ledger.setPosition(auditDTO.getPosition());
        ledger.setSalaryRange(auditDTO.getSalaryRange());
        ledger.setEmploymentCity(auditDTO.getEmploymentCity());
        ledger.setFurtherStudySchool(auditDTO.getFurtherStudySchool());
        ledger.setVerifiedBy(userId);
        ledger.setVerifiedAt(LocalDateTime.now());
        employmentLedgerMapper.updateById(ledger);
        return employmentLedgerMapper.selectById(ledger.getId());
    }

    @Override
    public List<EmploymentLedger> getEmploymentList(Long userId) {
        return employmentLedgerMapper.selectList(new QueryWrapper<EmploymentLedger>().orderByDesc("updated_at"));
    }

    @Override
    public List<Company> getCompanyList(String verifyStatus) {
        QueryWrapper<Company> queryWrapper = new QueryWrapper<>();
        if (verifyStatus != null && !verifyStatus.trim().isEmpty()) {
            queryWrapper.eq("verify_status", verifyStatus);
        }
        queryWrapper.orderByDesc("created_at");
        return companyMapper.selectList(queryWrapper);
    }

    @Override
    public List<Job> getJobList(String verifyStatus) {
        QueryWrapper<Job> queryWrapper = new QueryWrapper<>();
        if (verifyStatus != null && !verifyStatus.trim().isEmpty()) {
            queryWrapper.eq("verify_status", verifyStatus);
        }
        queryWrapper.orderByDesc("created_at");
        return jobMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional
    public Company auditCompany(Long userId, Long companyId, String verifyStatus, String remark) {
        try {
            Company company = companyMapper.selectById(companyId);
            if (company == null) {
                throw new RuntimeException("企业不存在");
            }

            // 更新审核状态
            company.setVerifyStatus(verifyStatus);

            // 注意：companies 表中没有 verify_remark 字段，所以不保存备注
            // 备注信息只记录在日志中
            log.info("企业审核 - 企业ID: {}, 审核结果: {}, 备注: {}", companyId, verifyStatus, remark);

            // 审核通过时，设置信用评分（如果为空）
            if ("approved".equals(verifyStatus) && company.getCreditScore() == null) {
                company.setCreditScore(80);
            }

            // 更新更新时间
            company.setUpdatedAt(LocalDateTime.now());

            int result = companyMapper.updateById(company);
            if (result > 0) {
                log.info("企业审核成功，企业ID: {}, 状态: {}", companyId, verifyStatus);
            } else {
                log.warn("企业审核失败，企业ID: {}", companyId);
            }

            return companyMapper.selectById(companyId);

        } catch (Exception e) {
            log.error("企业审核失败: companyId={}, error={}", companyId, e.getMessage());
            throw new RuntimeException("企业审核失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Job auditJob(Long userId, Long jobId, String verifyStatus, String remark) {
        Job job = jobMapper.selectById(jobId);
        if (job == null) {
            throw new RuntimeException("职位不存在");
        }
        job.setVerifyStatus(verifyStatus);
        job.setVerifyRemark(remark);
        if ("rejected".equals(verifyStatus)) {
            job.setIsActive(false);
        }
        jobMapper.updateById(job);
        return jobMapper.selectById(jobId);
    }

    @Override
    @Transactional
    public AssistanceRecord saveAssistanceRecord(Long userId, AssistanceRecordDTO recordDTO) {
        AssistanceRecord record = new AssistanceRecord();
        BeanUtils.copyProperties(recordDTO, record);
        record.setTeacherId(userId);
        record.setStartDate(LocalDate.now());
        record.setFollowUpDate(LocalDate.now().plusDays(7));
        assistanceRecordMapper.insert(record);
        return record;
    }

    @Override
    public List<AssistanceRecord> getAssistanceRecords(Long userId) {
        return assistanceRecordMapper.selectList(new QueryWrapper<AssistanceRecord>().eq("teacher_id", userId).orderByDesc("created_at"));
    }

    @Override
    public Teacher getTeacherEntityByUserId(Long userId) {
        return teacherMapper.selectOne(new QueryWrapper<Teacher>().eq("user_id", userId));
    }

    @Override
    @Transactional
    public com.aiinterview.model.entity.teacher.Activity createActivity(Long userId, com.aiinterview.model.dto.teacher.ActivityDTO activityDTO) {
        Teacher teacher = getTeacherEntityByUserId(userId);
        if (teacher == null) {
            throw new RuntimeException("教师信息不存在");
        }

        com.aiinterview.model.entity.teacher.Activity activity = new com.aiinterview.model.entity.teacher.Activity();
        BeanUtils.copyProperties(activityDTO, activity);
        activity.setTeacherId(teacher.getTeacherId());
        activity.setCollegeId(teacher.getCollegeId());
        activity.setCurrentParticipants(0);
        if (activity.getStatus() == null) {
            activity.setStatus("draft");
        }

        activityMapper.insert(activity);
        return activity;
    }

    @Override
    public List<com.aiinterview.model.entity.teacher.Activity> getActivities(Long userId, String status) {
        Teacher teacher = getTeacherEntityByUserId(userId);
        if (teacher == null) {
            throw new RuntimeException("教师信息不存在");
        }

        QueryWrapper<com.aiinterview.model.entity.teacher.Activity> wrapper = new QueryWrapper<>();
        wrapper.eq("teacher_id", teacher.getTeacherId());
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("created_at");

        return activityMapper.selectList(wrapper);
    }

    @Override
    public com.aiinterview.model.entity.teacher.Activity getActivityById(Long activityId) {
        return activityMapper.selectById(activityId);
    }

    @Override
    @Transactional
    public com.aiinterview.model.entity.teacher.Activity updateActivity(Long userId, Long activityId, com.aiinterview.model.dto.teacher.ActivityDTO activityDTO) {
        Teacher teacher = getTeacherEntityByUserId(userId);
        if (teacher == null) {
            throw new RuntimeException("教师信息不存在");
        }

        com.aiinterview.model.entity.teacher.Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }

        if (!activity.getTeacherId().equals(teacher.getTeacherId())) {
            throw new RuntimeException("无权限修改此活动");
        }

        BeanUtils.copyProperties(activityDTO, activity, "activityId", "teacherId", "collegeId", "currentParticipants", "createdAt");
        activityMapper.updateById(activity);
        return activity;
    }

    @Override
    @Transactional
    public void deleteActivity(Long userId, Long activityId) {
        Teacher teacher = getTeacherEntityByUserId(userId);
        if (teacher == null) {
            throw new RuntimeException("教师信息不存在");
        }

        com.aiinterview.model.entity.teacher.Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }

        if (!activity.getTeacherId().equals(teacher.getTeacherId())) {
            throw new RuntimeException("无权限删除此活动");
        }

        activityMapper.deleteById(activityId);
    }

    @Override
    public List<com.aiinterview.model.dto.teacher.ActivityRegistrationDTO> getActivityRegistrations(Long activityId) {
        // TODO: 实现活动报名列表查询
        return new java.util.ArrayList<>();
    }

    @Override
    public com.aiinterview.model.dto.teacher.EmploymentStatsDTO getEmploymentStats(Long userId, Long collegeId, Long majorId) {
        // TODO: 实现详细的就业统计
        com.aiinterview.model.dto.teacher.EmploymentStatsDTO stats = new com.aiinterview.model.dto.teacher.EmploymentStatsDTO();

        // 基础统计 - selectCount 返回 Long，需要转换为 Integer
        Long totalStudentsLong = studentProfileMapper.selectCount(new QueryWrapper<>());
        Long employedCountLong = employmentLedgerMapper.selectCount(
            new QueryWrapper<EmploymentLedger>().eq("employment_status", "已就业"));
        Long furtherStudyCountLong = employmentLedgerMapper.selectCount(
            new QueryWrapper<EmploymentLedger>().eq("employment_status", "升学"));
        Long unemployedCountLong = employmentLedgerMapper.selectCount(
            new QueryWrapper<EmploymentLedger>().in("employment_status", "待就业", "求职中"));

        Integer totalStudents = totalStudentsLong != null ? totalStudentsLong.intValue() : 0;
        Integer employedCount = employedCountLong != null ? employedCountLong.intValue() : 0;
        Integer furtherStudyCount = furtherStudyCountLong != null ? furtherStudyCountLong.intValue() : 0;
        Integer unemployedCount = unemployedCountLong != null ? unemployedCountLong.intValue() : 0;

        stats.setTotalStudents(totalStudents);
        stats.setEmployedCount(employedCount);
        stats.setFurtherStudyCount(furtherStudyCount);
        stats.setUnemployedCount(unemployedCount);

        if (totalStudents > 0) {
            stats.setEmploymentRate((double) employedCount / totalStudents * 100);
        }

        return stats;
    }

    @Override
    public List<com.aiinterview.model.dto.teacher.EarlyWarningDTO> getEarlyWarnings(Long userId, String warningLevel, String handleStatus) {
        QueryWrapper<com.aiinterview.model.entity.teacher.EarlyWarningResult> queryWrapper = new QueryWrapper<>();

        // 添加筛选条件
        if (warningLevel != null && !warningLevel.isEmpty()) {
            queryWrapper.eq("warning_level", warningLevel);
        }
        if (handleStatus != null && !handleStatus.isEmpty()) {
            queryWrapper.eq("handle_status", handleStatus);
        }

        // 按检测时间倒序排列
        queryWrapper.orderByDesc("detection_time");

        List<com.aiinterview.model.entity.teacher.EarlyWarningResult> results = earlyWarningResultMapper.selectList(queryWrapper);

        // 转换为 DTO
        return convertToEarlyWarningDTOList(results);
    }

    @Override
    @Transactional
    public com.aiinterview.model.dto.teacher.EarlyWarningDTO handleEarlyWarning(Long userId, Long warningId, String handleStatus, String handleRemark) {
        // 查询预警记录
        com.aiinterview.model.entity.teacher.EarlyWarningResult warning = earlyWarningResultMapper.selectById(warningId);
        if (warning == null) {
            throw new RuntimeException("预警记录不存在");
        }

        // 更新处理状态
        warning.setHandleStatus(handleStatus);
        warning.setHandleRemark(handleRemark);
        warning.setHandleTime(LocalDateTime.now());
        warning.setAssignedTo(userId);

        earlyWarningResultMapper.updateById(warning);

        // 返回更新后的数据
        return convertToEarlyWarningDTO(warning);
    }

    @Override
    @Transactional
    public com.aiinterview.model.dto.teacher.EarlyWarningDTO createEarlyWarning(Long userId, com.aiinterview.model.dto.teacher.EarlyWarningDTO warningDTO) {
        try {
            // 验证学生是否存在
            StudentProfile student = studentProfileMapper.selectById(warningDTO.getStudentId());
            if (student == null) {
                throw new RuntimeException("学生不存在");
            }

            // 创建预警记录
            com.aiinterview.model.entity.teacher.EarlyWarningResult warning = new com.aiinterview.model.entity.teacher.EarlyWarningResult();
            BeanUtils.copyProperties(warningDTO, warning);
            warning.setDetectionTime(LocalDateTime.now());
            warning.setHandleStatus("pending");
            warning.setAssignedTo(userId);
            warning.setIsNotified(true);
            warning.setNotifyTime(LocalDateTime.now());
            warning.setStudentViewed(false);

            earlyWarningResultMapper.insert(warning);

            // 返回创建的数据
            return convertToEarlyWarningDTO(warning);
        } catch (Exception e) {
            log.error("创建预警记录失败: userId={}, error={}", userId, e.getMessage());
            throw new RuntimeException("创建预警记录失败: " + e.getMessage());
        }
    }

    @Override
    public com.aiinterview.model.dto.teacher.EarlyWarningDTO getEarlyWarningDetail(Long warningId) {
        com.aiinterview.model.entity.teacher.EarlyWarningResult warning = earlyWarningResultMapper.selectById(warningId);
        if (warning == null) {
            throw new RuntimeException("预警记录不存在");
        }
        return convertToEarlyWarningDTO(warning);
    }

    @Override
    public List<com.aiinterview.model.dto.teacher.EarlyWarningDTO> getStudentWarningHistory(Long studentId) {
        QueryWrapper<com.aiinterview.model.entity.teacher.EarlyWarningResult> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId).orderByDesc("detection_time");

        List<com.aiinterview.model.entity.teacher.EarlyWarningResult> results = earlyWarningResultMapper.selectList(queryWrapper);
        return convertToEarlyWarningDTOList(results);
    }

    @Override
    @Transactional
    public int batchHandleEarlyWarnings(Long userId, List<Long> warningIds, String handleStatus, String handleRemark) {
        if (warningIds == null || warningIds.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (Long warningId : warningIds) {
            try {
                com.aiinterview.model.entity.teacher.EarlyWarningResult warning = earlyWarningResultMapper.selectById(warningId);
                if (warning != null) {
                    warning.setHandleStatus(handleStatus);
                    warning.setHandleRemark(handleRemark);
                    warning.setHandleTime(LocalDateTime.now());
                    warning.setAssignedTo(userId);
                    earlyWarningResultMapper.updateById(warning);
                    count++;
                }
            } catch (Exception e) {
                log.error("批量处理预警失败: warningId={}, error={}", warningId, e.getMessage());
            }
        }
        return count;
    }

    @Override
    public java.util.Map<String, Object> getEarlyWarningStats(Long userId) {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();

        // 总预警数
        long totalWarnings = earlyWarningResultMapper.selectCount(new QueryWrapper<>());
        stats.put("totalWarnings", totalWarnings);

        // 待处理预警数
        long pendingWarnings = earlyWarningResultMapper.selectCount(
            new QueryWrapper<com.aiinterview.model.entity.teacher.EarlyWarningResult>()
                .eq("handle_status", "pending")
        );
        stats.put("pendingWarnings", pendingWarnings);

        // 处理中预警数
        long processingWarnings = earlyWarningResultMapper.selectCount(
            new QueryWrapper<com.aiinterview.model.entity.teacher.EarlyWarningResult>()
                .eq("handle_status", "processing")
        );
        stats.put("processingWarnings", processingWarnings);

        // 已解决预警数
        long resolvedWarnings = earlyWarningResultMapper.selectCount(
            new QueryWrapper<com.aiinterview.model.entity.teacher.EarlyWarningResult>()
                .eq("handle_status", "resolved")
        );
        stats.put("resolvedWarnings", resolvedWarnings);

        // 按预警等级统计
        java.util.Map<String, Long> levelStats = new java.util.HashMap<>();
        levelStats.put("low", earlyWarningResultMapper.selectCount(
            new QueryWrapper<com.aiinterview.model.entity.teacher.EarlyWarningResult>()
                .eq("warning_level", "low")
        ));
        levelStats.put("medium", earlyWarningResultMapper.selectCount(
            new QueryWrapper<com.aiinterview.model.entity.teacher.EarlyWarningResult>()
                .eq("warning_level", "medium")
        ));
        levelStats.put("high", earlyWarningResultMapper.selectCount(
            new QueryWrapper<com.aiinterview.model.entity.teacher.EarlyWarningResult>()
                .eq("warning_level", "high")
        ));
        levelStats.put("urgent", earlyWarningResultMapper.selectCount(
            new QueryWrapper<com.aiinterview.model.entity.teacher.EarlyWarningResult>()
                .eq("warning_level", "urgent")
        ));
        stats.put("levelStats", levelStats);

        // 按预警类型统计
        java.util.Map<String, Long> typeStats = new java.util.HashMap<>();
        typeStats.put("employment", earlyWarningResultMapper.selectCount(
            new QueryWrapper<com.aiinterview.model.entity.teacher.EarlyWarningResult>()
                .eq("warning_type", "employment")
        ));
        typeStats.put("skill", earlyWarningResultMapper.selectCount(
            new QueryWrapper<com.aiinterview.model.entity.teacher.EarlyWarningResult>()
                .eq("warning_type", "skill")
        ));
        typeStats.put("interview", earlyWarningResultMapper.selectCount(
            new QueryWrapper<com.aiinterview.model.entity.teacher.EarlyWarningResult>()
                .eq("warning_type", "interview")
        ));
        typeStats.put("resume", earlyWarningResultMapper.selectCount(
            new QueryWrapper<com.aiinterview.model.entity.teacher.EarlyWarningResult>()
                .eq("warning_type", "resume")
        ));
        stats.put("typeStats", typeStats);

        return stats;
    }

    /**
     * 转换预警实体为DTO
     */
    private com.aiinterview.model.dto.teacher.EarlyWarningDTO convertToEarlyWarningDTO(com.aiinterview.model.entity.teacher.EarlyWarningResult warning) {
        com.aiinterview.model.dto.teacher.EarlyWarningDTO dto = new com.aiinterview.model.dto.teacher.EarlyWarningDTO();
        BeanUtils.copyProperties(warning, dto);

        // 查询学生信息
        StudentProfile student = studentProfileMapper.selectById(warning.getStudentId());
        if (student != null) {
            dto.setStudentName(student.getRealName());
            dto.setStudentNo(student.getStudentNo());
            dto.setMajor(student.getMajor());
        }

        // 查询负责教师信息
        if (warning.getAssignedTo() != null) {
            Teacher teacher = teacherMapper.selectOne(
                new QueryWrapper<Teacher>().eq("user_id", warning.getAssignedTo())
            );
            if (teacher != null) {
                dto.setAssignedTeacherName(teacher.getRealName());
            }
        }

        return dto;
    }

    /**
     * 批量转换预警实体为DTO列表
     */
    private List<com.aiinterview.model.dto.teacher.EarlyWarningDTO> convertToEarlyWarningDTOList(List<com.aiinterview.model.entity.teacher.EarlyWarningResult> results) {
        List<com.aiinterview.model.dto.teacher.EarlyWarningDTO> dtoList = new java.util.ArrayList<>();
        for (com.aiinterview.model.entity.teacher.EarlyWarningResult result : results) {
            dtoList.add(convertToEarlyWarningDTO(result));
        }
        return dtoList;
    }

    @Override
    public List<com.aiinterview.model.entity.teacher.CooperationApplication> getCooperationApplications(Long userId) {
        return cooperationApplicationMapper.selectList(new QueryWrapper<com.aiinterview.model.entity.teacher.CooperationApplication>()
                .orderByDesc("created_at"));
    }

    @Override
    @Transactional
    public com.aiinterview.model.entity.teacher.CooperationApplication auditCooperationApplication(Long userId, Long applicationId, String status, String comment) {
        com.aiinterview.model.entity.teacher.CooperationApplication app = cooperationApplicationMapper.selectById(applicationId);
        if (app == null) {
            throw new RuntimeException("合作申请不存在");
        }
        app.setStatus(status);
        app.setReviewComment(comment);
        app.setReviewedBy(userId);
        if ("approved".equals(status)) {
            app.setSignedAt(LocalDateTime.now());
        }
        cooperationApplicationMapper.updateById(app);
        return cooperationApplicationMapper.selectById(applicationId);
    }
}
