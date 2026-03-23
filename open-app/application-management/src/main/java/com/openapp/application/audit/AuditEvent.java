package com.openapp.application.audit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 审计事件模型
 * 用于记录系统审计事件
 * 
 * @author open-app
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEvent {

    /**
     * 事件 ID
     */
    private String id;

    /**
     * 事件类型
     */
    private EventType type;

    /**
     * 资源类型（如 APPLICATION）
     */
    private String resourceType;

    /**
     * 资源 ID
     */
    private String resourceId;

    /**
     * 旧值（JSON 字符串）
     */
    private String oldValue;

    /**
     * 新值（JSON 字符串）
     */
    private String newValue;

    /**
     * 变更原因
     */
    private String reason;

    /**
     * 操作人 ID
     */
    private String operatedBy;

    /**
     * 操作时间
     */
    private LocalDateTime operatedAt;

    /**
     * IP 地址
     */
    private String ipAddress;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 审计事件类型枚举
     */
    public enum EventType {
        CREATE,      // 创建
        UPDATE,      // 更新
        DELETE,      // 删除
        RESTORE,     // 恢复
        STATUS_CHANGE,  // 状态变更
        LOGIN,       // 登录
        LOGOUT,      // 登出
        ACCESS       // 访问
    }
}
