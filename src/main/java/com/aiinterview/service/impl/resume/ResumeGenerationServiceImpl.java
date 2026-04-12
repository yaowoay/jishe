package com.aiinterview.service.impl.resume;

import com.aiinterview.config.XunfeiAIConfig;
import com.aiinterview.model.dto.resume.ResumeGenerationRequest;
import com.aiinterview.model.dto.resume.ResumeGenerationResponse;
import com.aiinterview.service.resume.ResumeGenerationService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 简历生成服务实现类
 */
@Slf4j
@Service
public class ResumeGenerationServiceImpl implements ResumeGenerationService {

    @Autowired
    private XunfeiAIConfig xunfeiConfig;
    
    private final Gson gson = new Gson();
    private final OkHttpClient client;
    
    public ResumeGenerationServiceImpl() {
        this.client = new OkHttpClient().newBuilder()
                .connectionPool(new ConnectionPool(100, 5, TimeUnit.MINUTES))
                .readTimeout(60 * 10, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public ResumeGenerationResponse generateResume(ResumeGenerationRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("开始生成简历，请求参数: {}", request);

        try {
            // 验证配置
            log.info("验证API配置...");
            if (!validateApiConfig()) {
                log.error("API配置验证失败");
                return ResumeGenerationResponse.error("API配置不完整，请检查配置");
            }
            log.info("API配置验证通过");

            // 构建提示词
            log.info("构建提示词...");
            String prompt = buildPrompt(request);
            log.info("提示词构建完成，长度: {}", prompt.length());

            // 调用讯飞AI API
            log.info("开始调用讯飞AI API...");
            String resumeContent = callXunfeiAPI(prompt);
            log.info("讯飞AI API调用完成，返回内容长度: {}", resumeContent.length());

            long endTime = System.currentTimeMillis();
            log.info("简历生成完成，总耗时: {}ms", endTime - startTime);

            return ResumeGenerationResponse.success(resumeContent, "json");

        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("简历生成失败，耗时: {}ms，错误: {}", endTime - startTime, e.getMessage(), e);
            return ResumeGenerationResponse.error("简历生成失败: " + e.getMessage());
        }
    }

    @Override
    public ResumeGenerationResponse generateResumeWithTemplate(String template, String personalInfo) {
        try {
            String prompt = String.format("请根据以下模板和个人信息生成简历：\n\n模板：%s\n\n个人信息：%s", template, personalInfo);
            String resumeContent = callXunfeiAPI(prompt);
            return ResumeGenerationResponse.success(resumeContent, "json");
        } catch (Exception e) {
            log.error("基于模板生成简历失败", e);
            return ResumeGenerationResponse.error("基于模板生成简历失败: " + e.getMessage());
        }
    }

    @Override
    public boolean validateApiConfig() {
        log.info("验证讯飞AI配置...");

        boolean isValid = true;
        StringBuilder errorMsg = new StringBuilder("配置验证失败: ");

        if (!StringUtils.hasText(xunfeiConfig.getAppId()) || "your_app_id_here".equals(xunfeiConfig.getAppId())) {
            errorMsg.append("AppId未配置或使用默认值; ");
            isValid = false;
        }

        if (!StringUtils.hasText(xunfeiConfig.getApiKey()) || "your_api_key_here".equals(xunfeiConfig.getApiKey())) {
            errorMsg.append("ApiKey未配置或使用默认值; ");
            isValid = false;
        }

        if (!StringUtils.hasText(xunfeiConfig.getApiSecret()) || "your_api_secret_here".equals(xunfeiConfig.getApiSecret())) {
            errorMsg.append("ApiSecret未配置或使用默认值; ");
            isValid = false;
        }

        if (!StringUtils.hasText(xunfeiConfig.getHostUrl())) {
            errorMsg.append("HostUrl未配置; ");
            isValid = false;
        }

        if (isValid) {
            log.info("讯飞AI配置验证成功");
            log.debug("配置信息: AppId={}, HostUrl={}", xunfeiConfig.getAppId(), xunfeiConfig.getHostUrl());
        } else {
            log.error(errorMsg.toString());
            log.error("请在application.yml中正确配置xunfei.ai相关参数");
        }

        return isValid;
    }
    
    /**
     * 构建提示词
     */
    private String buildPrompt(ResumeGenerationRequest request) {
        StringBuilder prompt = new StringBuilder();

//        // 系统角色定义
//        prompt.append("你是一位专业的简历撰写专家，请根据提供的信息生成一份结构化的简历。\n\n");
//
//        // 用户信息收集
//        prompt.append("=== 用户信息 ===\n");
//
//        if (StringUtils.hasText(request.getPersonalInfo())) {
//            prompt.append("个人基本信息：\n").append(request.getPersonalInfo()).append("\n\n");
//        }
//
//        if (StringUtils.hasText(request.getTargetPosition())) {
//            prompt.append("求职意向：").append(request.getTargetPosition()).append("\n\n");
//        }
//
//        if (StringUtils.hasText(request.getEducation())) {
//            prompt.append("教育背景：\n").append(request.getEducation()).append("\n\n");
//        }
//
//        if (StringUtils.hasText(request.getWorkExperience())) {
//            prompt.append("工作经历：\n").append(request.getWorkExperience()).append("\n\n");
//        }
//
//        if (StringUtils.hasText(request.getSkills())) {
//            prompt.append("专业技能：\n").append(request.getSkills()).append("\n\n");
//        }
//
//        if (StringUtils.hasText(request.getProjects())) {
//            prompt.append("项目经验：\n").append(request.getProjects()).append("\n\n");
//        }
//
//        // 特殊要求
//        if (StringUtils.hasText(request.getPrompt())) {
//            prompt.append("特殊要求：").append(request.getPrompt()).append("\n\n");
//        }
//
//        // 输出格式要求
//        prompt.append("=== 输出要求 ===\n");
//        prompt.append("请根据以上信息生成一份专业的简历，并严格按照以下JSON格式输出，确保JSON格式正确且可解析：\n\n");
//
//        prompt.append("{\n");
//        prompt.append("  \"personalInfo\": {\n");
//        prompt.append("    \"name\": \"姓名\",\n");
//        prompt.append("    \"phone\": \"联系电话\",\n");
//        prompt.append("    \"email\": \"邮箱地址\",\n");
//        prompt.append("    \"address\": \"居住地址\",\n");
//        prompt.append("    \"summary\": \"个人简介/职业概述\"\n");
//        prompt.append("  },\n");
//        prompt.append("  \"objective\": \"求职意向和职业目标\",\n");
//        prompt.append("  \"education\": [\n");
//        prompt.append("    {\n");
//        prompt.append("      \"school\": \"学校名称\",\n");
//        prompt.append("      \"degree\": \"学历学位\",\n");
//        prompt.append("      \"major\": \"专业\",\n");
//        prompt.append("      \"period\": \"就读时间\",\n");
//        prompt.append("      \"achievements\": \"主要成就或荣誉\"\n");
//        prompt.append("    }\n");
//        prompt.append("  ],\n");
//        prompt.append("  \"workExperience\": [\n");
//        prompt.append("    {\n");
//        prompt.append("      \"company\": \"公司名称\",\n");
//        prompt.append("      \"position\": \"职位名称\",\n");
//        prompt.append("      \"period\": \"工作时间\",\n");
//        prompt.append("      \"responsibilities\": [\"主要职责1\", \"主要职责2\"],\n");
//        prompt.append("      \"achievements\": [\"主要成就1\", \"主要成就2\"]\n");
//        prompt.append("    }\n");
//        prompt.append("  ],\n");
//        prompt.append("  \"skills\": {\n");
//        prompt.append("    \"technical\": [\"技术技能1\", \"技术技能2\"],\n");
//        prompt.append("    \"professional\": [\"专业技能1\", \"专业技能2\"],\n");
//        prompt.append("    \"language\": [\"语言能力1\", \"语言能力2\"]\n");
//        prompt.append("  },\n");
//        prompt.append("  \"projects\": [\n");
//        prompt.append("    {\n");
//        prompt.append("      \"name\": \"项目名称\",\n");
//        prompt.append("      \"role\": \"担任角色\",\n");
//        prompt.append("      \"period\": \"项目时间\",\n");
//        prompt.append("      \"description\": \"项目描述\",\n");
//        prompt.append("      \"technologies\": [\"使用技术1\", \"使用技术2\"],\n");
//        prompt.append("      \"achievements\": [\"项目成果1\", \"项目成果2\"]\n");
//        prompt.append("    }\n");
//        prompt.append("  ],\n");
//        prompt.append("  \"certificates\": [\"证书1\", \"证书2\"],\n");
//        prompt.append("  \"awards\": [\"奖项1\", \"奖项2\"]\n");
//        prompt.append("}\n\n");


        String style = request.getStyle() != null ? request.getStyle() : "professional";
        prompt.append("6. 采用").append(style).append("风格进行内容组织\n");

        return prompt.toString();
    }

    /**
     * 测试API连接（用于调试）
     */
    public String testApiConnection() {
        try {
            if (!validateApiConfig()) {
                return "配置验证失败，请检查讯飞AI配置";
            }

            String testPrompt = "请生成一份简单的测试简历，包含基本的个人信息、教育背景和工作经验。";
            log.info("开始测试API连接，测试提示词: {}", testPrompt);

            String result = callXunfeiAPI(testPrompt);
            log.info("API连接测试成功，返回结果长度: {}", result.length());

            return "API连接测试成功";

        } catch (Exception e) {
            log.error("API连接测试失败", e);
            return "API连接测试失败: " + e.getMessage();
        }
    }

    /**
     * 获取配置调试信息
     */
    public Map<String, Object> getConfigDebugInfo() {
        Map<String, Object> debugInfo = new HashMap<>();

        debugInfo.put("hostUrl", xunfeiConfig.getHostUrl());
        debugInfo.put("appId", xunfeiConfig.getAppId());
        debugInfo.put("apiKeyLength", xunfeiConfig.getApiKey() != null ? xunfeiConfig.getApiKey().length() : 0);
        debugInfo.put("apiSecretLength", xunfeiConfig.getApiSecret() != null ? xunfeiConfig.getApiSecret().length() : 0);
        debugInfo.put("connectTimeout", xunfeiConfig.getConnectTimeout());
        debugInfo.put("readTimeout", xunfeiConfig.getReadTimeout());

        // 检查是否使用默认值
        debugInfo.put("isAppIdDefault", "your_app_id_here".equals(xunfeiConfig.getAppId()));
        debugInfo.put("isApiKeyDefault", "your_api_key_here".equals(xunfeiConfig.getApiKey()));
        debugInfo.put("isApiSecretDefault", "your_api_secret_here".equals(xunfeiConfig.getApiSecret()));

        return debugInfo;
    }
    
    /**
     * 调用讯飞AI API
     */
    private String callXunfeiAPI(String question) throws Exception {
        log.info("开始调用讯飞AI API");

        String authUrl = getAuthUrl(xunfeiConfig.getHostUrl(), xunfeiConfig.getApiKey(), xunfeiConfig.getApiSecret());
        JSONObject requestJson = buildRequestJson(question);

        log.debug("请求URL: {}", authUrl);
        log.debug("请求参数: {}", requestJson.toString());

        RequestBody body = RequestBody.create(
            requestJson.toString(),
            MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(authUrl)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                log.error("API调用失败: {} - {}", response.code(), errorBody);
                throw new IOException("API调用失败: " + response.code() + " - " + errorBody);
            }

            String responseBodyString = response.body().string();
            log.debug("API原始响应: {}", responseBodyString);

            ResponseData responseData = gson.fromJson(responseBodyString, ResponseData.class);

            // 检查响应头状态
            if (responseData.header.code != 0) {
                log.error("API返回错误状态: code={}, message={}", responseData.header.code, responseData.header.message);
                throw new RuntimeException("API返回错误: " + responseData.header.message);
            }

            String resText = responseData.payload.resData.text;
            if (resText == null || resText.trim().isEmpty()) {
                log.error("API返回的text字段为空");
                throw new RuntimeException("API返回的内容为空");
            }

            String decodedText = new String(Base64.getUrlDecoder().decode(resText));
            log.info("成功解码API响应，长度: {}", decodedText.length());

            // 处理和验证返回的JSON数据
            return processAIResponse(decodedText);
        }
    }

    /**
     * 处理AI返回的响应数据
     */
    private String processAIResponse(String aiResponse) {
        try {
            log.info("AI原始响应: {}", aiResponse);

            // 首先尝试解析为JSON
            JSONObject responseJson = JSONObject.parseObject(aiResponse);

            // 检查是否是讯飞API的links格式响应
            if (responseJson.containsKey("links")) {
                return handleLinksResponse(responseJson);
            }

            // 检查是否是标准的简历JSON格式
            if (isValidResumeJson(responseJson)) {
                log.info("检测到标准简历JSON格式");
                return aiResponse;
            }

            // 尝试提取JSON部分（处理可能包含额外文本的响应）
            String jsonContent = extractJsonFromResponse(aiResponse);
            JSONObject jsonObject = JSONObject.parseObject(jsonContent);

            // 验证必要字段
            validateResumeJson(jsonObject);

            log.info("JSON验证成功，返回处理后的简历数据");
            return jsonContent;

        } catch (Exception e) {
            log.error("处理AI响应失败: {}", e.getMessage());
            // 如果JSON解析失败，返回格式化的文本
            return formatAsPlainText(aiResponse);
        }
    }

    /**
     * 处理讯飞API的links格式响应
     */
    private String handleLinksResponse(JSONObject responseJson) {
        log.info("检测到讯飞API的links格式响应");

        // 检查links数组
        if (responseJson.containsKey("links") && responseJson.getJSONArray("links") != null) {
            JSONArray links = responseJson.getJSONArray("links");

            if (links.size() > 0) {
                JSONObject linkObj = links.getJSONObject(0);
                String imgUrl = linkObj.getString("img_url");
                String wordUrl = linkObj.getString("word_url");

                // 创建标准化的简历响应格式
                JSONObject standardResponse = new JSONObject();
                standardResponse.put("format", "links");
                standardResponse.put("message", "简历生成成功，已生成下载链接");

                JSONObject content = new JSONObject();
                content.put("img_url", imgUrl);
                content.put("word_url", wordUrl);
                content.put("description", "简历已生成，可通过以下链接下载");

                standardResponse.put("content", content);
                standardResponse.put("links", links);

                log.info("成功处理links响应，img_url: {}, word_url: {}", imgUrl, wordUrl);
                return standardResponse.toJSONString();
            }
        }

        // 如果links为空或格式不正确，返回错误信息
        JSONObject errorResponse = new JSONObject();
        errorResponse.put("format", "error");
        errorResponse.put("message", "API返回的链接格式异常");
        errorResponse.put("content", responseJson.toJSONString());

        return errorResponse.toJSONString();
    }

    /**
     * 检查是否是有效的简历JSON格式
     */
    private boolean isValidResumeJson(JSONObject jsonObject) {
        // 检查是否包含简历的基本字段
        String[] resumeFields = {"personalInfo", "objective", "education", "workExperience", "skills", "name", "phone", "email"};

        int matchCount = 0;
        for (String field : resumeFields) {
            if (jsonObject.containsKey(field)) {
                matchCount++;
            }
        }

        // 如果匹配到3个或以上字段，认为是简历JSON
        return matchCount >= 3;
    }

    /**
     * 从AI响应中提取JSON内容
     */
    private String extractJsonFromResponse(String response) {
        // 查找JSON开始和结束位置
        int jsonStart = response.indexOf("{");
        int jsonEnd = response.lastIndexOf("}");

        if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
            return response.substring(jsonStart, jsonEnd + 1);
        }

        // 如果没有找到完整的JSON，抛出异常
        throw new RuntimeException("无法从响应中提取有效的JSON格式");
    }

