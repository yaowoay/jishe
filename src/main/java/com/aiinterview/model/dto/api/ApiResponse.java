package com.aiinterview.model.dto.api;

import lombok.Data;

/**
 * 统一API响应格式
 */
@Data
public class ApiResponse<T> {
    
    private boolean success;
    private String message;
    private T data;
    private Integer code;
    
    public ApiResponse() {}
    
    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
    
    public ApiResponse(boolean success, String message, T data, Integer code) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.code = code;
    }
    
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(true, "操作成功");
    }
    
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "操作成功", data);
    }
    
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message);
    }
    
    public static <T> ApiResponse<T> error(String message, Integer code) {
        return new ApiResponse<>(false, message, null, code);
    }
}
