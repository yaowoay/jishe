package org.example;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import okhttp3.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 */
public class ResumeGeneration {
    public static final String hostUrl = "https://cn-huadong-1.xf-yun.com/v1/private/s73f4add9";
    public static String QUESTION = "这里填写生成简历的要求/提示词";
    public static final String APPID = "这里填写控制台的APPID";
    public static final String APISecret = "这里填写控制台的APISecret";
    public static final String APIkey = "这里填写控制台的APIkey";
    public static final Gson gson = new Gson();
    private final static OkHttpClient client = new OkHttpClient().newBuilder()
            .connectionPool(new ConnectionPool(100, 5, TimeUnit.MINUTES))
            .readTimeout(60 * 10, TimeUnit.SECONDS)
            .build();
    public static void main(String[] args) throws Exception {
        String authUrl = getAuthUrl(hostUrl, APIkey, APISecret);
        // 鉴权URL
        //System.err.println(authUrl);

        JSONObject requestJson = new JSONObject();
        // header参数
        JSONObject header = new JSONObject();
        header.put("app_id", APPID);
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
        String text = Base64.getEncoder().encodeToString(QUESTION.getBytes());
        reqData.put("text", text);
        payload.put("reqData", reqData);
        requestJson.put("payload", payload);
        //System.err.println(requestJson.toString());

        // 发起Post请求
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                requestJson.toString());


        Request request = new Request.Builder()
                .url(authUrl)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println(response.body().string());
                throw new IOException("ERROR_MESSAGE" + response);
            }
            ResponseData responseData = gson.fromJson(response.body().string(), ResponseData.class);
            String resText = responseData.payload.resData.text;
            String res = new String(Base64.getUrlDecoder().decode(resText));
            System.out.println(res);

        }

    }


    // 鉴权方法
    public static String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
        URL url = new URL(hostUrl);
        // 时间
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        // date="Thu, 12 Oct 2023 03:05:28 GMT";
        // 拼接
        String preStr = "host: " + url.getHost() + "\n" + "date: " + date + "\n" + "POST " + url.getPath() + " HTTP/1.1";
        // System.err.println(preStr);
        // SHA256加密
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "hmacsha256");
        mac.init(spec);

        byte[] hexDigits = mac.doFinal(preStr.getBytes(StandardCharsets.UTF_8));
        // Base64加密
        String sha = Base64.getEncoder().encodeToString(hexDigits);
        // System.err.println(sha);
        // 拼接
        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
        // 拼接地址
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse("https://" + url.getHost() + url.getPath())).newBuilder().//
                addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8))).//
                addQueryParameter("date", date).//
                addQueryParameter("host", url.getHost()).//
                build();

        // System.err.println(httpUrl.toString());
        return httpUrl.toString();
    }

    class ResponseData {
        Header header;
        Payload payload;
    }

    class Header {
        int code;
        String sid;
        String message;
    }

    class Payload {
        ResData resData;
    }

    class ResData {
        String encoding;
        String compress;
        String format;
        String text;
    }

}
