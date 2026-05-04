package com.aiinterview.flink.recommend.util;

import lombok.Builder;
import lombok.Data;


/**
 * 匹配结果
 */
@Data
@Builder
public class MatchResult {
    private double finalScore;           // 最终得分
    private ScoreDetail scoreDetail;     // 得分详情
    private String recommendReason;      // 推荐依据
    private String recommendTag;         // 推荐标签
}