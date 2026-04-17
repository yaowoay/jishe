package com.aiinterview.flink.recommend.service.impl;

import com.aiinterview.flink.recommend.entity.Position;
import com.aiinterview.flink.recommend.mapper.PositionMapper;
import com.aiinterview.flink.recommend.service.PositionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 职位服务实现类, 基于MyBatis-Plus查询jobs表
 */
@Service
@Slf4j
public class PositionServiceImpl extends ServiceImpl<PositionMapper, Position> implements PositionService {

    // 注意：继承ServiceImpl后，不需要手动注入@Resource，直接使用父类的baseMapper
    // 错误代码：@Resource private PositionMapper positionMapper;

    @Override
    public List<Position> getAllPositions() {
        // 1. 直接调用父类方法，无需传入positionMapper
        // 2. queryWrapper为null时表示查询所有，也可以使用 Wrappers.emptyWrapper()
        return this.list();
        // 等价于 return this.baseMapper.selectList(null);
    }

    @Override
    public List<Position> getAllPositionsWithIndustry() {
        try {
            return this.baseMapper.selectAllWithIndustry();
        } catch (Exception e) {
            log.warn("查询岗位行业失败，回退基础岗位查询", e);
            return this.list();
        }
    }
}