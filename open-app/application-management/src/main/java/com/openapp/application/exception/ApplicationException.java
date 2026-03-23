package com.openapp.application.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 应用管理自定义异常
 * 继承 RuntimeException，用于业务逻辑异常
 * 
 * @author open-app
 * @since 1.0.0
 */
@Getter
public class ApplicationException extends RuntimeException {

    /**
     * 错误码
     */
    private final ApplicationErrorCode errorCode;
    
    /**
     * 自定义错误消息（可选，覆盖默认消息）
     */
    private final String customMessage;
    
    /**
     * 附加详情数据
     */
    private final Map<String, Object> details;

    /**
     * 构造函数 - 使用错误码
     * 
     * @param errorCode 错误码
     */
    public ApplicationException(ApplicationErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.customMessage = null;
        this.details = new HashMap<>();
    }

    /**
     * 构造函数 - 使用错误码和自定义消息
     * 
     * @param errorCode 错误码
     * @param message 自定义消息
     */
    public ApplicationException(ApplicationErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.customMessage = message;
        this.details = new HashMap<>();
    }

    /**
     * 构造函数 - 使用错误码和详情
     * 
     * @param errorCode 错误码
     * @param details 详情数据
     */
    public ApplicationException(ApplicationErrorCode errorCode, Map<String, Object> details) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.customMessage = null;
        this.details = details != null ? details : new HashMap<>();
    }

    /**
     * 构造函数 - 使用错误码、消息和详情
     * 
     * @param errorCode 错误码
     * @param message 自定义消息
     * @param details 详情数据
     */
    public ApplicationException(ApplicationErrorCode errorCode, String message, Map<String, Object> details) {
        super(message);
        this.errorCode = errorCode;
        this.customMessage = message;
        this.details = details != null ? details : new HashMap<>();
    }

    /**
     * 构造函数 - 使用错误码、消息和原因
     * 
     * @param errorCode 错误码
     * @param message 自定义消息
     * @param cause 异常原因
     */
    public ApplicationException(ApplicationErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.customMessage = message;
        this.details = new HashMap<>();
    }

    /**
     * 构造函数 - 使用错误码和原因
     * 
     * @param errorCode 错误码
     * @param cause 异常原因
     */
    public ApplicationException(ApplicationErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.customMessage = null;
        this.details = new HashMap<>();
    }

    /**
     * 添加详情数据
     * 
     * @param key 键
     * @param value 值
     * @return 当前异常对象（支持链式调用）
     */
    public ApplicationException withDetail(String key, Object value) {
        this.details.put(key, value);
        return this;
    }

    /**
     * 获取 HTTP 状态码
     * 
     * @return HTTP 状态码
     */
    public int getHttpStatus() {
        return errorCode.getHttpStatus().value();
    }

    /**
     * 获取错误码字符串
     * 
     * @return 错误码
     */
    public String getErrorCodeString() {
        return errorCode.getCode();
    }

    /**
     * 获取最终消息（优先返回自定义消息，否则返回默认消息）
     * 
     * @return 消息
     */
    public String getFinalMessage() {
        return customMessage != null ? customMessage : errorCode.getMessage();
    }
}
