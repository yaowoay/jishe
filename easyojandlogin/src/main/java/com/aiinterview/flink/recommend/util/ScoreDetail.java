package com.aiinterview.flink.recommend.util;

import lombok.Builder;
import lombok.Data;

/**
 * 得分详情
 */
@Data
@Builder
public class ScoreDetail {
    private double skillMatch;      // 技能匹配分
    private double educationMatch;  // 学历匹配分
    private double positionMatch;   // 职位匹配分
    private double industryMatch;   // 行业匹配分
    private double salaryMatch;     // 薪资匹配分
    private double locationMatch;   // 地点匹配分
    private double contentScore;    // 内容综合分
    private double cfScore;         // 协同过滤分
    private double realtimeScore;   // 实时偏好分
    private double finalScore;      // 最终得分
}
