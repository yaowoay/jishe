package com.aiinterview.flink.recommend.service;

import com.aiinterview.flink.recommend.entity.CfScore;
import com.aiinterview.flink.recommend.entity.Position;
import com.aiinterview.flink.recommend.entity.RecommendationLogs;
import com.aiinterview.flink.recommend.entity.UserFullProfile;
import com.aiinterview.flink.recommend.mapper.CfScoreMapper;
import com.aiinterview.flink.recommend.mapper.RecommendationLogsMapper;
import com.aiinterview.flink.recommend.util.RecommendUtil;
import com.aiinterview.flink.recommend.util.RedisProfileUtil;
import com.aiinterview.flink.recommend.util.MatchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PositionRecommendService {

    @Resource
    private RedisProfileUtil redisProfileUtil;

    @Resource
    private PositionService positionService;

    @Resource
    private RecommendationLogsMapper recommendationLogsMapper;

    @Resource
    private CfScoreMapper cfScoreMapper;

    @Resource
    private RecommendUtil recommendUtil;

    // 双引擎权重
    @Value("${recommend.weight.content:0.7}")
    private Double contentWeight;

    @Value("${recommend.weight.cf:0.3}")
    private Double cfWeight;

    @Value("${recommend.weight.realtime:0.15}")
    private Double realtimeWeight;

    @Value("${recommend.cf-score.min:0}")
    private Double cfScoreMin;

    @Value("${recommend.cf-score.max:100}")
    private Double cfScoreMax;

    // 返回TopN
    @Value("${recommend.top-n:10}")
    private Integer topN;

    // 最低推荐分，过滤明显不相关岗位
    @Value("${recommend.min-score:35}")
    private Double minScore;

    // 默认不返回低匹配岗位
    @Value("${recommend.exclude-low-match:true}")
    private Boolean excludeLowMatch;

    // 高质量结果为空时，允许兜底返回的低匹配数量（默认0表示不兜底）
    @Value("${recommend.low-match-fallback-count:0}")
    private Integer lowMatchFallbackCount;

    // ====================== 核心推荐接口 ======================
    public List<Position> recommendPosition(String userId) {
        // 1. 从Redis获取【基础画像 + 实时画像】（内部已实现MySQL兜底）
        UserFullProfile fullProfile = redisProfileUtil.getFullUserProfile(userId);
        if (fullProfile == null || fullProfile.getUserId() == null) {
            log.warn("用户画像为空，userId={}", userId);
            return Collections.emptyList();
        }

        // 2. 获取候选岗位
        List<Position> candidateJobs = getCandidateJobs(fullProfile);
        if (CollectionUtils.isEmpty(candidateJobs)) {
            return Collections.emptyList();
        }

        Map<Long, Double> cfScoreMap = loadCfScores(userId, candidateJobs);

        // 3. 计算每个岗位的匹配分
        List<Position> recommendList = new ArrayList<>();
        for (Position position : candidateJobs) {
            // 获取协同过滤得分
            double cfScore = getCfScore(cfScoreMap, position.getId());

            // 使用RecommendUtil计算匹配结果
            MatchResult matchResult = recommendUtil.calculateMatchResult(
                    position, fullProfile, cfScore,
                    contentWeight, cfWeight, realtimeWeight);

            position.setMatchScore(matchResult.getFinalScore());
            position.setRecommendReason(matchResult.getRecommendReason());
            position.setRecommendTag(matchResult.getRecommendTag());
            recommendList.add(position);

            log.debug("岗位匹配计算完成，userId={}, jobId={}, finalScore={}, reason={}",
                    userId, position.getId(), matchResult.getFinalScore(), matchResult.getRecommendReason());
        }

        // 4. 先过滤低分，再按匹配标签与分数排序
        double threshold = minScore == null ? 35.0 : minScore;
        List<Position> filteredByScore = recommendList.stream()
            .filter(p -> p.getMatchScore() != null && p.getMatchScore() >= threshold)
            .sorted(Comparator.comparing(Position::getMatchScore).reversed())
            .collect(Collectors.toList());

        boolean excludeLow = excludeLowMatch == null || excludeLowMatch;
        List<Position> topRecommendList = filteredByScore.stream()
            .filter(p -> !excludeLow || !"低匹配".equals(p.getRecommendTag()))
                .limit(Math.max(topN, 1))
                .collect(Collectors.toList());

        // 如果高质量推荐为空，按配置决定是否兜底低匹配结果
        if (CollectionUtils.isEmpty(topRecommendList)
            && !CollectionUtils.isEmpty(filteredByScore)
            && lowMatchFallbackCount != null
            && lowMatchFallbackCount > 0) {
            topRecommendList = filteredByScore.stream()
                .filter(p -> "低匹配".equals(p.getRecommendTag()))
                .limit(Math.min(lowMatchFallbackCount, Math.max(topN, 1)))
                .collect(Collectors.toList());

            if (!CollectionUtils.isEmpty(topRecommendList)) {
            log.info("高质量推荐为空，启用低匹配兜底，userId={}, fallbackCount={}", userId, topRecommendList.size());
            }
        }

        // 5. 记录推荐日志（只记录返回给用户的TopN结果）
        for (Position position : topRecommendList) {
            insertRecommendationLog(userId, position.getId(), position.getMatchScore());
        }

        return topRecommendList;
    }

    // ====================== 协同过滤CF得分 ======================
    private Map<Long, Double> loadCfScores(String userId, List<Position> candidateJobs) {
        if (!StringUtils.hasText(userId) || CollectionUtils.isEmpty(candidateJobs)) {
            return Collections.emptyMap();
        }

        try {
            Long studentId = Long.parseLong(userId);
            List<Long> jobIds = candidateJobs.stream()
                    .map(Position::getId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(jobIds)) {
                return Collections.emptyMap();
            }

            List<CfScore> cfScores = cfScoreMapper.selectCfScoresByStudentAndJobs(studentId, jobIds);
            if (CollectionUtils.isEmpty(cfScores)) {
                return Collections.emptyMap();
            }

            return cfScores.stream()
                    .filter(score -> score.getJobId() != null && score.getCfScore() != null)
                    .collect(Collectors.toMap(
                            CfScore::getJobId,
                            score -> normalizeCfScore(score.getCfScore()),
                            (a, b) -> a));
        } catch (Exception e) {
            log.warn("加载CF分数失败，userId={}，将使用默认0分", userId, e);
            return Collections.emptyMap();
        }
    }

    private double getCfScore(Map<Long, Double> cfScoreMap, Long jobId) {
        try {
            if (jobId == null || CollectionUtils.isEmpty(cfScoreMap)) {
                return 0.0;
            }
            return normalizeCfScore(cfScoreMap.getOrDefault(jobId, 0.0));
        } catch (Exception e) {
            return 0.0;
        }
    }

    private double normalizeCfScore(Double rawScore) {
        if (rawScore == null || rawScore.isNaN() || rawScore.isInfinite()) {
            return 0.0;
        }

        double min = cfScoreMin == null ? 0.0 : cfScoreMin;
        double max = cfScoreMax == null ? 100.0 : cfScoreMax;
        if (min > max) {
            double temp = min;
            min = max;
            max = temp;
        }
        return Math.max(min, Math.min(max, rawScore));
    }

    // ====================== 候选岗位筛选 ======================
    private List<Position> getCandidateJobs(UserFullProfile profile) {
        List<Position> allPositions = positionService.getAllPositionsWithIndustry();
        if (CollectionUtils.isEmpty(allPositions)) {
            return Collections.emptyList();
        }

        try {
            // 优先用Flink实时偏好筛选，筛选结果为空时回退全量，避免空召回
            if (StringUtils.hasText(profile.getRealTimeJobTypes())) {
                Set<String> typeSet = Arrays.stream(profile.getRealTimeJobTypes().split(","))
                        .map(this::normalizeToken)
                        .filter(StringUtils::hasText)
                        .collect(Collectors.toSet());

                List<Position> filtered = allPositions.stream()
                        .filter(job -> typeSet.contains(normalizeToken(job.getType())))
                        .collect(Collectors.toList());

                if (!CollectionUtils.isEmpty(filtered)) {
                    return filtered;
                }

                log.info("实时偏好未命中岗位类型，回退全量召回，userId={}", profile.getUserId());
            }
        } catch (Exception e) {
            log.warn("筛选岗位失败，使用全量", e);
        }
        return allPositions;
    }

    private void insertRecommendationLog(String userId, Long jobId, Double finalScore) {
        if (!StringUtils.hasText(userId) || jobId == null || finalScore == null) {
            return;
        }

        try {
            RecommendationLogs log = new RecommendationLogs();
            log.setUserId(Long.parseLong(userId));
            log.setJobId(jobId);
            log.setAlgorithmType("hybrid");
            log.setScore(finalScore);
            log.setIsClicked(0);
            log.setIsApplied(0);
            log.setCreateTime(new Date());
            recommendationLogsMapper.insert(log);
        } catch (Exception e) {
            log.warn("写入推荐日志失败，userId={}, jobId={}", userId, jobId, e);
        }
    }

    private String normalizeToken(String token) {
        return token == null ? "" : token.trim().toLowerCase();
    }
}