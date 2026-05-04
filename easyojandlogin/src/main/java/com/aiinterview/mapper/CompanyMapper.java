package com.aiinterview.mapper;

import com.aiinterview.model.entity.company.Company;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 公司Mapper接口
 */
@Mapper
public interface CompanyMapper extends BaseMapper<Company> {
}
