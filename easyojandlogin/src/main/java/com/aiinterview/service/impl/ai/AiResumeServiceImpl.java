package com.aiinterview.service.impl.ai;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aiinterview.model.dto.*;
import com.aiinterview.model.dto.request.ResumeCreateRequest;
import com.aiinterview.service.ai.AiResumeService;
import com.aiinterview.utils.ResumeGeneration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * AI简历生成服务实现类
 */
@Slf4j
@Service
public class AiResumeServiceImpl implements AiResumeService {

    @Override
    public ResumeCreateRequest generateResumeFromUserInfo(String userInfo) {
        try {
            String prompt = buildPromptFromUserInfo(userInfo);
            String aiResponse = ResumeGeneration.generateResume(prompt);
            log.info("AI原始返回数据: {}", aiResponse);
            // 打印所有links元素
            com.alibaba.fastjson.JSONObject json = com.alibaba.fastjson.JSON.parseObject(aiResponse);
            com.alibaba.fastjson.JSONArray links = json.getJSONArray("links");
            if (links != null) {
                for (int i = 0; i < links.size(); i++) {
                    log.info("AI返回links[{}]: {}", i, links.getJSONObject(i).toJSONString());
                }
            }
            ResumeCreateRequest req = parseAiResponse(aiResponse);
            if (links != null && !links.isEmpty()) {
                com.alibaba.fastjson.JSONObject first = links.getJSONObject(0);
                req.setImgUrl(first.getString("img_url"));
                req.setWordUrl(first.getString("word_url"));
                req.setPdfUrl(first.getString("pdf_url"));
            }
            return req;
        } catch (Exception e) {
            log.error("AI生成简历失败", e);
            return createDefaultResume(userInfo);
        }
    }

    @Override
    public ResumeCreateRequest generateResumeForJob(String jobTitle, String userInfo) {
        try {
            String prompt = buildPromptForJob(jobTitle, userInfo);
            String aiResponse = ResumeGeneration.generateResume(prompt);
            log.info("AI原始返回数据: {}", aiResponse);
            // 打印所有links元素
            com.alibaba.fastjson.JSONObject json = com.alibaba.fastjson.JSON.parseObject(aiResponse);
            com.alibaba.fastjson.JSONArray links = json.getJSONArray("links");
            if (links != null) {
                for (int i = 0; i < links.size(); i++) {
                    log.info("AI返回links[{}]: {}", i, links.getJSONObject(i).toJSONString());
                }
            }
            ResumeCreateRequest req = parseAiResponse(aiResponse);
            if (links != null && !links.isEmpty()) {
                com.alibaba.fastjson.JSONObject first = links.getJSONObject(0);
                req.setImgUrl(first.getString("img_url"));
                req.setWordUrl(first.getString("word_url"));
                req.setPdfUrl(first.getString("pdf_url"));
            }
            return req;
        } catch (Exception e) {
            log.error("AI生成简历失败", e);
            return createDefaultResume(userInfo);
        }
    }

    @Override
    public ResumeCreateRequest optimizeResume(String existingResume) {
        try {
            String prompt = buildOptimizePrompt(existingResume);
            String aiResponse = ResumeGeneration.generateResume(prompt);
            return parseAiResponse(aiResponse);
        } catch (Exception e) {
            log.error("AI优化简历失败", e);
            return createDefaultResume(existingResume);
        }
    }

    /**
     * 根据用户信息构建提示词
     */
    private String buildPromptFromUserInfo(String userInfo) {
        return String.format(
            "请根据以下用户信息生成一份专业的简历，要求：\n" +
            "1. 生成JSON格式的简历数据\n" +
            "2. 包含基本信息、工作经历、教育经历、项目经验、技能专长\n" +
            "3. 工作经历要详细描述职责和成果\n" +
            "4. 技能专长要标注熟练度(1-5星)\n" +
            "5. 简历内容要专业、突出优势\n\n" +
            "用户信息：%s\n\n" +
            "请返回JSON格式的简历数据，包含以下字段：\n" +
            "{\n" +
            "  \"name\": \"简历名称\",\n" +
            "  \"fullName\": \"姓名\",\n" +
            "  \"phone\": \"电话\",\n" +
            "  \"email\": \"邮箱\",\n" +
            "  \"position\": \"期望职位\",\n" +
            "  \"workYears\": 工作年限,\n" +
            "  \"location\": \"所在地\",\n" +
            "  \"profile\": \"个人简介\",\n" +
            "  \"workExperiences\": [工作经历数组],\n" +
            "  \"educations\": [教育经历数组],\n" +
            "  \"projectExperiences\": [项目经验数组],\n" +
            "  \"skills\": [技能专长数组],\n" +
            "  \"additionalInfos\": [其他信息数组]\n" +
            "}",
            userInfo
        );
    }

