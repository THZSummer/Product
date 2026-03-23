package com.openapp.application.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 应用管理错误码枚举
 * 定义所有业务相关的错误码和 HTTP 状态码
 * 
 * @author open-app
 * @since 1.0.0
 */
@Getter
public enum ApplicationErrorCode {

    // ============ 4xx 客户端错误 ============
    
    /**
     * 应用未找到
     */
    APPLICATION_NOT_FOUND("APP_404_001", "应用不存在", HttpStatus.NOT_FOUND),
    
    /**
     * 应用名称重复
     */
    APPLICATION_NAME_DUPLICATE("APP_409_001", "应用名称已存在", HttpStatus.CONFLICT),
    
    /**
     * 无效的状态转换
     */
    INVALID_STATUS_TRANSITION("APP_400_001", "无效的状态转换", HttpStatus.BAD_REQUEST),
    
    /**
     * 权限不足
     */
    PERMISSION_DENIED("APP_403_001", "权限不足，无法执行此操作", HttpStatus.FORBIDDEN),
    
    /**
     * 无效的所有者
     */
    INVALID_OWNER("APP_400_002", "无效的所有者信息", HttpStatus.BAD_REQUEST),
    
    /**
     * 参数验证错误
     */
    VALIDATION_ERROR("APP_400_003", "参数验证失败", HttpStatus.BAD_REQUEST),
    
    /**
     * 应用已被删除
     */
    APPLICATION_DELETED("APP_410_001", "应用已被删除", HttpStatus.GONE),
    
    /**
     * 乐观锁失败 - 数据已被修改
     */
    OPTIMISTIC_LOCK_ERROR("APP_409_002", "数据已被修改，请刷新后重试", HttpStatus.CONFLICT),
    
    /**
     * 应用状态不允许此操作
     */
    INVALID_OPERATION_FOR_STATUS("APP_400_004", "当前状态不允许执行此操作", HttpStatus.BAD_REQUEST),

    // ============ 5xx 服务器错误 ============
    
    /**
     * 数据库错误
     */
    DATABASE_ERROR("APP_500_001", "数据库操作失败", HttpStatus.INTERNAL_SERVER_ERROR),
    
    /**
     * 系统内部错误
     */
    INTERNAL_ERROR("APP_500_002", "系统内部错误", HttpStatus.INTERNAL_SERVER_ERROR),
    
    /**
     * 服务不可用
     */
    SERVICE_UNAVAILABLE("APP_503_001", "服务暂时不可用", HttpStatus.SERVICE_UNAVAILABLE);

    /**
     * 错误码
     */
    private final String code;
    
    /**
     * 错误消息
     */
    private final String message;
    
    /**
     * HTTP 状态码
     */
    private final HttpStatus httpStatus;
    
    /**
     * 构造函数
     * 
     * @param code 错误码
     * @param message 错误消息
     * @param httpStatus HTTP 状态码
     */
    ApplicationErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
    
    /**
     * 根据错误码获取错误消息
     * 
     * @param code 错误码
     * @return 错误消息，未找到返回 null
     */
    public static String getMessageByCode(String code) {
        for (ApplicationErrorCode errorCode : values()) {
            if (errorCode.getCode().equals(code)) {
                return errorCode.getMessage();
            }
        }
        return null;
    }
}
