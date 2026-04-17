package com.aiinterview.service.impl.applicant;

import com.aiinterview.mapper.ResumerMapper;
import com.aiinterview.mapper.StudentProfileMapper;
import com.aiinterview.model.dto.applicant.ApplicantProfileDTO;
import com.aiinterview.model.entity.resumer;
import com.aiinterview.model.entity.student.StudentProfile;
import com.aiinterview.service.applicat.ApplicantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 求职者信息服务实现类
 * 修复：已完全移除 Applicant 实体，改用 StudentProfile + Resume + User 组合
 * 说明：使用 resumer 实体对应 resume 表（在线简历编辑）
 *      使用 StudentProfile 实体对应 student_profile 表（学生基本信息）
 *      使用 user_id 作为唯一标识
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicantServiceImpl implements ApplicantService {

    private final StudentProfileMapper studentProfileMapper;
    private final ResumerMapper resumerMapper;

    @Override
    public ApplicantProfileDTO getApplicantByUserId(Long userId) {
        try {
            StudentProfile student = studentProfileMapper.selectByUserId(userId);
            resumer resume = resumerMapper.selectDefaultByUserId(userId);

            if (student == null && resume == null) {
                return null;
            }

            ApplicantProfileDTO dto = new ApplicantProfileDTO();

            if (student != null) {
                dto.setFullName(student.getRealName());
                dto.setGender(student.getGender());
                dto.setEducationLevel(student.getEducationLevel());
            }

            if (resume != null) {
                dto.setExpectedPosition(resume.getPosition());
                dto.setWorkYears(resume.getWorkYears());
                dto.setPhone(resume.getPhone());
            }

            return dto;
        } catch (Exception e) {
            log.error("获取求职者信息失败: userId={}, error={}", userId, e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional
    public ApplicantProfileDTO saveOrUpdateApplicant(Long userId, ApplicantProfileDTO dto) {
        try {
            StudentProfile student = studentProfileMapper.selectByUserId(userId);
            if (student == null) {
                student = new StudentProfile();
                student.setUserId(userId);
            }
            student.setRealName(dto.getFullName());
            student.setGender(dto.getGender());
            student.setEducationLevel(dto.getEducationLevel());

            if (student.getStudentId() == null) {
                studentProfileMapper.insert(student);
            } else {
                studentProfileMapper.updateById(student);
            }

            resumer resume = resumerMapper.selectDefaultByUserId(userId);
            if (resume == null) {
                resume = new resumer();
                resume.setUserId(userId);
                resume.setIsDefault(true);
            }
            resume.setPosition(dto.getExpectedPosition());
            resume.setWorkYears(dto.getWorkYears());
            resume.setPhone(dto.getPhone());

            if (resume.getId() == null) {
                resumerMapper.insert(resume);
            } else {
                resumerMapper.updateById(resume);
            }

            log.info("求职者信息保存成功: userId={}", userId);
            return dto;
        } catch (Exception e) {
            log.error("保存求职者信息失败: userId={}, error={}", userId, e.getMessage());
            throw new RuntimeException("保存求职者信息失败: " + e.getMessage());
        }
    }

    @Override
    public boolean hasApplicantProfile(Long userId) {
        try {
            StudentProfile student = studentProfileMapper.selectByUserId(userId);
            return student != null;
        } catch (Exception e) {
            log.error("检查求职者档案失败: userId={}, error={}", userId, e.getMessage());
            return false;
        }
    }

    //@Override
    //public Applicant getApplicantEntityByUserId(Long userId) {
        //log.warn("调用已废弃的方法 getApplicantEntityByUserId，Applicant 实体已不再使用");
        //return null;
    //}
}
