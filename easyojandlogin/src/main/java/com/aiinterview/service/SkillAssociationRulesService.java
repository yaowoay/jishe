package com.aiinterview.service;

import com.aiinterview.model.entity.SkillAssociationRule;
import java.util.List;

/**
 * 技能关联规则Service接口
 */
public interface SkillAssociationRulesService {

    /**
     * 添加关联规则
     */
    boolean addRule(SkillAssociationRule rule);

    /**
     * 查询所有规则
     */
    List<SkillAssociationRule> getAllRules();

    /**
     * 根据技能A查询关联规则
     */
    List<SkillAssociationRule> getRulesBySkillA(String skillA);

    /**
     * 根据技能B查询关联规则
     */
    List<SkillAssociationRule> getRulesBySkillB(String skillB);

    /**
     * 查询技能对的关联规则
     */
    SkillAssociationRule getRuleBySkillPair(String skillA, String skillB);

    /**
     * 按置信度排序查询规则
     */
    List<SkillAssociationRule> getRulesOrderByConfidence();

    /**
     * 按提升度排序查询规则
     */
    List<SkillAssociationRule> getRulesOrderByLift();

    /**
     * 分页查询规则
     */
    List<SkillAssociationRule> getRulesByPage(int pageNum, int pageSize);

    /**
     * 获取规则总数
     */
    long getRuleCount();

    /**
     * 删除规则
     */
    boolean deleteRule(Integer ruleId);

    /**
     * 更新规则
     */
    boolean updateRule(SkillAssociationRule rule);
}
