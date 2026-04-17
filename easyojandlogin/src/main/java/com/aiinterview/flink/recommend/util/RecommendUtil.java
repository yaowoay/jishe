package com.aiinterview.flink.recommend.util;


import com.aiinterview.flink.recommend.entity.Position;
import com.aiinterview.flink.recommend.entity.JobRecommendVO;
import com.aiinterview.flink.recommend.entity.UserFullProfile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 核心工具类：匹配度计算、推荐依据生成
 */
@Component
public class RecommendUtil {

    private static final Set<String> SKILL_NOISE_WORDS = new HashSet<>(Arrays.asList(
            "编程", "开发", "工程", "工程师", "架构", "系统", "应用", "设计", "优化", "能力", "基础", "经验"
    ));

    private static final Map<String, String> SKILL_SYNONYM_MAP = new HashMap<>();
    private static final Map<String, String> INDUSTRY_ALIAS_MAP = new HashMap<>();

    static {
        SKILL_SYNONYM_MAP.put("mysql", "sql");
        SKILL_SYNONYM_MAP.put("postgresql", "sql");
        SKILL_SYNONYM_MAP.put("oracle", "sql");
        SKILL_SYNONYM_MAP.put("spring", "springboot");
        SKILL_SYNONYM_MAP.put("mybatisplus", "mybatis");

        INDUSTRY_ALIAS_MAP.put("it", "it互联网");
        INDUSTRY_ALIAS_MAP.put("信息技术", "it互联网");
        INDUSTRY_ALIAS_MAP.put("互联网", "it互联网");
        INDUSTRY_ALIAS_MAP.put("互联网/信息技术", "it互联网");
        INDUSTRY_ALIAS_MAP.put("it互联网", "it互联网");
        INDUSTRY_ALIAS_MAP.put("it/互联网", "it互联网");
        INDUSTRY_ALIAS_MAP.put("计算机", "it互联网");
        INDUSTRY_ALIAS_MAP.put("软件", "it互联网");
        INDUSTRY_ALIAS_MAP.put("电子商务", "it互联网");
    }

    /**
     * 1. 技能匹配度计算（Jaccard相似度，核心：学生技能与岗位要求技能重合度）
     * @param studentSkills 学生技能（逗号分隔）
     * @param jobSkills 岗位要求技能（逗号分隔）
     * @return 技能匹配度（%），保留1位小数
     */
    public Double calculateSkillMatch(String studentSkills, String jobSkills) {
        if (!StringUtils.hasText(studentSkills) || !StringUtils.hasText(jobSkills)) {
            return 0.0;
        }

        Set<String> studentSkillSet = tokenizeSkills(studentSkills);
        Set<String> jobSkillSet = tokenizeSkills(jobSkills);
        if (jobSkillSet.isEmpty()) {
            return 0.0;
        }

        int matched = 0;
        for (String jobSkill : jobSkillSet) {
            boolean hit = studentSkillSet.stream().anyMatch(studentSkill -> isSkillMatched(studentSkill, jobSkill));
            if (hit) {
                matched++;
            }
        }

        double match = (double) matched / jobSkillSet.size() * 100;
        return Math.round(match * 10) / 10.0;
    }

    /**
     * 2. 学历匹配度计算（贴合能力画像，精准匹配岗位学历要求）
     * @return 学历匹配度（%）
     */
    public Double calculateEducationMatch(String studentMajor, String jobMajor) {
        if (!StringUtils.hasText(studentMajor)) {
            return 0.0;
        }
        if (!StringUtils.hasText(jobMajor) || "无要求".equals(jobMajor) || "不限".equals(jobMajor)) {
            return 80.0;
        }

        int studentRank = getEducationRank(studentMajor);
        int requiredRank = getEducationRank(jobMajor);

        if (studentRank > 0 && requiredRank > 0) {
            if (studentRank >= requiredRank) {
                return 100.0;
            }
            return studentRank + 1 == requiredRank ? 60.0 : 0.0;
        }

        String student = normalizeToken(studentMajor);
        String job = normalizeToken(jobMajor);
        if (student.equals(job)) {
            return 100.0;
        } else if (student.contains(job) || job.contains(student)) {
            return 90.0;
        } else {
            return 0.0;
        }
    }

