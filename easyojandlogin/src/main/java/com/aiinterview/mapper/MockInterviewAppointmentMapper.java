package com.aiinterview.mapper;

import com.aiinterview.model.entity.teacher.MockInterviewAppointment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 模拟面试预约Mapper接口
 */
@Mapper
public interface MockInterviewAppointmentMapper extends BaseMapper<MockInterviewAppointment> {
}
