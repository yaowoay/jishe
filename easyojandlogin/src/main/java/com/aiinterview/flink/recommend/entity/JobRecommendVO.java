package com.aiinterview.flink.recommend.entity;



import lombok.Data;

@Data
public class JobRecommendVO {
    // 岗位核心信息
    private Position job;

    // 各维度匹配度（百分比，保留1位小数）
    private Double skillMatch; // 技能匹配度
    private Double educationMatch; // 学历匹配度
    private Double positionMatch; // 职位匹配度
    private Double industryMatch; // 行业匹配度
    private Double salaryMatch; // 薪资匹配度
    private Double locationMatch; // 位置匹配度

    // 双引擎分数
    private Double contentScore; // 内容匹配综合分（%）
    private Double cfScore; // 协同过滤分数（%）
    private Double realtimeScore; // 实时偏好分（%）

    // 最终综合匹配度（%）
    private Double finalScore;

    // 推荐标签
    private String recommendTag;

    // 推荐依据说明（贴合学生画像与求职意向，直观易懂）
    private String recommendReason;
}