    /**
     * 3. 薪资匹配度计算（区间对区间）
     * @param expectedSalaryMin 用户期望薪资下限
     * @param expectedSalaryMax 用户期望薪资上限
     * @param jobSalaryMin 岗位薪资下限
     * @param jobSalaryMax 岗位薪资上限
     * @return 匹配度 0~100 分
     */
    public Double calculateSalaryMatch(
            Integer expectedSalaryMin,
            Integer expectedSalaryMax,
            Integer jobSalaryMin,
            Integer jobSalaryMax) {

        int[] expectedRange = normalizeRange(expectedSalaryMin, expectedSalaryMax);
        int[] jobRange = normalizeRange(jobSalaryMin, jobSalaryMax);
        if (expectedRange == null || jobRange == null) {
            return 0.0;
        }

        int expectedMin = expectedRange[0];
        int expectedMax = expectedRange[1];
        int jobMin = jobRange[0];
        int jobMax = jobRange[1];

        int overlapMin = Math.max(expectedMin, jobMin);
        int overlapMax = Math.min(expectedMax, jobMax);

        // 区间有交集：按交并比评分，避免超宽区间虚高
        if (overlapMin <= overlapMax) {
            int overlapWidth = overlapMax - overlapMin;
            int unionWidth = Math.max(1, Math.max(expectedMax, jobMax) - Math.min(expectedMin, jobMin));
            double iou = Math.max(0.0, Math.min(1.0, overlapWidth / (double) unionWidth));

            double score = 60.0 + 40.0 * iou;
            return Math.round(score * 10) / 10.0;
        }

        // 无交集：按区间间隔距离衰减
        int gap = expectedMax < jobMin ? (jobMin - expectedMax) : (expectedMin - jobMax);
        int reference = Math.max(Math.max(expectedMax, jobMax), 1);
        double gapRatio = gap / (double) reference;
        if (gapRatio <= 0.05) {
            return 85.0;
        }
        if (gapRatio <= 0.15) {
            return 65.0;
        }
        if (gapRatio <= 0.30) {
            return 40.0;
        }
        return 0.0;
    }

    /**
     * 4. 位置匹配度计算（贴合求职意向，城市匹配）
     * @param expectCity 学生意向城市
     * @param jobCity 岗位工作城市
     * @return 位置匹配度（%）
     */
    public Double calculateLocationMatch(String expectCity, String jobCity) {
        if (!StringUtils.hasText(expectCity) || !StringUtils.hasText(jobCity)) {
            return 0.0;
        }

        String expected = extractCityKeyword(expectCity);
        String target = extractCityKeyword(jobCity);
        if (!StringUtils.hasText(expected) || !StringUtils.hasText(target)) {
            return 0.0;
        }

        if (expected.equals(target)) {
            return 100.0;
        }

        // 明细地址包含关系，如“北京” vs “北京市朝阳区科技园区”
        String normalizedExpect = normalizeToken(expectCity);
        String normalizedJob = normalizeToken(jobCity);
        if (normalizedJob.contains(expected) || normalizedExpect.contains(target)) {
            return 95.0;
        }

        // 同城市群（贴合实际求职习惯，可扩展）
        Set<String> pearlRiverDelta = new HashSet<>(Arrays.asList("深圳", "广州", "中山", "珠海", "东莞"));
        Set<String> yangtzeRiverDelta = new HashSet<>(Arrays.asList("上海", "杭州", "苏州", "南京", "无锡"));
        if (pearlRiverDelta.contains(expected) && pearlRiverDelta.contains(target)) {
            return 95.0;
        }
        if (yangtzeRiverDelta.contains(expected) && yangtzeRiverDelta.contains(target)) {
            return 95.0;
        }

        if (getProvince(expected).equals(getProvince(target))) {
            return 80.0;
        }

        return 0.0;
    }

    /**
     * 行业匹配度计算
     */
    public Double calculateIndustryMatch(String expectedIndustry, String major, String jobIndustry) {
        String sourceIndustry = StringUtils.hasText(expectedIndustry) ? expectedIndustry : major;
        if (!StringUtils.hasText(sourceIndustry)) {
            return 0.0;
        }
        if (!StringUtils.hasText(jobIndustry)) {
            return 50.0;
        }

        String expected = normalizeIndustryTerm(sourceIndustry);
        String target = normalizeIndustryTerm(jobIndustry);
        if (expected.equals(target)) {
            return 100.0;
        }

        Set<String> expectedTokens = tokenizeIndustry(sourceIndustry);
        Set<String> targetTokens = tokenizeIndustry(jobIndustry);
        if (!expectedTokens.isEmpty() && !targetTokens.isEmpty()) {
            Set<String> intersection = new HashSet<>(expectedTokens);
            intersection.retainAll(targetTokens);
            if (!intersection.isEmpty()) {
                double ratio = intersection.size() / (double) Math.max(expectedTokens.size(), targetTokens.size());
                double score = 70.0 + 30.0 * ratio;
                return Math.round(score * 10) / 10.0;
            }
        }

        double overlapRatio = calculateTextOverlapRatio(sourceIndustry, jobIndustry);
        if (overlapRatio > 0) {
            double score = 40.0 + 60.0 * overlapRatio;
            return Math.round(score * 10) / 10.0;
        }

        if (expected.contains(target) || target.contains(expected)) {
            return 85.0;
        }

        return 0.0;
    }