    /**
     * 验证简历JSON的必要字段
     */
    private void validateResumeJson(JSONObject jsonObject) {
        // 检查必要的顶级字段
        String[] requiredFields = {"personalInfo", "objective", "education", "workExperience", "skills"};

        for (String field : requiredFields) {
            if (!jsonObject.containsKey(field)) {
                log.warn("缺少必要字段: {}", field);
            }
        }

        // 验证personalInfo字段
        if (jsonObject.containsKey("personalInfo")) {
            JSONObject personalInfo = jsonObject.getJSONObject("personalInfo");
            if (!personalInfo.containsKey("name") || !personalInfo.containsKey("phone")) {
                log.warn("personalInfo缺少必要的姓名或电话字段");
            }
        }
    }

    /**
     * 将响应格式化为纯文本（降级方案）
     */
    private String formatAsPlainText(String response) {
        log.info("使用降级方案处理响应为纯文本格式");

        // 创建一个简单的JSON结构包装纯文本响应
        JSONObject fallbackJson = new JSONObject();

        // 尝试清理和格式化文本
        String cleanedResponse = response.trim();
        if (cleanedResponse.isEmpty()) {
            cleanedResponse = "简历生成完成，但内容为空。请检查输入信息或重试。";
        }

        fallbackJson.put("content", cleanedResponse);
        fallbackJson.put("format", "text");
        fallbackJson.put("message", "AI返回了文本格式的简历，已进行格式化处理");
        fallbackJson.put("timestamp", System.currentTimeMillis());

        return fallbackJson.toJSONString();
    }

