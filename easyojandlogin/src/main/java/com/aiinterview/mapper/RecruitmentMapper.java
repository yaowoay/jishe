package com.aiinterview.mapper;

import com.aiinterview.entity.Recruitment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 招聘职位Mapper接口
 */
@Mapper
public interface RecruitmentMapper {

    /**
     * 查询所有职位
     */
    List<Recruitment> selectAll();

    /**
     * 根据ID查询职位
     */
    Recruitment selectById(@Param("jobId") Long jobId);

    /**
     * 根据城市查询职位
     */
    List<Recruitment> selectByCity(@Param("city") String city);

    /**
     * 根据公司查询职位
     */
    List<Recruitment> selectByCompany(@Param("companyName") String companyName);

    /**
     * 根据职位名称模糊查询
     */
    List<Recruitment> selectByPositionName(@Param("positionName") String positionName);

    /**
     * 根据薪资范围查询
     */
    List<Recruitment> selectBySalaryRange(@Param("minSalary") Double minSalary, 
                                          @Param("maxSalary") Double maxSalary);

    /**
     * 插入职位
     */
    int insert(Recruitment recruitment);

    /**
     * 更新职位
     */
    int update(Recruitment recruitment);

    /**
     * 删除职位
     */
    int delete(@Param("jobId") Long jobId);

    /**
     * 分页查询职位
     */
    List<Recruitment> selectPage(@Param("offset") int offset, 
                                 @Param("limit") int limit);

    /**
     * 统计职位总数
     */
    long countAll();
}
