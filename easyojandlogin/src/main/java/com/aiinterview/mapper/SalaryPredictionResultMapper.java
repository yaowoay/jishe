package com.aiinterview.mapper;

import com.aiinterview.entity.SalaryPredictionResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 薪资预测结果Mapper接口
 */
@Mapper
public interface SalaryPredictionResultMapper {

    /**
     * 添加预测结果
     */
    int insert(SalaryPredictionResult result);

    /**
     * 查询用户最新预测结果
     */
    SalaryPredictionResult selectLatestByUserId(@Param("userId") Integer userId);

    /**
     * 查询用户所有预测结果
     */
    List<SalaryPredictionResult> selectByUserId(@Param("userId") Integer userId);

    /**
     * 分页查询用户预测结果
     */
    List<SalaryPredictionResult> selectPageByUserId(@Param("userId") Integer userId,
                                                    @Param("offset") int offset,
                                                    @Param("limit") int limit);

    /**
     * 获取用户预测总数
     */
    long countByUserId(@Param("userId") Integer userId);

    /**
     * 根据预测ID查询
     */
    SalaryPredictionResult selectById(@Param("predId") Integer predId);

    /**
     * 删除预测结果
     */
    int delete(@Param("predId") Integer predId);

    /**
     * 更新预测结果
     */
    int update(SalaryPredictionResult result);
}