    /**
     * 职位匹配度计算（用户期望职位 vs 岗位名称）
     */
    public Double calculatePositionMatch(String expectedPosition, String jobName) {
        if (!StringUtils.hasText(expectedPosition)) {
            return 60.0;
        }
        if (!StringUtils.hasText(jobName)) {
            return 0.0;
        }

        String expected = normalizeToken(expectedPosition);
        String target = normalizeToken(jobName);
        if (expected.equals(target)) {
            return 100.0;
        }
        if (target.contains(expected) || expected.contains(target)) {
            return 90.0;
        }

        Set<String> expectedTokens = tokenizePosition(expectedPosition);
        Set<String> nameTokens = tokenizePosition(jobName);
        if (expectedTokens.isEmpty() || nameTokens.isEmpty()) {
            return 0.0;
        }

        Set<String> intersection = new HashSet<>(expectedTokens);
        intersection.retainAll(nameTokens);
        if (!intersection.isEmpty()) {
            double ratio = intersection.size() / (double) Math.max(1, expectedTokens.size());
            double score = 70.0 + 30.0 * Math.min(1.0, ratio);
            return Math.round(score * 10) / 10.0;
        }

        if ((expectedTokens.contains("java") && (nameTokens.contains("后端") || nameTokens.contains("backend")))
                || (expectedTokens.contains("后端") && nameTokens.contains("java"))) {
            return 85.0;
        }

        return 0.0;
    }

    /**
     * 5. 内容匹配综合分计算（加权求和，贴合能力画像与求职意向）
     * 权重分配：技能40%、学历15%、薪资25%、位置20%
     */
    public Double calculateContentScore(Double skillMatch, Double majorMatch, Double positionMatch, Double industryMatch, Double salaryMatch, Double locationMatch) {
        // 业务相关性优先：技能、职位、城市权重更高
        double contentScore = skillMatch * 0.35
            + majorMatch * 0.15
            + positionMatch * 0.2
            + industryMatch * 0.08
            + salaryMatch * 0.1
            + locationMatch * 0.12;
        return Math.round(contentScore * 10) / 10.0;
    }

    /**
     * 6. 双引擎最终分数计算（内容匹配+协同过滤）
     * @param contentScore 内容匹配分
     * @param cfScore 协同过滤分
     * @param contentWeight 内容匹配权重
     * @param cfWeight 协同过滤权重
     * @return 最终综合匹配度（%），保留1位小数
     */
    public Double calculateFinalScore(Double contentScore, Double cfScore, Double contentWeight, Double cfWeight) {
        double finalScore = contentScore * contentWeight + cfScore * cfWeight;
        return Math.round(finalScore * 10) / 10.0;
    }

    /**
     * 6.1 计算实时偏好分
     */
    public Double calculateRealtimeScore(UserFullProfile student, Position job) {
        if (student == null || job == null) {
            return 0.0;
        }

        double typeScore = 0.0;
        double cityScore = 0.0;
        double salaryScore = 0.0;

        Set<String> realtimeTypes = splitToNormalizedSet(student.getRealTimeJobTypes());
        if (!realtimeTypes.isEmpty() && StringUtils.hasText(job.getType())
                && realtimeTypes.contains(normalizeToken(job.getType()))) {
            typeScore = 100.0;
        }

        Set<String> realtimeCities = splitToNormalizedSet(student.getRealTimeCities());
        if (!realtimeCities.isEmpty() && StringUtils.hasText(job.getCity())
                && realtimeCities.contains(normalizeToken(job.getCity().replaceAll("市$", "")))) {
            cityScore = 100.0;
        }

        if (student.getRealTimeSalaryMin() != null && student.getRealTimeSalaryMax() != null
                && job.getSalaryMin() != null && job.getSalaryMax() != null) {
            int userMin = Math.min(student.getRealTimeSalaryMin(), student.getRealTimeSalaryMax());
            int userMax = Math.max(student.getRealTimeSalaryMin(), student.getRealTimeSalaryMax());
            int jobMin = Math.min(job.getSalaryMin(), job.getSalaryMax());
            int jobMax = Math.max(job.getSalaryMin(), job.getSalaryMax());

            int overlapMin = Math.max(userMin, jobMin);
            int overlapMax = Math.min(userMax, jobMax);
            if (overlapMin <= overlapMax) {
                int overlap = overlapMax - overlapMin;
                int union = Math.max(userMax, jobMax) - Math.min(userMin, jobMin);
                salaryScore = union == 0 ? 100.0 : (double) overlap / union * 100;
            }
        }

        double realtimeScore = typeScore * 0.5 + cityScore * 0.3 + salaryScore * 0.2;
        return Math.round(realtimeScore * 10) / 10.0;
    }

