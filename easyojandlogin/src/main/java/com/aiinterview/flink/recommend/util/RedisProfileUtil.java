package com.aiinterview.flink.recommend.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aiinterview.flink.recommend.entity.UserFullProfile;
import com.aiinterview.flink.recommend.mapper.UserProfileMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RedisProfileUtil {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    // 注入 MySQL 画像 Mapper
    @Resource
    private UserProfileMapper userProfileMapper;

    private static final String BASE_KEY_PREFIX = "user:profile:base:";
    private static final String REAL_KEY_PREFIX = "user:profile:real:";
    private static final String BASE_HASH_KEY = "user:profile:base";
    private static final String REAL_HASH_KEY = "user:profile:real";

    // Redis 缓存过期时间（小时）
    private static final long CACHE_EXPIRE_HOURS = 24;

    // 是否优先读取MySQL（true: 先读库再回填缓存；false: 默认缓存优先）
    @Value("${recommend.profile.mysql-first:true}")
    private Boolean mysqlFirst;

    /**
     * 获取用户完整画像：合并静态+实时 + MySQL兜底
     */
    public UserFullProfile getFullUserProfile(String userId) {
        UserFullProfile fullProfile = new UserFullProfile();

        Integer parsedUserId = parseUserId(userId);
        if (parsedUserId != null) {
            fullProfile.setUserId(parsedUserId);
        }

        // 1. 获取静态基础画像（支持 MySQL 优先/Redis 优先 两种模式）
        boolean preferMysql = mysqlFirst != null && mysqlFirst;
        if (preferMysql && parsedUserId != null) {
            UserFullProfile dbProfile = userProfileMapper.selectByUserId(parsedUserId);
            if (dbProfile != null) {
                applyBaseProfileFromDb(fullProfile, dbProfile);
                saveBaseProfileToRedis(userId, dbProfile);
                log.debug("画像读取来源=MySQL优先，userId={}", userId);
            } else {
                String baseJson = readBaseProfileJson(userId);
                if (StringUtils.hasText(baseJson)) {
                    applyBaseProfileFromJson(fullProfile, baseJson);
                    log.debug("画像读取来源=Redis回退（MySQL无数据），userId={}", userId);
                }
            }
        } else {
            String baseJson = readBaseProfileJson(userId);
            if (StringUtils.hasText(baseJson)) {
                applyBaseProfileFromJson(fullProfile, baseJson);
                log.debug("画像读取来源=Redis优先，userId={}", userId);
            } else if (parsedUserId != null) {
                UserFullProfile dbProfile = userProfileMapper.selectByUserId(parsedUserId);
                if (dbProfile != null) {
                    applyBaseProfileFromDb(fullProfile, dbProfile);
                    saveBaseProfileToRedis(userId, dbProfile);
                    log.debug("画像读取来源=MySQL兜底，userId={}", userId);
                }
            }
        }

        // 2. 获取实时偏好画像（不变）
        String realJson = readRealtimeProfileJson(userId);
        if (StringUtils.hasText(realJson)) {
            JSONObject realObj = JSON.parseObject(realJson);
            fullProfile.setRealTimeJobTypes(realObj.getString("real_time_job_types"));
            fullProfile.setRealTimeCities(realObj.getString("real_time_cities"));
            fullProfile.setRealTimeSalaryMin(realObj.getInteger("real_time_salary_min"));
            fullProfile.setRealTimeSalaryMax(realObj.getInteger("real_time_salary_max"));
        }

        return fullProfile;
    }

    private void applyBaseProfileFromJson(UserFullProfile fullProfile, String baseJson) {
        JSONObject baseObj = JSON.parseObject(baseJson);
        if (fullProfile.getUserId() == null) {
            Integer userIdFromData = baseObj.getInteger("user_id");
            if (userIdFromData != null) {
                fullProfile.setUserId(userIdFromData);
            }
        }
        fullProfile.setPosition(baseObj.getString("position"));
        fullProfile.setExpectedCity(baseObj.getString("expected_city"));
        fullProfile.setExpectedSalaryMin(baseObj.getInteger("expected_salary_min"));
        fullProfile.setExpectedSalaryMax(baseObj.getInteger("expected_salary_max"));

        String expectedIndustry = baseObj.getString("expected_industry");
        if (!StringUtils.hasText(expectedIndustry)) {
            expectedIndustry = baseObj.getString("expectedIndustry");
        }
        fullProfile.setExpectedIndustry(expectedIndustry);

        fullProfile.setSkills(baseObj.getString("skills"));
        fullProfile.setEducationLevel(baseObj.getString("education"));
        fullProfile.setWorkYears(baseObj.getByte("work_years"));
    }

    private void applyBaseProfileFromDb(UserFullProfile fullProfile, UserFullProfile dbProfile) {
        fullProfile.setUserId(dbProfile.getUserId());
        fullProfile.setPosition(dbProfile.getPosition());
        fullProfile.setExpectedCity(dbProfile.getExpectedCity());
        fullProfile.setExpectedSalaryMin(dbProfile.getExpectedSalaryMin());
        fullProfile.setExpectedSalaryMax(dbProfile.getExpectedSalaryMax());
        fullProfile.setExpectedIndustry(dbProfile.getExpectedIndustry());
        fullProfile.setSkills(dbProfile.getSkills());
        fullProfile.setEducationLevel(dbProfile.getEducationLevel());
        fullProfile.setWorkYears(dbProfile.getWorkYears());
    }

    /**
     * 新增：将 MySQL 基础画像保存到 Redis（与原有结构一致）
     */
    private void saveBaseProfileToRedis(String userId, UserFullProfile dbProfile) {
        try {
            JSONObject json = new JSONObject();
            json.put("user_id", dbProfile.getUserId());
            json.put("position", dbProfile.getPosition());
            json.put("expected_city", dbProfile.getExpectedCity());
            json.put("expected_salary_min", dbProfile.getExpectedSalaryMin());
            json.put("expected_salary_max", dbProfile.getExpectedSalaryMax());
            json.put("expected_industry", dbProfile.getExpectedIndustry());
            json.put("skills", dbProfile.getSkills());
            json.put("education", dbProfile.getEducationLevel());
            json.put("work_years", dbProfile.getWorkYears());

            String value = json.toJSONString();
            String redisKey = BASE_KEY_PREFIX + userId;

            // 保存为 string 结构，兼容原有逻辑
            stringRedisTemplate.opsForValue().set(redisKey, value, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        } catch (Exception e) {
            // 不影响主流程
        }
    }

    // ====================== 以下代码完全不变，保持你原有逻辑 ======================
    private String readBaseProfileJson(String userId) {
        String keyProfile = stringRedisTemplate.opsForValue().get(BASE_KEY_PREFIX + userId);
        if (StringUtils.hasText(keyProfile)) {
            return keyProfile;
        }

        Object hashProfile = stringRedisTemplate.opsForHash().get(BASE_HASH_KEY, userId);
        if (hashProfile == null) {
            hashProfile = stringRedisTemplate.opsForHash().get(BASE_HASH_KEY, BASE_KEY_PREFIX + userId);
        }
        return hashProfile == null ? null : hashProfile.toString();
    }

    private String readRealtimeProfileJson(String userId) {
        String keyProfile = stringRedisTemplate.opsForValue().get(REAL_KEY_PREFIX + userId);
        if (StringUtils.hasText(keyProfile)) {
            return keyProfile;
        }

        Object hashProfile = stringRedisTemplate.opsForHash().get(REAL_HASH_KEY, userId);
        if (hashProfile != null) {
            return hashProfile.toString();
        }

        Object legacyProfile = stringRedisTemplate.opsForHash().get(REAL_HASH_KEY, REAL_HASH_KEY);
        return legacyProfile == null ? null : legacyProfile.toString();
    }

    private Integer parseUserId(String userId) {
        try {
            return Integer.parseInt(userId);
        } catch (Exception ignore) {
            return null;
        }
    }
}