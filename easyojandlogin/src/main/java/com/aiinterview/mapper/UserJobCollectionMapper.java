package com.aiinterview.mapper;

import com.aiinterview.entity.UserJobCollection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 用户收藏职位Mapper接口
 */
@Mapper
public interface UserJobCollectionMapper {

    /**
     * 添加收藏
     */
    int insert(UserJobCollection collection);

    /**
     * 取消收藏
     */
    int delete(@Param("userId") Integer userId, @Param("jobId") Integer jobId);

    /**
     * 查询用户收藏列表
     */
    List<UserJobCollection> selectByUserId(@Param("userId") Integer userId);

    /**
     * 检查是否已收藏
     */
    UserJobCollection selectByUserAndJob(@Param("userId") Integer userId, @Param("jobId") Integer jobId);

    /**
     * 获取用户收藏总数
     */
    long countByUserId(@Param("userId") Integer userId);

    /**
     * 分页查询用户收藏
     */
    List<UserJobCollection> selectPageByUserId(@Param("userId") Integer userId, 
                                               @Param("offset") int offset, 
                                               @Param("limit") int limit);
}
