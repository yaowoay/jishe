//package com.aiinterview.utils;
//
//import cn.hutool.core.io.FileUtil;
//import cn.hutool.core.util.IdUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.File;
//import java.io.IOException;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//
///**
// * 文件工具类
// */
//@Slf4j
//public class FileUtils {
//
//    /**
//     * 上传文件
//     */
//    public static String uploadFile(MultipartFile file, String uploadPath) throws IOException {
//        if (file.isEmpty()) {
//            throw new RuntimeException("文件不能为空");
//        }
//
//        // 获取原始文件名
//        String originalFilename = file.getOriginalFilename();
//        if (originalFilename == null) {
//            throw new RuntimeException("文件名不能为空");
//        }
//
//        // 获取文件扩展名
//        String extension = FileUtil.extName(originalFilename);
//
//        // 生成新的文件名：日期 + UUID + 扩展名
//        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
//        String newFileName = dateStr + "_" + IdUtil.simpleUUID() + "." + extension;
//
//        // 创建日期目录
//        String datePath = dateStr.substring(0, 4) + "/" + dateStr.substring(4, 6) + "/" + dateStr.substring(6, 8);
//        String fullPath = uploadPath + "/" + datePath;
//
//        File dir = new File(fullPath);
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
//
//        // 保存文件
//        String filePath = fullPath + "/" + newFileName;
//        file.transferTo(new File(filePath));
//
//        log.info("文件上传成功: {}", filePath);
//
//        // 返回相对路径
//        return datePath + "/" + newFileName;
//    }
//
//    /**
//     * 删除文件
//     */
//    public static boolean deleteFile(String filePath) {
//        try {
//            File file = new File(filePath);
//            if (file.exists()) {
//                boolean deleted = file.delete();
//                log.info("文件删除{}: {}", deleted ? "成功" : "失败", filePath);
//                return deleted;
//            }
//            return true;
//        } catch (Exception e) {
//            log.error("删除文件失败: {}", filePath, e);
//            return false;
//        }
//    }
//
//    /**
//     * 获取文件大小的可读格式
//     */
//    public static String getReadableFileSize(long size) {
//        if (size <= 0) return "0 B";
//
//        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
//        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
//
//        return String.format("%.1f %s", size / Math.pow(1024, digitGroups), units[digitGroups]);
//    }
//
//    /**
//     * 检查文件类型
//     */
//    public static boolean isValidFileType(String fileName, String[] allowedTypes) {
//        if (fileName == null || allowedTypes == null) {
//            return false;
//        }
//
//        String extension = FileUtil.extName(fileName).toLowerCase();
//        for (String type : allowedTypes) {
//            if (type.toLowerCase().equals(extension)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    /**
//     * 生成唯一文件名
//     */
//    public static String generateUniqueFileName(String originalFileName) {
//        String extension = FileUtil.extName(originalFileName);
//        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
//        return dateStr + "_" + IdUtil.simpleUUID() + "." + extension;
//    }
//}
