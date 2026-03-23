package com.openapp.application.repository;

import com.openapp.application.domain.ApplicationStatusLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 应用状态变更日志数据访问接口
 * 
 * @author open-app
 * @since 1.0.0
 */
public interface ApplicationStatusLogRepository {

    /**
     * 创建状态变更日志
     *
     * @param log 状态变更日志
     * @return 创建后的日志实体
     */
    ApplicationStatusLog create(ApplicationStatusLog log);

    /**
     * 根据 ID 查询状态变更日志
     *
     * @param id 日志 ID
     * @return 日志实体，不存在返回 Optional.empty()
     */
    Optional<ApplicationStatusLog> findById(String id);

    /**
     * 根据应用 ID 查询状态变更历史
     *
     * @param applicationId 应用 ID
     * @return 状态变更日志列表，按时间倒序
     */
    List<ApplicationStatusLog> findByApplicationId(String applicationId);

    /**
     * 根据应用 ID 和时间范围查询状态变更历史
     *
     * @param applicationId 应用 ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 状态变更日志列表
     */
    List<ApplicationStatusLog> findByApplicationIdAndTimeRange(String applicationId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据时间范围查询所有状态变更日志
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 状态变更日志列表
     */
    List<ApplicationStatusLog> findByTimeRange(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计应用的状态变更次数
     *
     * @param applicationId 应用 ID
     * @return 变更次数
     */
    int countByApplicationId(String applicationId);
}
