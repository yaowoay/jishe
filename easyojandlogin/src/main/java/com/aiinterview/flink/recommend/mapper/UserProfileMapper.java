package com.aiinterview.flink.recommend.mapper;


import com.aiinterview.flink.recommend.entity.UserFullProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserProfileMapper {

    /**
     * 根据用户ID查询静态用户画像（MySQL持久化的基础字段）
     * @param userId 用户ID
     * @return 用户静态画像
     */
    UserFullProfile selectByUserId(@Param("userId") Integer userId);
}
