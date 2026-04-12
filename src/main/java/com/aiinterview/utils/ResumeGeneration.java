package com.aiinterview.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import okhttp3.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
/**
 * AI简历生成工具类
 * 使用讯飞星火大模型API生成简历内容
 */
public class ResumeGeneration {

    // 讯飞星火API配置
    public static final String HOST_URL = "https://cn-huadong-1.xf-yun.com/v1/private/s73f4add9";
    public static final String APP_ID = "02316b6e";
    public static final String API_SECRET = "OGYzZDQ1NmRiMTA0NzVjMGI4MWQ1YmQ5";
    public static final String API_KEY = "ca9bebbd7a27438ee57c29a60bb73e41";

    // 默认提示词
    public static final String DEFAULT_PROMPT = "请根据以下信息生成一份专业的简历：姓名、工作经验、教育背景、技能特长等";

    private static final Gson gson = new Gson();
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectionPool(new ConnectionPool(100, 5, TimeUnit.MINUTES))
            .readTimeout(60 * 10, TimeUnit.SECONDS)
            .build();

    /**
     * 生成简历内容
     * @param prompt 生成简历的提示词
     * @return 生成的简历内容
     */
    public static String generateResume(String prompt) throws Exception {
        if (prompt == null || prompt.trim().isEmpty()) {
            prompt = DEFAULT_PROMPT;
        }

        String authUrl = getAuthUrl(HOST_URL, API_KEY, API_SECRET);

        // 构建请求参数
        JSONObject requestJson = buildRequestJson(prompt);

        // 发送请求
        return sendRequest(authUrl, requestJson.toString());
    }

    /**
     * 构建请求JSON
     */
    private static JSONObject buildRequestJson(String prompt) {
        JSONObject requestJson = new JSONObject();

        // header参数
        JSONObject header = new JSONObject();
        header.put("app_id", APP_ID);
        header.put("status", 3);
        requestJson.put("header", header);

        // parameter参数
        JSONObject parameter = new JSONObject();
        JSONObject aiResume = new JSONObject();
        JSONObject resData = new JSONObject();
        resData.put("encoding", "utf8");
        resData.put("compress", "raw");
        resData.put("format", "json");
        aiResume.put("resData", resData);
        parameter.put("ai_resume", aiResume);
        requestJson.put("parameter", parameter);

        // payload参数
        JSONObject payload = new JSONObject();
        JSONObject reqData = new JSONObject();
        reqData.put("encoding", "utf8");
        reqData.put("compress", "raw");
        reqData.put("format", "json");
        reqData.put("status", 3);
        String text = Base64.getEncoder().encodeToString(prompt.getBytes());
        reqData.put("text", text);
        payload.put("reqData", reqData);
        requestJson.put("payload", payload);

        return requestJson;
    }

    /**
     * 发送HTTP请求
     */
    private static String sendRequest(String authUrl, String requestBody) throws IOException {
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                requestBody
        );

        Request request = new Request.Builder()
                .url(authUrl)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                throw new IOException("API请求失败: " + response.code() + " - " + errorBody);
            }

            String responseBody = response.body().string();
            ResponseData responseData = gson.fromJson(responseBody, ResponseData.class);

            if (responseData.payload != null && responseData.payload.resData != null) {
                String resText = responseData.payload.resData.text;
                return new String(Base64.getUrlDecoder().decode(resText));
            } else {
                throw new IOException("API响应格式错误: " + responseBody);
            }
        }
    }

    /**
     * 生成鉴权URL
     */
    public static String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
        URL url = new URL(hostUrl);

        // 生成时间戳
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());

        // 构建签名字符串
        String preStr = "host: " + url.getHost() + "\n" +
                       "date: " + date + "\n" +
                       "POST " + url.getPath() + " HTTP/1.1";

        // HMAC-SHA256签名
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "hmacsha256");
        mac.init(spec);

        byte[] hexDigits = mac.doFinal(preStr.getBytes(StandardCharsets.UTF_8));
        String sha = Base64.getEncoder().encodeToString(hexDigits);

        // 构建授权头
        String authorization = String.format(
            "api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"",
            apiKey, "hmac-sha256", "host date request-line", sha
        );

        // 构建完整URL
        HttpUrl httpUrl = HttpUrl.parse("https://" + url.getHost() + url.getPath())
                .newBuilder()
                .addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8)))
                .addQueryParameter("date", date)
                .addQueryParameter("host", url.getHost())
                .build();

        return httpUrl.toString();
    }

    /**
     * 测试方法
     */
    public static void main(String[] args) {
        try {
            String prompt = "请生成一份Java开发工程师的简历，包含基本信息、工作经验、教育背景、技能特长等";
            String resume = generateResume(prompt);
            System.out.println("生成的简历内容：");
            System.out.println(resume);
        } catch (Exception e) {
            System.err.println("生成简历失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 响应数据类
    public static class ResponseData {
        public Header header;
        public Payload payload;
    }

    public static class Header {
        public int code;
        public String sid;
        public String message;
    }

    public static class Payload {
        public ResData resData;
    }

    public static class ResData {
        public String encoding;
        public String compress;
        public String format;
        public String text;
    }
}
