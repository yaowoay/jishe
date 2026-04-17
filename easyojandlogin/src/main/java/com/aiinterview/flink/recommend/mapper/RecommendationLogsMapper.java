package com.aiinterview.flink.recommend.mapper;



import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiinterview.flink.recommend.entity.RecommendationLogs;
import org.apache.ibatis.annotations.Mapper;

/**
 * 推荐日志Mapper：操作recommendation_logs表，写入推荐结果
 */
@Mapper
public interface RecommendationLogsMapper extends BaseMapper<RecommendationLogs> {
    // 继承BaseMapper，自动生成插入、更新等方法
}
