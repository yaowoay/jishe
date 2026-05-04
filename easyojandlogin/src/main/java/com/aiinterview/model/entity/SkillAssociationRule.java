package com.aiinterview.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 技能关联规则
 * 对应数据库表 skill_association_rules
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillAssociationRule implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer ruleId;                // 规则ID
    private String skillA;                 // 技能A
    private String skillB;                 // 技能B
    private BigDecimal support;            // 支持度
    private BigDecimal confidence;         // 置信度
    private BigDecimal lift;               // 提升度
    private LocalDateTime createdAt;       // 创建时间
}
