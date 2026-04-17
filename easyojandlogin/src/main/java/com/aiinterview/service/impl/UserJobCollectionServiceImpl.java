package com.aiinterview.service.impl;

import com.aiinterview.entity.UserJobCollection;
import com.aiinterview.mapper.UserJobCollectionMapper;
import com.aiinterview.service.UserJobCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 用户收藏职位Service实现类
 */
@Service
public class UserJobCollectionServiceImpl implements UserJobCollectionService {

    @Autowired
    private UserJobCollectionMapper userJobCollectionMapper;

    @Override
    public boolean addCollection(Integer userId, Integer jobId) {
        UserJobCollection collection = new UserJobCollection();
        collection.setUserId(userId);
        collection.setJobId(jobId);
        collection.setIsDeleted(0);
        return userJobCollectionMapper.insert(collection) > 0;
    }

    @Override
    public boolean removeCollection(Integer userId, Integer jobId) {
        return userJobCollectionMapper.delete(userId, jobId) > 0;
    }

    @Override
    public List<UserJobCollection> getUserCollections(Integer userId) {
        return userJobCollectionMapper.selectByUserId(userId);
    }

    @Override
    public boolean isCollected(Integer userId, Integer jobId) {
        UserJobCollection collection = userJobCollectionMapper.selectByUserAndJob(userId, jobId);
        return collection != null && collection.getIsDeleted() == 0;
    }

    @Override
    public long getCollectionCount(Integer userId) {
        return userJobCollectionMapper.countByUserId(userId);
    }

    @Override
    public List<UserJobCollection> getUserCollectionsByPage(Integer userId, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return userJobCollectionMapper.selectPageByUserId(userId, offset, pageSize);
    }
}