    /**
     * 6.2 双引擎+实时偏好最终分
     */
    public MatchResult calculateMatchResult(
            Position job,
            UserFullProfile student,
            Double cfScore,
            Double contentWeight,
            Double cfWeight,
            Double realtimeWeight) {

        double skillMatch = calculateSkillMatch(student.getSkills(), job.getSkills());
        double educationMatch = calculateEducationMatch(student.getEducationLevel(), job.getEducation());

        double salaryMatch = calculateSalaryMatch(
                student.getExpectedSalaryMin(),
                student.getExpectedSalaryMax(),
                job.getSalaryMin(),
                job.getSalaryMax());

        double locationMatch = calculateLocationMatch(student.getExpectedCity(), job.getCity());
        double positionMatch = calculatePositionMatch(student.getPosition(), job.getName());
        double industryMatch = calculateIndustryMatch(student.getExpectedIndustry(), student.getMajor(), job.getIndustry());
        double contentScore = calculateContentScoreByAvailableDimensions(
            student,
            job,
            skillMatch,
            educationMatch,
            positionMatch,
            industryMatch,
            salaryMatch,
            locationMatch);
        double realtimeScore = calculateRealtimeScore(student, job);

        double cWeight = contentWeight == null ? 0.7 : contentWeight;
        double fWeight = cfWeight == null ? 0.2 : cfWeight;
        double rWeight = realtimeWeight == null ? 0.1 : realtimeWeight;

        if (cfScore == null || cfScore <= 0) {
            fWeight = 0.0;
        }

        if (!hasRealtimePreference(student)) {
            rWeight = 0.0;
        }

        double totalWeight = cWeight + fWeight + rWeight;
        if (totalWeight <= 0) {
            cWeight = 0.7;
            fWeight = 0.2;
            rWeight = 0.1;
            totalWeight = 1.0;
        }

        double finalScore = (contentScore * cWeight + (cfScore == null ? 0.0 : cfScore) * fWeight + realtimeScore * rWeight) / totalWeight;

        int availableDimensionCount = countAvailableDimensions(student, job);
        double coverageFactor = getCoverageFactor(availableDimensionCount, 6);
        finalScore = finalScore * coverageFactor;
        finalScore = Math.round(finalScore * 10) / 10.0;

        ScoreDetail detail = ScoreDetail.builder()
                .skillMatch(skillMatch)
                .educationMatch(educationMatch)
                .positionMatch(positionMatch)
                .industryMatch(industryMatch)
                .salaryMatch(salaryMatch)
                .locationMatch(locationMatch)
                .contentScore(contentScore)
                .cfScore(cfScore == null ? 0.0 : cfScore)
                .realtimeScore(realtimeScore)
                .finalScore(finalScore)
                .build();

        JobRecommendVO vo = new JobRecommendVO();
        vo.setJob(job);
        vo.setSkillMatch(skillMatch);
        vo.setEducationMatch(educationMatch);
        vo.setPositionMatch(positionMatch);
        vo.setIndustryMatch(industryMatch);
        vo.setSalaryMatch(salaryMatch);
        vo.setLocationMatch(locationMatch);
        vo.setContentScore(contentScore);
        vo.setCfScore(cfScore == null ? 0.0 : cfScore);
        vo.setRealtimeScore(realtimeScore);
        vo.setFinalScore(finalScore);
        vo.setRecommendTag(resolveRecommendTag(finalScore));

        String reason = generateRecommendReason(student, job, vo);

        return MatchResult.builder()
                .finalScore(finalScore)
                .scoreDetail(detail)
                .recommendReason(reason)
            .recommendTag(resolveRecommendTag(finalScore))
                .build();
    }

