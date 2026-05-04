package com.aiinterview.mapper;

import com.aiinterview.model.entity.student.UserAction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 用户行为Mapper接口
 */
@Mapper
public interface UserActionMapper {

    /**
     * 根据用户ID查询行为记录
     */
    List<UserAction> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和时间范围查询行为记录
     */
    List<UserAction> selectByUserIdAndTimeRange(
            @Param("userId") Long userId,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime);

    /**
     * 根据行为类型查询记录
     */
    List<UserAction> selectByActionType(@Param("actionType") String actionType);

    /**
     * 根据用户ID和行为类型查询记录
     */
    List<UserAction> selectByUserIdAndActionType(
            @Param("userId") Long userId,
            @Param("actionType") String actionType);

    /**
     * 查询用户的行为统计
     */
    List<UserAction> selectActionStats(@Param("userId") Long userId);

    /**
     * 插入行为记录
     */
    int insert(UserAction action);

    /**
     * 批量插入行为记录
     */
    int insertBatch(@Param("actions") List<UserAction> actions);

    /**
     * 删除用户的行为记录
     */
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 查询行为总数
     */
    long countByUserId(@Param("userId") Long userId);

    /**
     * 查询用户的行为类型统计
     */
    List<Map<String, Object>> selectActionTypeStats(@Param("userId") Long userId);
}
