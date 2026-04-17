package com.aiinterview.service;

import com.aiinterview.entity.Recruitment;
import java.util.List;

/**
 * 招聘职位Service接口
 */
public interface RecruitmentService {

    /**
     * 查询所有职位
     */
    List<Recruitment> getAllRecruitments();

    /**
     * 根据ID查询职位
     */
    Recruitment getRecruitmentById(Long jobId);

    /**
     * 根据城市查询职位
     */
    List<Recruitment> getRecruitmentsByCity(String city);

    /**
     * 根据公司查询职位
     */
    List<Recruitment> getRecruitmentsByCompany(String companyName);

    /**
     * 根据职位名称模糊查询
     */
    List<Recruitment> searchRecruitments(String positionName);

    /**
     * 根据薪资范围查询
     */
    List<Recruitment> getRecruitmentsBySalaryRange(Double minSalary, Double maxSalary);

    /**
     * 新增职位
     */
    boolean addRecruitment(Recruitment recruitment);

    /**
     * 更新职位
     */
    boolean updateRecruitment(Recruitment recruitment);

    /**
     * 删除职位
     */
    boolean deleteRecruitment(Long jobId);

    /**
     * 分页查询职位
     */
    List<Recruitment> getRecruitmentsByPage(int pageNum, int pageSize);

    /**
     * 获取职位总数
     */
    long getTotalCount();
}
