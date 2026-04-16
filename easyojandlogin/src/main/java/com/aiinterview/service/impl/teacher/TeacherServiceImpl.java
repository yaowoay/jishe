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
        dto.setTotalStudents(studentProfileMapper.selectCount(new QueryWrapper<>()));
        dto.setEmployedStudents(employmentLedgerMapper.selectCount(new QueryWrapper<EmploymentLedger>().eq("employment_status", "已就业")));
        dto.setPendingStudents(employmentLedgerMapper.selectCount(new QueryWrapper<EmploymentLedger>().in("employment_status", "待就业", "求职中", "未落实")));
        dto.setWarningStudents(earlyWarningResultMapper.selectCount(new QueryWrapper<com.aiinterview.model.entity.teacher.EarlyWarningResult>().in("handle_status", "pending", "processing")));
        dto.setTotalCompanies(companyMapper.selectCount(new QueryWrapper<>()));
        dto.setPendingCompanyVerifications(companyMapper.selectCount(new QueryWrapper<Company>().eq("verify_status", "pending")));
        dto.setTotalActivities(0L);
        dto.setCooperationApplications(0L);
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
        Company company = companyMapper.selectById(companyId);
        if (company == null) {
            throw new RuntimeException("企业不存在");
        }
        company.setVerifyStatus(verifyStatus);
        company.setVerifyRemark(remark);
        if ("approved".equals(verifyStatus) && company.getCreditScore() == null) {
            company.setCreditScore(80);
        }
        companyMapper.updateById(company);
        return companyMapper.selectById(companyId);
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
    public com.aiinterview.model.entity.teacher.Activity createActivity(Long userId, com.aiinterview.model.entity.teacher.Activity activity) {
        activity.setTeacherId(userId);
        if (activity.getStatus() == null) {
            activity.setStatus("draft");
        }
        if (activity.getCurrentParticipants() == null) {
            activity.setCurrentParticipants(0);
        }
        activityMapper.insert(activity);
        return activity;
    }

    @Override
    public List<com.aiinterview.model.entity.teacher.Activity> getActivities(Long userId) {
        return activityMapper.selectList(new QueryWrapper<com.aiinterview.model.entity.teacher.Activity>()
                .eq("teacher_id", userId)
                .orderByDesc("created_at"));
    }

    @Override
    public com.aiinterview.model.entity.teacher.Activity getActivityById(Long activityId) {
        return activityMapper.selectById(activityId);
    }

    @Override
    public com.aiinterview.model.dto.teacher.TeacherDashboardDTO getEmploymentStats(Long userId) {
        return getDashboard(userId);
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
