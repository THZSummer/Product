package com.openapp.application.audit;

import com.openapp.application.domain.enums.AppStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审计日志服务接口
 * 
 * @author open-app
 * @since 1.0.0
 */
public interface AuditLogService {

    /**
     * 记录状态变更审计日志
     *
     * @param applicationId 应用 ID
     * @param oldStatus 旧状态
     * @param newStatus 新状态
     * @param reason 变更原因
     * @param changedBy 变更人 ID
     */
    void logStatusChange(String applicationId, AppStatus oldStatus, AppStatus newStatus, 
                         String reason, String changedBy);

    /**
     * 记录创建审计日志
     *
     * @param resourceType 资源类型
     * @param resourceId 资源 ID
     * @param newData 新数据
     * @param operatedBy 操作人 ID
     */
    void logCreate(String resourceType, String resourceId, String newData, String operatedBy);

    /**
     * 记录更新审计日志
     *
     * @param resourceType 资源类型
     * @param resourceId 资源 ID
     * @param oldData 旧数据
     * @param newData 新数据
     * @param reason 变更原因
     * @param operatedBy 操作人 ID
     */
    void logUpdate(String resourceType, String resourceId, String oldData, String newData, 
                   String reason, String operatedBy);

    /**
     * 记录删除审计日志
     *
     * @param resourceType 资源类型
     * @param resourceId 资源 ID
     * @param oldData 旧数据
     * @param operatedBy 操作人 ID
     */
    void logDelete(String resourceType, String resourceId, String oldData, String operatedBy);

    /**
     * 查询资源的审计日志
     *
     * @param resourceId 资源 ID
     * @return 审计日志列表
     */
    List<AuditEvent> getAuditLogs(String resourceId);

    /**
     * 根据时间范围查询审计日志
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 审计日志列表
     */
    List<AuditEvent> getAuditLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime);
}
