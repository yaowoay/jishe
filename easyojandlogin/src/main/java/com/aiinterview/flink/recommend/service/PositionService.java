package com.aiinterview.flink.recommend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.aiinterview.flink.recommend.entity.Position;
import java.util.List;

/**
 * 职位服务接口，继承MyBatis-Plus IService
 */
public interface PositionService extends IService<Position> {
    /**
     * 获取所有职位（用于推荐匹配）
     */
    List<Position> getAllPositions();

    /**
     * 获取所有职位并补齐公司行业（用于行业匹配）
     */
    List<Position> getAllPositionsWithIndustry();
}