    /**
     * 7. 生成推荐依据说明（直观易懂，贴合学生画像与求职意向）
     */
    public String generateRecommendReason(UserFullProfile student, Position job, JobRecommendVO vo) {
        StringBuilder reason = new StringBuilder();
        reason.append("推荐依据：");
        int availableDimensionCount = countAvailableDimensions(student, job);
        double coverageFactor = getCoverageFactor(availableDimensionCount, 6);
        if (hasSkillDimension(student, job)) {
            reason.append("1. 技能匹配度").append(vo.getSkillMatch()).append("%，")
                    .append(describeMatch(vo.getSkillMatch(), "技能高度重合", "部分技能重合", "技能重合较少"))
                    .append("（你的技能【").append(safeText(student.getSkills())).append("】，岗位技能【")
                    .append(safeText(job.getSkills())).append("】）；");
        } else {
            reason.append("1. 技能项未参与评分（技能信息缺失）；");
        }

        if (hasEducationDimension(student, job)) {
            reason.append("2. 学历匹配度").append(vo.getEducationMatch()).append("%，")
                    .append(describeMatch(vo.getEducationMatch(), "学历满足或高于要求", "学历基本满足要求", "学历与岗位要求差距较大"))
                    .append("（你的学历【").append(safeText(student.getEducationLevel())).append("】，岗位要求【")
                    .append(safeText(job.getEducation())).append("】）；");
        } else {
            reason.append("2. 学历项未参与评分（学历信息缺失）；");
        }

        if (hasPositionDimension(student, job)) {
            reason.append("3. 职位匹配度").append(vo.getPositionMatch()).append("%，")
                    .append(describeMatch(vo.getPositionMatch(), "岗位方向与你目标一致", "岗位方向与目标部分一致", "岗位方向与目标不一致"))
                    .append("（期望职位【").append(safeText(student.getPosition())).append("】，岗位名称【")
                    .append(safeText(job.getName())).append("】）；");
        } else {
            reason.append("3. 职位项未参与评分（期望职位信息缺失）；");
        }

        if (hasIndustryDimension(student, job)) {
            reason.append("4. 行业匹配度").append(vo.getIndustryMatch()).append("%，")
                    .append(describeMatch(vo.getIndustryMatch(), "行业方向一致", "行业方向部分一致", "行业方向差异较大"))
                    .append("（")
                    .append(buildIndustrySourceText(student))
                    .append("，岗位行业【")
                    .append(safeText(job.getIndustry())).append("】）；");
        } else {
            reason.append("4. 行业项未参与评分（期望行业信息缺失）；");
        }

        if (hasSalaryDimension(student, job)) {
            reason.append("5. 薪资匹配度").append(vo.getSalaryMatch()).append("%，")
                    .append(describeMatch(vo.getSalaryMatch(), "薪资区间匹配度高", "薪资区间有一定重合", "薪资区间重合较少"))
                    .append("（岗位薪资【").append(formatSalaryRange(job.getSalaryMin(), job.getSalaryMax())).append("】，期望薪资【")
                    .append(formatSalaryRange(student.getExpectedSalaryMin(), student.getExpectedSalaryMax())).append("】）；");
        } else {
            reason.append("5. 薪资项未参与评分（薪资信息缺失）；");
        }

        if (hasLocationDimension(student, job)) {
            reason.append("6. 城市匹配度").append(vo.getLocationMatch()).append("%，")
                    .append(describeMatch(vo.getLocationMatch(), "工作城市与意向城市一致或同城", "工作城市与你意向城市较近", "工作城市与你意向城市差异较大"))
                    .append("（岗位城市【").append(safeText(job.getCity())).append("】，意向城市【")
                    .append(safeText(student.getExpectedCity())).append("】）；");
        } else {
            reason.append("6. 城市项未参与评分（城市信息缺失）；");
        }

        reason
        //  .append("7. 协同过滤参考分").append(vo.getCfScore())
        //          .append("，实时偏好分").append(vo.getRealtimeScore())
        //      .append("，维度覆盖").append(availableDimensionCount).append("/6")
        //      .append("（可信度系数").append(Math.round(coverageFactor * 100)).append("%）")
            .append("推荐标签【").append(vo.getRecommendTag()).append("】。");
        return reason.toString();
    }

