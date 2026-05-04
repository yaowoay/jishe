package com.aiinterview.mapper;

import com.aiinterview.model.entity.teacher.ActivityRegistration;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 活动报名签到Mapper接口
 */
@Mapper
public interface ActivityRegistrationMapper extends BaseMapper<ActivityRegistration> {
}
