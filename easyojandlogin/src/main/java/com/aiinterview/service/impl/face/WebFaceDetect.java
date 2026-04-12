package com.aiinterview.service.impl.face;


import com.aiinterview.service.face.FaceDetectionService;
import com.aiinterview.utils.FileUtil;
import com.aiinterview.utils.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class WebFaceDetect implements FaceDetectionService {

    class Property {
        public final static String requestUrl = "https://api.xf-yun.com/v1/private/s67c9c78c";
        public final static String appid = "96c077b7"; // 填写控制台获取的APPID
        public final static String apiSecret = "NTY1NDg5MzZmYTE3MGU5YzBmMTZkYjMw"; // 填写控制台获取的APISecret
        public final static String apiKey = "8cf73945dd152d12c504928d26cf529e"; // 填写控制台获取的APIKey
        public final static String serviceId = "s67c9c78c";
    }

    @Override
    public ResponseData detectFace(String imagePath) throws Exception {
        return faceContrast(imagePath);
    }

    public String getXParam(String imageBase641, String imageEncoding1) {
        JsonObject jso = new JsonObject();

        JsonObject header = new JsonObject();
        header.addProperty("app_id", Property.appid);
        header.addProperty("status", 3);
        jso.add("header", header);

        JsonObject parameter = new JsonObject();
        JsonObject service = new JsonObject();
        service.addProperty("service_kind", "face_detect");
        service.addProperty("detect_points", "1");
        service.addProperty("detect_property", "1");

        JsonObject faceCompareResult = new JsonObject();
        faceCompareResult.addProperty("encoding", "utf8");
        faceCompareResult.addProperty("format", "json");
        faceCompareResult.addProperty("compress", "raw");
        service.add("face_detect_result", faceCompareResult);
        parameter.add(Property.serviceId, service);
        jso.add("parameter", parameter);

        JsonObject payload = new JsonObject();
        JsonObject inputImage1 = new JsonObject();
        inputImage1.addProperty("encoding", imageEncoding1);
        inputImage1.addProperty("image", imageBase641);
        payload.add("input1", inputImage1);
        jso.add("payload", payload);

        return jso.toString();
    }

    private byte[] readImage(String imagePath) throws IOException {
        InputStream is = new FileInputStream(imagePath);
        byte[] imageByteArray1 = FileUtil.read(imagePath);
        return imageByteArray1;
    }

    public ResponseData faceContrast(String imageFirstUrl) throws Exception {
        String url = assembleRequestUrl(Property.requestUrl, Property.apiKey, Property.apiSecret);

        String imageBase641 = Base64.getEncoder().encodeToString(readImage(imageFirstUrl));
        String imageEncoding1 = imageFirstUrl.substring(imageFirstUrl.lastIndexOf(".") + 1);

        return handleFaceContrastRes(url, getXParam(imageBase641, imageEncoding1));
    }

    private ResponseData handleFaceContrastRes(String url, String bodyParam) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/json");
        String result = HttpUtil.doPost2(url, headers, bodyParam);
        if (result != null) {
            System.out.println("人脸检测及属性分析接口调用结果：" + result);
            return new Gson().fromJson(result, ResponseData.class);
        } else {
            return null;
        }
    }

    public static String assembleRequestUrl(String requestUrl, String apiKey, String apiSecret) {
        URL url = null;
        String httpRequestUrl = requestUrl.replace("ws://", "http://").replace("wss://", "https://");
        try {
            url = new URL(httpRequestUrl);
            SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
            format.setTimeZone(TimeZone.getTimeZone("GMT"));
            String date = format.format(new Date());

            String host = url.getHost();
            if (url.getPort() != 80 && url.getPort() != 443) {
                host = host + ":" + String.valueOf(url.getPort());
            }
            StringBuilder builder = new StringBuilder("host: ").append(host).append("\n")
                    .append("date: ").append(date).append("\n")
                    .append("POST ").append(url.getPath()).append(" HTTP/1.1");
            Charset charset = Charset.forName("UTF-8");
            Mac mac = Mac.getInstance("hmacsha256");
            SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(charset), "hmacsha256");
            mac.init(spec);
            byte[] hexDigits = mac.doFinal(builder.toString().getBytes(charset));
            String sha = Base64.getEncoder().encodeToString(hexDigits);

            String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
            String authBase = Base64.getEncoder().encodeToString(authorization.getBytes(charset));
            return String.format("%s?authorization=%s&host=%s&date=%s", requestUrl, URLEncoder.encode(authBase, "UTF-8"), URLEncoder.encode(host, "UTF-8"), URLEncoder.encode(date, "UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException("assemble requestUrl error:" + e.getMessage());
        }
    }

    public static class ResponseData {
        private Header header;
        private PayLoad payload;

        public Header getHeader() {
            return header;
        }

        public PayLoad getPayLoad() {
            return payload;
        }
    }

    public static class Header {
        private int code;
        private String message;
        private String sid;

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public String getSid() {
            return sid;
        }
    }

    public static class PayLoad {
        private FaceResult face_detect_result;

        public FaceResult getFace_detect_result() {
            return face_detect_result;
        }
    }

    public static class FaceResult {
        private String compress;
        private String encoding;
        private String format;
        private String text;

        public String getCompress() {
            return compress;
        }

        public void setCompress(String compress) {
            this.compress = compress;
        }

        public String getEncoding() {
            return encoding;
        }

        public void setEncoding(String encoding) {
            this.encoding = encoding;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