    private Double calculateContentScoreByAvailableDimensions(
            UserFullProfile student,
            Position job,
            Double skillMatch,
            Double educationMatch,
            Double positionMatch,
            Double industryMatch,
            Double salaryMatch,
            Double locationMatch) {

        double weightedSum = 0.0;
        double weightTotal = 0.0;

        if (hasSkillDimension(student, job)) {
            weightedSum += (skillMatch == null ? 0.0 : skillMatch) * 0.35;
            weightTotal += 0.35;
        }
        if (hasEducationDimension(student, job)) {
            weightedSum += (educationMatch == null ? 0.0 : educationMatch) * 0.15;
            weightTotal += 0.15;
        }
        if (hasPositionDimension(student, job)) {
            weightedSum += (positionMatch == null ? 0.0 : positionMatch) * 0.2;
            weightTotal += 0.2;
        }
        if (hasIndustryDimension(student, job)) {
            weightedSum += (industryMatch == null ? 0.0 : industryMatch) * 0.08;
            weightTotal += 0.08;
        }
        if (hasSalaryDimension(student, job)) {
            weightedSum += (salaryMatch == null ? 0.0 : salaryMatch) * 0.1;
            weightTotal += 0.1;
        }
        if (hasLocationDimension(student, job)) {
            weightedSum += (locationMatch == null ? 0.0 : locationMatch) * 0.12;
            weightTotal += 0.12;
        }

        if (weightTotal <= 0) {
            return 0.0;
        }
        return Math.round((weightedSum / weightTotal) * 10) / 10.0;
    }

    private boolean hasSkillDimension(UserFullProfile student, Position job) {
        return student != null && job != null
                && StringUtils.hasText(student.getSkills())
                && StringUtils.hasText(job.getSkills());
    }

    private boolean hasEducationDimension(UserFullProfile student, Position job) {
        return student != null && job != null
                && StringUtils.hasText(student.getEducationLevel())
                && StringUtils.hasText(job.getEducation());
    }

    private boolean hasPositionDimension(UserFullProfile student, Position job) {
        return student != null && job != null
                && StringUtils.hasText(student.getPosition())
                && StringUtils.hasText(job.getName());
    }

    private boolean hasIndustryDimension(UserFullProfile student, Position job) {
        return student != null && job != null
                && (StringUtils.hasText(student.getExpectedIndustry()) || StringUtils.hasText(student.getMajor()))
                && StringUtils.hasText(job.getIndustry());
    }

    private String buildIndustrySourceText(UserFullProfile student) {
        if (student == null) {
            return "行业信息缺失";
        }
        if (StringUtils.hasText(student.getExpectedIndustry())) {
            return "期望行业【" + safeText(student.getExpectedIndustry()) + "】";
        }
        if (StringUtils.hasText(student.getMajor())) {
            return "未填写期望行业，使用专业【" + safeText(student.getMajor()) + "】作为行业兜底";
        }
        return "行业信息缺失";
    }

    private boolean hasSalaryDimension(UserFullProfile student, Position job) {
        return student != null && job != null
                && (student.getExpectedSalaryMin() != null || student.getExpectedSalaryMax() != null)
                && (job.getSalaryMin() != null || job.getSalaryMax() != null);
    }

    private boolean hasLocationDimension(UserFullProfile student, Position job) {
        return student != null && job != null
                && StringUtils.hasText(student.getExpectedCity())
                && StringUtils.hasText(job.getCity());
    }

    private int countAvailableDimensions(UserFullProfile student, Position job) {
        int count = 0;
        if (hasSkillDimension(student, job)) {
            count++;
        }
        if (hasEducationDimension(student, job)) {
            count++;
        }
        if (hasPositionDimension(student, job)) {
            count++;
        }
        if (hasIndustryDimension(student, job)) {
            count++;
        }
        if (hasSalaryDimension(student, job)) {
            count++;
        }
        if (hasLocationDimension(student, job)) {
            count++;
        }
        return count;
    }

    private double getCoverageFactor(int availableCount, int totalCount) {
        if (totalCount <= 0) {
            return 1.0;
        }
        double coverage = Math.max(0.0, Math.min(1.0, availableCount / (double) totalCount));
        // 最低保留60%，避免维度少时出现虚高但不至于过度打压
        return 0.6 + 0.4 * coverage;
    }

    private String describeMatch(Double score, String high, String middle, String low) {
        double s = score == null ? 0.0 : score;
        if (s >= 80) {
            return high;
        }
        if (s >= 60) {
            return middle;
        }
        return low;
    }

    private String safeText(String text) {
        return StringUtils.hasText(text) ? text : "未填写";
    }

    private String formatSalaryRange(Integer min, Integer max) {
        if (min == null && max == null) {
            return "未填写";
        }
        if (min == null) {
            return "<= " + max;
        }
        if (max == null) {
            return ">= " + min;
        }
        return min + "-" + max;
    }

