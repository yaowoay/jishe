package com.aiinterview.controller;

import com.aiinterview.common.BaseResponse;
import com.aiinterview.common.ResultUtils;
import com.aiinterview.model.entity.SkillAssociationRule;
import com.aiinterview.service.SkillAssociationRulesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 技能关联规则Controller
 */
@RestController
@RequestMapping("/api/skill-association-rules")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SkillAssociationRulesController {

    @Autowired
    private SkillAssociationRulesService skillAssociationRulesService;

    /**
     * 查询所有规则
     */
    @GetMapping
    public BaseResponse<List<SkillAssociationRule>> getAllRules() {
        try {
            List<SkillAssociationRule> rules = skillAssociationRulesService.getAllRules();
            return ResultUtils.success(rules);
        } catch (Exception e) {
            return ResultUtils.error(500, "查询所有规则失败");
        }
    }

    /**
     * 根据技能A查询关联规则
     */
    @GetMapping("/skill-a/{skillA}")
    public BaseResponse<List<SkillAssociationRule>> getRulesBySkillA(
            @PathVariable String skillA) {
        try {
            List<SkillAssociationRule> rules = skillAssociationRulesService.getRulesBySkillA(skillA);
            return ResultUtils.success(rules);
        } catch (Exception e) {
            return ResultUtils.error(500, "按技能A查询规则失败");
        }
    }

    /**
     * 根据技能B查询关联规则
     */
    @GetMapping("/skill-b/{skillB}")
    public BaseResponse<List<SkillAssociationRule>> getRulesBySkillB(
            @PathVariable String skillB) {
        try {
            List<SkillAssociationRule> rules = skillAssociationRulesService.getRulesBySkillB(skillB);
            return ResultUtils.success(rules);
        } catch (Exception e) {
            return ResultUtils.error(500, "按技能B查询规则失败");
        }
    }

    /**
     * 查询技能对的关联规则
     */
    @GetMapping("/skill-pair")
    public BaseResponse<SkillAssociationRule> getRuleBySkillPair(
            @RequestParam String skillA,
            @RequestParam String skillB) {
        try {
            SkillAssociationRule rule = skillAssociationRulesService.getRuleBySkillPair(skillA, skillB);
            return ResultUtils.success(rule);
        } catch (Exception e) {
            return ResultUtils.error(500, "查询技能对规则失败");
        }
    }

    /**
     * 按置信度排序查询规则
     */
    @GetMapping("/order-by-confidence")
    public BaseResponse<List<SkillAssociationRule>> getRulesOrderByConfidence() {
        try {
            List<SkillAssociationRule> rules = skillAssociationRulesService.getRulesOrderByConfidence();
            return ResultUtils.success(rules);
        } catch (Exception e) {
            return ResultUtils.error(500, "按置信度排序查询失败");
        }
    }

    /**
     * 按提升度排序查询规则
     */
    @GetMapping("/order-by-lift")
    public BaseResponse<List<SkillAssociationRule>> getRulesOrderByLift() {
        try {
            List<SkillAssociationRule> rules = skillAssociationRulesService.getRulesOrderByLift();
            return ResultUtils.success(rules);
        } catch (Exception e) {
            return ResultUtils.error(500, "按提升度排序查询失败");
        }
    }

    /**
     * 分页查询规则
     */
    @GetMapping("/page")
    public BaseResponse<List<SkillAssociationRule>> getRulesByPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            List<SkillAssociationRule> rules = skillAssociationRulesService.getRulesByPage(pageNum, pageSize);
            return ResultUtils.success(rules);
        } catch (Exception e) {
            return ResultUtils.error(500, "分页查询规则失败");
        }
    }

    /**
     * 添加规则
     */
    @PostMapping("/add")
    public BaseResponse<Boolean> addRule(@RequestBody SkillAssociationRule rule) {
        try {
            boolean success = skillAssociationRulesService.addRule(rule);
            return ResultUtils.success(success);
        } catch (Exception e) {
            return ResultUtils.error(500, "添加规则失败");
        }
    }

    /**
     * 更新规则
     */
    @PutMapping("/update")
    public BaseResponse<Boolean> updateRule(@RequestBody SkillAssociationRule rule) {
        try {
            boolean success = skillAssociationRulesService.updateRule(rule);
            return ResultUtils.success(success);
        } catch (Exception e) {
            return ResultUtils.error(500, "更新规则失败");
        }
    }

    /**
     * 删除规则
     */
    @DeleteMapping("/{ruleId}")
    public BaseResponse<Boolean> deleteRule(@PathVariable Integer ruleId) {
        try {
            boolean success = skillAssociationRulesService.deleteRule(ruleId);
            return ResultUtils.success(success);
        } catch (Exception e) {
            return ResultUtils.error(500, "删除规则失败");
        }
    }

    /**
     * 获取规则总数
     */
    @GetMapping("/count")
    public BaseResponse<Long> getRuleCount() {
        try {
            long count = skillAssociationRulesService.getRuleCount();
            return ResultUtils.success(count);
        } catch (Exception e) {
            return ResultUtils.error(500, "获取规则总数失败");
        }
    }
}

