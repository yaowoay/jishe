package com.aiinterview.mapper;

import com.aiinterview.model.dto.applicant.ApplicantDetailDTO;
import com.aiinterview.model.dto.application.ApplicationDetailDTO;
import com.aiinterview.model.entity.application.Application;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 申请Mapper接口
 */
@Mapper
public interface ApplicationMapper extends BaseMapper<Application> {

    /**
     * 分页查询公司的应聘者列表
     */
    List<ApplicantDetailDTO> selectApplicantsByCompany(
        Page<ApplicantDetailDTO> page,
        @Param("companyId") Long companyId,
        @Param("status") String status,
        @Param("keyword") String keyword
    );

    /**
     * 查询应聘者详细信息
     */
    ApplicantDetailDTO selectApplicantDetail(
        @Param("applicationId") Long applicationId,
        @Param("companyId") Long companyId
    );

    /**
     * 查询用户的投递记录详细信息
     */
    List<ApplicationDetailDTO> selectApplicationDetailsByUserId(@Param("userId") Long userId);

    /**
     * 统计公司指定状态的申请数量
     */
    @Select("SELECT COUNT(*) FROM applications a " +
            "INNER JOIN jobs j ON a.job_id = j.job_id " +
            "WHERE j.company_id = #{companyId} AND a.status = #{status}")
    Integer countByCompanyAndStatus(@Param("companyId") Long companyId, @Param("status") String status);

    /**
     * 验证申请是否属于指定公司
     */
    @Select("SELECT COUNT(*) FROM applications a " +
            "INNER JOIN jobs j ON a.job_id = j.job_id " +
            "WHERE a.application_id = #{applicationId} AND j.company_id = #{companyId}")
    Integer countApplicationByCompany(@Param("applicationId") Long applicationId, @Param("companyId") Long companyId);
}
