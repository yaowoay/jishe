package com.aiinterview.service.impl;

import com.aiinterview.entity.Recruitment;
import com.aiinterview.mapper.RecruitmentMapper;
import com.aiinterview.service.RecruitmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 招聘职位Service实现类
 */
@Service
public class RecruitmentServiceImpl implements RecruitmentService {

    @Autowired
    private RecruitmentMapper recruitmentMapper;

    @Override
    public List<Recruitment> getAllRecruitments() {
        return recruitmentMapper.selectAll();
    }

    @Override
    public Recruitment getRecruitmentById(Long jobId) {
        return recruitmentMapper.selectById(jobId);
    }

    @Override
    public List<Recruitment> getRecruitmentsByCity(String city) {
        return recruitmentMapper.selectByCity(city);
    }

    @Override
    public List<Recruitment> getRecruitmentsByCompany(String companyName) {
        return recruitmentMapper.selectByCompany(companyName);
    }

    @Override
    public List<Recruitment> searchRecruitments(String positionName) {
        return recruitmentMapper.selectByPositionName(positionName);
    }

    @Override
    public List<Recruitment> getRecruitmentsBySalaryRange(Double minSalary, Double maxSalary) {
        return recruitmentMapper.selectBySalaryRange(minSalary, maxSalary);
    }

    @Override
    public boolean addRecruitment(Recruitment recruitment) {
        return recruitmentMapper.insert(recruitment) > 0;
    }

    @Override
    public boolean updateRecruitment(Recruitment recruitment) {
        return recruitmentMapper.update(recruitment) > 0;
    }

    @Override
    public boolean deleteRecruitment(Long jobId) {
        return recruitmentMapper.delete(jobId) > 0;
    }

    @Override
    public List<Recruitment> getRecruitmentsByPage(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return recruitmentMapper.selectPage(offset, pageSize);
    }

    @Override
    public long getTotalCount() {
        return recruitmentMapper.countAll();
    }
}
