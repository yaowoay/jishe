package com.aiinterview.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 职位搜索匹配工具类
 * 用于智能匹配经验、学历、薪资、公司规模等条件
 */
public class JobSearchMatcher {

    /**
     * 经验要求映射表
     */
    private static final Map<String, String[]> EXPERIENCE_MAPPING = new HashMap<>();
    
    /**
     * 学历要求映射表
     */
    private static final Map<String, Integer> EDUCATION_LEVEL = new HashMap<>();
    
    static {
        // 经验要求映射 - 用户搜索条件 -> 可匹配的职位要求
        EXPERIENCE_MAPPING.put("在校生/应届生", new String[]{"不限", "应届", "在校", "应届毕业生", "应届生", "实习"});
        EXPERIENCE_MAPPING.put("1年以下", new String[]{"不限", "应届", "在校", "1年", "应届毕业生"});
        EXPERIENCE_MAPPING.put("1-3年", new String[]{"不限", "应届", "1年", "1-3年", "2年", "3年"});
        EXPERIENCE_MAPPING.put("3-5年", new String[]{"不限", "1-3年", "3年", "3-5年", "4年", "5年"});
        EXPERIENCE_MAPPING.put("5-10年", new String[]{"不限", "3-5年", "5年", "5-10年", "8年"});
        EXPERIENCE_MAPPING.put("10年以上", new String[]{"不限", "5-10年", "10年"});
        
        // 学历等级 - 数字越大学历越高
        EDUCATION_LEVEL.put("不限", 0);
        EDUCATION_LEVEL.put("初中及以下", 1);
        EDUCATION_LEVEL.put("高中", 2);
        EDUCATION_LEVEL.put("中专", 2);
        EDUCATION_LEVEL.put("大专", 3);
        EDUCATION_LEVEL.put("本科", 4);
        EDUCATION_LEVEL.put("硕士", 5);
        EDUCATION_LEVEL.put("博士", 6);
    }

    /**
     * 匹配经验要求
     * @param userExperience 用户选择的经验条件
     * @param jobExperience 职位要求的经验
     * @return 是否匹配
     */
    public static boolean matchExperience(String userExperience, String jobExperience) {
        if (userExperience == null || jobExperience == null) {
            return true;
        }
        
        // 职位要求"不限"，任何条件都匹配
        if (jobExperience.contains("不限")) {
            return true;
        }
        
        // 获取用户条件对应的可匹配职位要求
        String[] matchableRequirements = EXPERIENCE_MAPPING.get(userExperience);
        if (matchableRequirements != null) {
            for (String requirement : matchableRequirements) {
                if (jobExperience.contains(requirement)) {
                    return true;
                }
            }
        }
        
        // 模糊匹配
        return jobExperience.contains(userExperience);
    }

    /**
     * 匹配学历要求
     * @param userEducation 用户的学历
     * @param jobEducation 职位要求的学历
     * @return 是否匹配
     */
    public static boolean matchEducation(String userEducation, String jobEducation) {
        if (userEducation == null || jobEducation == null) {
            return true;
        }
        
        // 职位要求"不限"，任何学历都匹配
        if (jobEducation.contains("不限")) {
            return true;
        }
        
        // 获取学历等级
        Integer userLevel = getEducationLevel(userEducation);
        Integer jobLevel = getEducationLevel(jobEducation);
        
        // 用户学历 >= 职位要求学历，则匹配
        return userLevel != null && jobLevel != null && userLevel >= jobLevel;
    }

    /**
     * 获取学历等级
     */
    private static Integer getEducationLevel(String education) {
        for (Map.Entry<String, Integer> entry : EDUCATION_LEVEL.entrySet()) {
            if (education.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return 0; // 默认返回最低等级
    }

    /**
     * 匹配薪资范围
     * @param userMinSalary 用户期望最低薪资
     * @param userMaxSalary 用户期望最高薪资
     * @param jobMinSalary 职位最低薪资
     * @param jobMaxSalary 职位最高薪资
     * @return 是否匹配
     */
    public static boolean matchSalary(Integer userMinSalary, Integer userMaxSalary, 
                                     Integer jobMinSalary, Integer jobMaxSalary) {
        // 如果用户没有设置薪资要求，全部匹配
        if (userMinSalary == null && userMaxSalary == null) {
            return true;
        }
        
        // 如果职位没有薪资信息，也匹配（面议）
        if (jobMinSalary == null && jobMaxSalary == null) {
            return true;
        }
        
        // 薪资区间有交集即匹配
        // 例如：用户期望 8-15k，职位提供 10-20k，有交集 10-15k
        if (userMinSalary != null && jobMaxSalary != null && userMinSalary > jobMaxSalary) {
            return false; // 用户最低期望 > 职位最高薪资，不匹配
        }
        
        if (userMaxSalary != null && jobMinSalary != null && userMaxSalary < jobMinSalary) {
            return false; // 用户最高期望 < 职位最低薪资，不匹配
        }
        
        return true; // 有交集，匹配
    }

    /**
     * 匹配公司规模
     * @param userScale 用户选择的规模
     * @param companyScale 公司实际规模
     * @return 是否匹配
     */
    public static boolean matchCompanyScale(String userScale, String companyScale) {
        if (userScale == null || companyScale == null) {
            return true;
        }
        
        // 提取数字进行范围匹配
        Integer userMin = extractMinNumber(userScale);
        Integer userMax = extractMaxNumber(userScale);
        Integer companyMin = extractMinNumber(companyScale);
        Integer companyMax = extractMaxNumber(companyScale);
        
        // 如果无法提取数字，使用模糊匹配
        if (userMin == null || companyMin == null) {
            return companyScale.contains(userScale) || userScale.contains(companyScale);
        }
        
        // 范围有交集即匹配
        if (userMax != null && companyMax != null) {
            return !(userMin > companyMax || userMax < companyMin);
        }
        
        return true;
    }

    /**
     * 从字符串中提取最小数字
     */
    private static Integer extractMinNumber(String text) {
        Pattern pattern = Pattern.compile("(\\d+)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return null;
    }

    /**
     * 从字符串中提取最大数字
     */
    private static Integer extractMaxNumber(String text) {
        Pattern pattern = Pattern.compile("(\\d+)");
        Matcher matcher = pattern.matcher(text);
        Integer last = null;
        while (matcher.find()) {
            last = Integer.parseInt(matcher.group(1));
        }
        return last;
    }
}
