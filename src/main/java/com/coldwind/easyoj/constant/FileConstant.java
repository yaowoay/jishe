package com.coldwind.easyoj.constant;

/**
 * 文件常量（本地存储版）
 */
public interface FileConstant {

    /**
     * 本地存储访问前缀（与 application.yml 中 local.storage.access-prefix 一致）
     * 格式示例：http://localhost:8111/api/file/download/
     */
    String COS_HOST = "http://localhost:8111/api/file/download/";
}