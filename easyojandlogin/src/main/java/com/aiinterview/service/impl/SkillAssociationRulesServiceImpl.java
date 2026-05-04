package com.aiinterview.service.impl;

import com.aiinterview.model.entity.SkillAssociationRule;
import com.aiinterview.mapper.SkillAssociationRulesMapper;
import com.aiinterview.service.SkillAssociationRulesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 技能关联规则Service实现类
 */
@Service
public class SkillAssociationRulesServiceImpl implements SkillAssociationRulesService {

    @Autowired
    private SkillAssociationRulesMapper skillAssociationRulesMapper;

    @Override
    public boolean addRule(SkillAssociationRule rule) {
        return skillAssociationRulesMapper.insert(rule) > 0;
    }

    @Override
    public List<SkillAssociationRule> getAllRules() {
        return skillAssociationRulesMapper.selectAll();
    }

    @Override
    public List<SkillAssociationRule> getRulesBySkillA(String skillA) {
        return skillAssociationRulesMapper.selectBySkillA(skillA);
    }

    @Override
    public List<SkillAssociationRule> getRulesBySkillB(String skillB) {
        return skillAssociationRulesMapper.selectBySkillB(skillB);
    }

    @Override
    public SkillAssociationRule getRuleBySkillPair(String skillA, String skillB) {
        return skillAssociationRulesMapper.selectBySkillPair(skillA, skillB);
    }

    @Override
    public List<SkillAssociationRule> getRulesOrderByConfidence() {
        return skillAssociationRulesMapper.selectOrderByConfidence();
    }

    @Override
    public List<SkillAssociationRule> getRulesOrderByLift() {
        return skillAssociationRulesMapper.selectOrderByLift();
    }

    @Override
    public List<SkillAssociationRule> getRulesByPage(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return skillAssociationRulesMapper.selectPage(offset, pageSize);
    }

    @Override
    public long getRuleCount() {
        return skillAssociationRulesMapper.countAll();
    }

    @Override
    public boolean deleteRule(Integer ruleId) {
        return skillAssociationRulesMapper.delete(ruleId) > 0;
    }

    @Override
    public boolean updateRule(SkillAssociationRule rule) {
        return skillAssociationRulesMapper.update(rule) > 0;
    }
}