    private boolean hasRealtimePreference(UserFullProfile student) {
        if (student == null) {
            return false;
        }
        return StringUtils.hasText(student.getRealTimeJobTypes())
                || StringUtils.hasText(student.getRealTimeCities())
                || student.getRealTimeSalaryMin() != null
                || student.getRealTimeSalaryMax() != null;
    }

    private String resolveRecommendTag(double finalScore) {
        if (finalScore >= 80) {
            return "高匹配";
        }
        if (finalScore >= 60) {
            return "较匹配";
        }
        if (finalScore >= 40) {
            return "可探索";
        }
        return "低匹配";
    }

    private Set<String> tokenizeSkills(String csv) {
        if (!StringUtils.hasText(csv)) {
            return Collections.emptySet();
        }
        String normalized = csv.replace("，", ",")
                .replace("、", ",")
                .replace(";", ",")
                .replace("；", ",")
                .replace("/", ",")
                .replace("|", ",");

        return Arrays.stream(normalized.split(","))
                .map(this::normalizeSkillToken)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());
    }

    private String normalizeSkillToken(String token) {
        if (!StringUtils.hasText(token)) {
            return "";
        }
        String normalized = normalizeToken(token).replaceAll("[^a-z0-9\u4e00-\u9fa5]", "");
        for (String noise : SKILL_NOISE_WORDS) {
            normalized = normalized.replace(noise, "");
        }
        if (SKILL_SYNONYM_MAP.containsKey(normalized)) {
            return SKILL_SYNONYM_MAP.get(normalized);
        }
        return normalized;
    }

    private boolean isSkillMatched(String studentSkill, String jobSkill) {
        if (!StringUtils.hasText(studentSkill) || !StringUtils.hasText(jobSkill)) {
            return false;
        }
        if (studentSkill.equals(jobSkill)) {
            return true;
        }
        if (studentSkill.length() >= 2 && jobSkill.contains(studentSkill)) {
            return true;
        }
        return jobSkill.length() >= 2 && studentSkill.contains(jobSkill);
    }

    private int getEducationRank(String education) {
        if (!StringUtils.hasText(education)) {
            return 0;
        }
        String normalized = normalizeToken(education);
        if (normalized.contains("博士")) {
            return 5;
        }
        if (normalized.contains("硕士") || normalized.contains("研究生")) {
            return 4;
        }
        if (normalized.contains("本科")) {
            return 3;
        }
        if (normalized.contains("大专") || normalized.contains("专科")) {
            return 2;
        }
        if (normalized.contains("中专") || normalized.contains("高中")) {
            return 1;
        }
        return 0;
    }

    private String extractCityKeyword(String city) {
        if (!StringUtils.hasText(city)) {
            return "";
        }
        String text = city.trim();
        if (text.contains("北京")) {
            return "北京";
        }
        if (text.contains("上海")) {
            return "上海";
        }
        if (text.contains("广州")) {
            return "广州";
        }
        if (text.contains("深圳")) {
            return "深圳";
        }
        if (text.contains("杭州")) {
            return "杭州";
        }
        if (text.contains("苏州")) {
            return "苏州";
        }

        String cleaned = text.replaceAll("省|市|区|县|自治州|特别行政区", " ").trim();
        if (!StringUtils.hasText(cleaned)) {
            return "";
        }
        return cleaned.split("\\s+")[0];
    }

    private Set<String> tokenizePosition(String text) {
        Set<String> tokens = new HashSet<>(splitToNormalizedSet(
                text.replace("工程师", ",工程师,")
                        .replace("开发", ",开发,")
                        .replace("后端", ",后端,")
                        .replace("前端", ",前端,")
                        .replace("数据", ",数据,")
                        .replace("测试", ",测试,")
                        .replace("运维", ",运维,")
                        .replace("产品", ",产品,")
        ));

        String normalized = normalizeToken(text);
        if (normalized.contains("java")) {
            tokens.add("java");
        }
        if (normalized.contains("python")) {
            tokens.add("python");
        }
        if (normalized.contains("go")) {
            tokens.add("go");
        }
        if (normalized.contains("后端") || normalized.contains("backend")) {
            tokens.add("后端");
        }
        if (normalized.contains("前端") || normalized.contains("frontend")) {
            tokens.add("前端");
        }
        if (normalized.contains("开发")) {
            tokens.add("开发");
        }
        if (normalized.contains("工程师")) {
            tokens.add("工程师");
        }
        return tokens;
    }

    private String buildRecommendReason(UserFullProfile student, Position job, ScoreDetail detail) {
        StringBuilder reason = new StringBuilder();
        reason.append("技能匹配").append(detail.getSkillMatch()).append("分，")
                .append("学历匹配").append(detail.getEducationMatch()).append("分，")
                .append("职位匹配").append(detail.getPositionMatch()).append("分，")
                .append("行业匹配").append(detail.getIndustryMatch()).append("分，")
                .append("薪资匹配").append(detail.getSalaryMatch()).append("分，")
                .append("城市匹配").append(detail.getLocationMatch()).append("分");

        if (StringUtils.hasText(student.getRealTimeJobTypes()) || StringUtils.hasText(student.getRealTimeCities())) {
            reason.append("；结合最近行为偏好，实时加权").append(detail.getRealtimeScore()).append("分");
        }
        reason.append("。岗位：").append(job.getName());
        return reason.toString();
    }

    private int[] normalizeRange(Integer min, Integer max) {
        if (min == null && max == null) {
            return null;
        }
        int left = min == null ? max : min;
        int right = max == null ? min : max;
        if (left > right) {
            int temp = left;
            left = right;
            right = temp;
        }
        return new int[]{left, right};
    }

    private Set<String> splitToNormalizedSet(String csv) {
        if (!StringUtils.hasText(csv)) {
            return new HashSet<>();
        }
        return Arrays.stream(csv.split(","))
                .map(this::normalizeToken)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());
    }

    private Set<String> tokenizeIndustry(String text) {
        if (!StringUtils.hasText(text)) {
            return Collections.emptySet();
        }

        String normalized = text.replace("、", "/")
                .replace("，", "/")
                .replace(",", "/")
                .replace(";", "/")
                .replace("；", "/")
                .replace("|", "/")
                .replace(" ", "");

        Set<String> tokens = new HashSet<>();
        for (String part : normalized.split("/")) {
            String token = normalizeIndustryTerm(part);
            if (StringUtils.hasText(token)) {
                tokens.add(token);
            }
        }

        if (tokens.isEmpty()) {
            String token = normalizeIndustryTerm(normalized);
            if (StringUtils.hasText(token)) {
                tokens.add(token);
            }
        }

        return tokens;
    }

    private String normalizeIndustryTerm(String text) {
        String token = normalizeToken(text);
        token = token.replace("/", "").replace("-", "").replace("_", "");
        if (INDUSTRY_ALIAS_MAP.containsKey(token)) {
            return INDUSTRY_ALIAS_MAP.get(token);
        }
        return token;
    }

    private double calculateTextOverlapRatio(String left, String right) {
        Set<String> leftGrams = buildCharacterGrams(left);
        Set<String> rightGrams = buildCharacterGrams(right);
        if (leftGrams.isEmpty() || rightGrams.isEmpty()) {
            return 0.0;
        }

        Set<String> intersection = new HashSet<>(leftGrams);
        intersection.retainAll(rightGrams);
        if (intersection.isEmpty()) {
            return 0.0;
        }

        Set<String> union = new HashSet<>(leftGrams);
        union.addAll(rightGrams);
        if (union.isEmpty()) {
            return 0.0;
        }
        return intersection.size() / (double) union.size();
    }

    private Set<String> buildCharacterGrams(String text) {
        if (!StringUtils.hasText(text)) {
            return Collections.emptySet();
        }

        String normalized = normalizeIndustryTerm(text);
        if (!StringUtils.hasText(normalized)) {
            return Collections.emptySet();
        }

        String compact = normalized.replaceAll("[^a-z0-9\u4e00-\u9fa5]", "");
        if (compact.length() <= 1) {
            return new HashSet<>(Collections.singletonList(compact));
        }

        Set<String> grams = new HashSet<>();
        for (int i = 0; i < compact.length() - 1; i++) {
            grams.add(compact.substring(i, i + 2));
        }
        return grams;
    }

    private String normalizeToken(String token) {
        return token == null ? "" : token.trim().toLowerCase();
    }

    /**
     * 辅助方法：根据城市获取省份（用于位置匹配）
     */
    private String getProvince(String city) {
        if (city.contains("北京") || city.contains("上海") || city.contains("天津") || city.contains("重庆")) {
            return city;
        } else if (city.contains("深圳") || city.contains("广州") || city.contains("中山")) {
            return "广东省";
        } else if (city.contains("杭州") || city.contains("宁波") || city.contains("温州")) {
            return "浙江省";
        } else if (city.contains("苏州") || city.contains("南京") || city.contains("无锡")) {
            return "江苏省";
        } else {
            // 可根据实际需求扩展更多省份-城市映射
            return city;
        }
    }
}
