package com.openapp.application.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.openapp.application.domain.ApplicationStatusLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 应用状态变更日志 Mapper 接口
 * 继承 MyBatis-Plus BaseMapper，提供基础 CRUD 操作
 * 
 * @author open-app
 * @since 1.0.0
 */
@Mapper
public interface ApplicationStatusLogMapper extends BaseMapper<ApplicationStatusLog> {

    /**
     * 根据应用 ID 查询状态变更历史
     *
     * @param applicationId 应用 ID
     * @return 状态变更日志列表，按时间倒序
     */
    List<ApplicationStatusLog> findByApplicationId(@Param("applicationId") String applicationId);

    /**
     * 根据应用 ID 和时间范围查询状态变更历史
     *
     * @param applicationId 应用 ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 状态变更日志列表
     */
    List<ApplicationStatusLog> findByApplicationIdAndTimeRange(
        @Param("applicationId") String applicationId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );

    /**
     * 根据时间范围查询所有状态变更日志
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 状态变更日志列表
     */
    List<ApplicationStatusLog> findByTimeRange(
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );

    /**
     * 统计应用的状态变更次数
     *
     * @param applicationId 应用 ID
     * @return 变更次数
     */
    int countByApplicationId(@Param("applicationId") String applicationId);

    /**
     * 插入状态变更日志
     *
     * @param log 状态变更日志
     * @return 影响行数
     */
    int insertLog(ApplicationStatusLog log);
}