    /**
     * 构建请求JSON
     */
    private JSONObject buildRequestJson(String question) {
        JSONObject requestJson = new JSONObject();

        // header参数
        JSONObject header = new JSONObject();
        header.put("app_id", xunfeiConfig.getAppId());
        header.put("status", 3);
        requestJson.put("header", header);

        // parameter参数
        JSONObject parameter = new JSONObject();
        JSONObject aiResume = new JSONObject();
        JSONObject resData = new JSONObject();
        resData.put("encoding", "utf8");
        resData.put("compress", "raw");
        resData.put("format", "json");  // 返回格式为json
        aiResume.put("resData", resData);
        parameter.put("ai_resume", aiResume);
        requestJson.put("parameter", parameter);

        // payload参数
        JSONObject payload = new JSONObject();
        JSONObject reqData = new JSONObject();
        reqData.put("encoding", "utf8");
        reqData.put("compress", "raw");
        reqData.put("format", "plain");  // 输入格式为plain文本
        reqData.put("status", 3);
        // 使用UTF-8编码进行base64编码
        String text = Base64.getEncoder().encodeToString(question.getBytes(StandardCharsets.UTF_8));
        reqData.put("text", text);
        payload.put("reqData", reqData);
        requestJson.put("payload", payload);

        log.debug("构建的请求JSON: {}", requestJson.toJSONString());
        return requestJson;
    }
    
    /**
     * 生成鉴权URL
     */
    private String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
        URL url = new URL(hostUrl);
        
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        
        String preStr = "host: " + url.getHost() + "\n" + 
                       "date: " + date + "\n" + 
                       "POST " + url.getPath() + " HTTP/1.1";
        
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "hmacsha256");
        mac.init(spec);
        
        byte[] hexDigits = mac.doFinal(preStr.getBytes(StandardCharsets.UTF_8));
        String sha = Base64.getEncoder().encodeToString(hexDigits);
        
        String authorization = String.format(
            "api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"",
            apiKey, "hmac-sha256", "host date request-line", sha
        );
        
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse("https://" + url.getHost() + url.getPath()))
                .newBuilder()
                .addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8)))
                .addQueryParameter("date", date)
                .addQueryParameter("host", url.getHost())
                .build();
        
        return httpUrl.toString();
    }
    
    // 内部类定义
    static class ResponseData {
        Header header;
        Payload payload;
    }
    
    static class Header {
        int code;
        String sid;
        String message;
    }
    
    static class Payload {
        ResData resData;
    }
    
    static class ResData {
        String encoding;
        String compress;
        String format;
        String text;
    }
}