    /**
     * 根据职位要求构建提示词
     */
    private String buildPromptForJob(String jobTitle, String userInfo) {
        return String.format(
            "请根据职位要求和用户信息生成一份针对性的简历，要求：\n" +
            "1. 针对\"%s\"职位进行优化\n" +
            "2. 突出与职位相关的技能和经验\n" +
            "3. 生成JSON格式的简历数据\n" +
            "4. 包含基本信息、工作经历、教育经历、项目经验、技能专长\n" +
            "5. 工作经历要详细描述职责和成果\n" +
            "6. 技能专长要标注熟练度(1-5星)\n\n" +
            "用户信息：%s\n\n" +
            "请返回JSON格式的简历数据，包含以下字段：\n" +
            "{\n" +
            "  \"name\": \"简历名称\",\n" +
            "  \"fullName\": \"姓名\",\n" +
            "  \"phone\": \"电话\",\n" +
            "  \"email\": \"邮箱\",\n" +
            "  \"position\": \"期望职位\",\n" +
            "  \"workYears\": 工作年限,\n" +
            "  \"location\": \"所在地\",\n" +
            "  \"profile\": \"个人简介\",\n" +
            "  \"workExperiences\": [工作经历数组],\n" +
            "  \"educations\": [教育经历数组],\n" +
            "  \"projectExperiences\": [项目经验数组],\n" +
            "  \"skills\": [技能专长数组],\n" +
            "  \"additionalInfos\": [其他信息数组]\n" +
            "}",
            jobTitle, userInfo
        );
    }

    /**
     * 构建优化简历的提示词
     */
    private String buildOptimizePrompt(String existingResume) {
        return String.format(
            "请优化以下简历内容，要求：\n" +
            "1. 保持原有信息的基础上进行优化\n" +
            "2. 使表述更专业、更有说服力\n" +
            "3. 突出核心优势和成就\n" +
            "4. 生成JSON格式的简历数据\n" +
            "5. 包含基本信息、工作经历、教育经历、项目经验、技能专长\n\n" +
            "现有简历内容：%s\n\n" +
            "请返回优化后的JSON格式简历数据，包含以下字段：\n" +
            "{\n" +
            "  \"name\": \"简历名称\",\n" +
            "  \"fullName\": \"姓名\",\n" +
            "  \"phone\": \"电话\",\n" +
            "  \"email\": \"邮箱\",\n" +
            "  \"position\": \"期望职位\",\n" +
            "  \"workYears\": 工作年限,\n" +
            "  \"location\": \"所在地\",\n" +
            "  \"profile\": \"个人简介\",\n" +
            "  \"workExperiences\": [工作经历数组],\n" +
            "  \"educations\": [教育经历数组],\n" +
            "  \"projectExperiences\": [项目经验数组],\n" +
            "  \"skills\": [技能专长数组],\n" +
            "  \"additionalInfos\": [其他信息数组]\n" +
            "}",
            existingResume
        );
    }

    /**
     * 解析AI响应
     */
    private ResumeCreateRequest parseAiResponse(String aiResponse) {
        try {
            // 尝试解析JSON
            JSONObject jsonObject = JSON.parseObject(aiResponse);
            return parseJsonToResumeRequest(jsonObject);
        } catch (Exception e) {
            log.error("解析AI响应失败", e);
            return createDefaultResume(aiResponse);
        }
    }

    /**
     * 将JSON解析为简历请求对象
     */
    private ResumeCreateRequest parseJsonToResumeRequest(JSONObject jsonObject) {
        ResumeCreateRequest request = new ResumeCreateRequest();

        // 基本信息
        request.setName(getStringValue(jsonObject, "name", "AI生成的简历"));
        request.setFullName(getStringValue(jsonObject, "fullName", ""));
        request.setPhone(getStringValue(jsonObject, "phone", ""));
        request.setEmail(getStringValue(jsonObject, "email", ""));
        request.setPosition(getStringValue(jsonObject, "position", ""));
        request.setWorkYears(getIntegerValue(jsonObject, "workYears", 0));
//        request.setLocation(getStringValue(jsonObject, "location", ""));
        request.setProfile(getStringValue(jsonObject, "profile", ""));

        // 工作经历
        request.setWorkExperiences(parseWorkExperiences(jsonObject.getJSONArray("workExperiences")));

        // 教育经历
        request.setEducations(parseEducations(jsonObject.getJSONArray("educations")));

        // 项目经验
        request.setProjectExperiences(parseProjectExperiences(jsonObject.getJSONArray("projectExperiences")));

        // 技能专长
        request.setSkills(parseSkills(jsonObject.getJSONArray("skills")));

        // 其他信息
        request.setAdditionalInfos(parseAdditionalInfos(jsonObject.getJSONArray("additionalInfos")));

        return request;
    }

