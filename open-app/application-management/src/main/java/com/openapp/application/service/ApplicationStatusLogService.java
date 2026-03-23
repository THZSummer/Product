package com.openapp.application.service;

import com.openapp.application.domain.ApplicationStatusLog;
import com.openapp.application.domain.enums.AppStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 应用状态变更日志服务接口
 * 
 * @author open-app
 * @since 1.0.0
 */
public interface ApplicationStatusLogService {

    /**
     * 记录状态变更
     *
     * @param applicationId 应用 ID
     * @param oldStatus 旧状态
     * @param newStatus 新状态
     * @param reason 变更原因
     * @param changedBy 变更人 ID
     * @return 创建的日志
     */
    ApplicationStatusLog logStatusChange(String applicationId, AppStatus oldStatus, AppStatus newStatus, 
                                          String reason, String changedBy);

    /**
     * 获取应用的状态变更历史
     *
     * @param applicationId 应用 ID
     * @return 状态变更日志列表
     */
    List<ApplicationStatusLog> getStatusHistory(String applicationId);

    /**
     * 根据时间范围查询状态变更历史
     *
     * @param applicationId 应用 ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 状态变更日志列表
     */
    List<ApplicationStatusLog> getStatusHistoryByTimeRange(String applicationId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计应用的状态变更次数
     *
     * @param applicationId 应用 ID
     * @return 变更次数
     */
    int getStatusChangeCount(String applicationId);
}
