package com.aiinterview.service;

import com.aiinterview.model.entity.student.UserJobCollection;
import java.util.List;

/**
 * 用户收藏职位Service接口
 */
public interface UserJobCollectionService {

    /**
     * 添加收藏
     */
    boolean addCollection(Integer userId, Integer jobId);

    /**
     * 取消收藏
     */
    boolean removeCollection(Integer userId, Integer jobId);

    /**
     * 查询用户收藏列表
     */
    List<UserJobCollection> getUserCollections(Integer userId);

    /**
     * 检查是否已收藏
     */
    boolean isCollected(Integer userId, Integer jobId);

    /**
     * 获取用户收藏总数
     */
    long getCollectionCount(Integer userId);

    /**
     * 分页查询用户收藏
     */
    List<UserJobCollection> getUserCollectionsByPage(Integer userId, int pageNum, int pageSize);
}