    /**
     * 解析工作经历
     */
    private List<WorkExperienceDTO> parseWorkExperiences(JSONArray array) {
        List<WorkExperienceDTO> list = new ArrayList<>();
        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                WorkExperienceDTO dto = new WorkExperienceDTO();
                dto.setCompany(getStringValue(obj, "company", ""));
                dto.setPosition(getStringValue(obj, "position", ""));
                dto.setStartDate(parseDate(getStringValue(obj, "startDate", "")));
                dto.setEndDate(parseDate(getStringValue(obj, "endDate", "")));
                dto.setResponsibility(getStringValue(obj, "responsibility", ""));
                dto.setAchievement(getStringValue(obj, "achievement", ""));
                list.add(dto);
            }
        }
        return list;
    }

    /**
     * 解析教育经历
     */
    private List<EducationDTO> parseEducations(JSONArray array) {
        List<EducationDTO> list = new ArrayList<>();
        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                EducationDTO dto = new EducationDTO();
                dto.setSchool(getStringValue(obj, "school", ""));
                dto.setMajor(getStringValue(obj, "major", ""));
                dto.setDegree(getStringValue(obj, "degree", ""));
                dto.setStartDate(parseDate(getStringValue(obj, "startDate", "")));
                dto.setEndDate(parseDate(getStringValue(obj, "endDate", "")));
                dto.setDescription(getStringValue(obj, "description", ""));
                list.add(dto);
            }
        }
        return list;
    }

    /**
     * 解析项目经验
     */
    private List<ProjectExperienceDTO> parseProjectExperiences(JSONArray array) {
        List<ProjectExperienceDTO> list = new ArrayList<>();
        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                ProjectExperienceDTO dto = new ProjectExperienceDTO();
                dto.setProjectName(getStringValue(obj, "projectName", ""));
                dto.setRole(getStringValue(obj, "role", ""));
                dto.setStartDate(parseDate(getStringValue(obj, "startDate", "")));
                dto.setEndDate(parseDate(getStringValue(obj, "endDate", "")));
                dto.setDescription(getStringValue(obj, "description", ""));
                list.add(dto);
            }
        }
        return list;
    }

    /**
     * 解析技能专长
     */
    private List<SkillDTO> parseSkills(JSONArray array) {
        List<SkillDTO> list = new ArrayList<>();
        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                SkillDTO dto = new SkillDTO();
                dto.setSkillName(getStringValue(obj, "skillName", ""));
                dto.setProficiency(getIntegerValue(obj, "proficiency", 3));
                dto.setDescription(getStringValue(obj, "description", ""));
                list.add(dto);
            }
        }
        return list;
    }

    /**
     * 解析其他信息
     */
    private List<AdditionalInfoDTO> parseAdditionalInfos(JSONArray array) {
        List<AdditionalInfoDTO> list = new ArrayList<>();
        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                AdditionalInfoDTO dto = new AdditionalInfoDTO();
                dto.setType(getStringValue(obj, "type", ""));
                dto.setName(getStringValue(obj, "name", ""));
                dto.setStartDate(parseDate(getStringValue(obj, "startDate", "")));
                dto.setEndDate(parseDate(getStringValue(obj, "endDate", "")));
                dto.setDescription(getStringValue(obj, "description", ""));
                list.add(dto);
            }
        }
        return list;
    }

    /**
     * 创建默认简历
     */
    private ResumeCreateRequest createDefaultResume(String userInfo) {
        ResumeCreateRequest request = new ResumeCreateRequest();
        request.setName("AI生成的简历");
        request.setFullName("请填写姓名");
        request.setPhone("请填写电话");
        request.setEmail("请填写邮箱");
        request.setPosition("请填写期望职位");
        request.setWorkYears(0);
//        request.setLocation("请填写所在地");
        request.setProfile("请根据您的实际情况填写个人简介");
        return request;
    }

    /**
     * 获取字符串值
     */
    private String getStringValue(JSONObject obj, String key, String defaultValue) {
        Object value = obj.get(key);
        return value != null ? value.toString() : defaultValue;
    }

    /**
     * 获取整数值
     */
    private Integer getIntegerValue(JSONObject obj, String key, Integer defaultValue) {
        Object value = obj.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return defaultValue;
    }

    /**
     * 解析日期
     */
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr);
        } catch (Exception e) {
            log.warn("日期解析失败: " + dateStr);
            return null;
        }
    }
}