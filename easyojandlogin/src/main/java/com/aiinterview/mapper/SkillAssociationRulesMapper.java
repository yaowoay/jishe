package com.aiinterview.mapper;

import com.aiinterview.model.entity.SkillAssociationRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 技能关联规则Mapper接口
 */
@Mapper
public interface SkillAssociationRulesMapper {

    /**
     * 添加关联规则
     */
    int insert(SkillAssociationRule rule);

    /**
     * 查询所有规则
     */
    List<SkillAssociationRule> selectAll();

    /**
     * 根据技能A查询关联规则
     */
    List<SkillAssociationRule> selectBySkillA(@Param("skillA") String skillA);

    /**
     * 根据技能B查询关联规则
     */
    List<SkillAssociationRule> selectBySkillB(@Param("skillB") String skillB);

    /**
     * 查询技能对的关联规则
     */
    SkillAssociationRule selectBySkillPair(@Param("skillA") String skillA, 
                                           @Param("skillB") String skillB);

    /**
     * 根据置信度排序查询
     */
    List<SkillAssociationRule> selectOrderByConfidence();

    /**
     * 根据提升度排序查询
     */
    List<SkillAssociationRule> selectOrderByLift();

    /**
     * 分页查询
     */
    List<SkillAssociationRule> selectPage(@Param("offset") int offset, 
                                          @Param("limit") int limit);

    /**
     * 获取规则总数
     */
    long countAll();

    /**
     * 删除规则
     */
    int delete(@Param("ruleId") Integer ruleId);

    /**
     * 更新规则
     */
    int update(SkillAssociationRule rule);
}
