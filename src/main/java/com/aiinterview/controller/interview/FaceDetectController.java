package com.aiinterview.controller.interview;

import com.aiinterview.service.face.FaceDetectionService;
import com.aiinterview.service.impl.face.WebFaceDetect;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/face")
@Slf4j
@CrossOrigin(origins = "*") // 允许跨域
public class FaceDetectController {

    @Autowired
    private FaceDetectionService faceDetect;
    private final Gson gson = new Gson();

    /**
     * 上传图片进行人脸检测
     */
    @PostMapping("/detect")
    public Map<String, Object> detectFace(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 保存上传的文件
            // 清理文件名
            //String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename().replaceAll("[^a-zA-Z0-9.]", "_");
            //String fileName = "uploads1753157902692_1.jpg";
            // 动态生成文件名：时间戳 + 随机数或原始文件名（去除特殊字符） + .jpg 后缀
            String fileName = System.currentTimeMillis() + "_" + (int)(Math.random() * 1000) + ".jpg";
            // 绝对路径
            String uploadDir = "D:\\jyl\\updown";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            Path filePath = Paths.get(uploadDir + fileName);
            System.out.println("filePath ： " +filePath.toString());
            Files.write(filePath, file.getBytes());

            log.info("开始调用人脸检测");
            // 调用人脸检测
            WebFaceDetect.ResponseData response = faceDetect.detectFace(filePath.toString());

            if (response != null && response.getPayLoad().getFace_detect_result() != null) {
                String textBase64 = response.getPayLoad().getFace_detect_result().getText();
                String faceResult = new String(Base64.getDecoder().decode(textBase64));
                System.out.println("解码后的数据格式 ： " + faceResult);
                // 将 JsonObject 转换为 JSON 字符串
                JsonObject faceJson = JsonParser.parseString(faceResult).getAsJsonObject();
                String faceJsonString = faceJson.toString();
                System.out.println("faceJson : " +faceJsonString);
                result.put("success", true);
                result.put("message", "检测成功");
                result.put("data", faceJsonString);
                result.put("fileName", fileName);
            } else {
                result.put("success", false);
                result.put("message", "检测失败");
            }

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "处理失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 获取上传的图片
     */
    @GetMapping("/image/{fileName}")
    public byte[] getImage(@PathVariable String fileName) throws IOException {
        Path imagePath = Paths.get("uploads/" + fileName);
        return Files.readAllBytes(imagePath);
    }
}
