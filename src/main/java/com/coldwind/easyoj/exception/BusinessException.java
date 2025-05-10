package com.coldwind.easyoj.exception;

import com.coldwind.easyoj.common.ErrorCode;

/**
 * 自定义异常类
 *
 * EL PSY KONGROO
 */
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    // 新增构造方法，支持传递Exception作为异常原因
    public BusinessException(ErrorCode errorCode, String message, Exception e) {
        super(message, e);  // 调用父类RuntimeException的构造方法，传递消息和异常原因
        this.code = errorCode.getCode();
    }

    public int getCode() {
        return code;
    }
}
