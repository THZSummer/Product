package com.openapp.application.repository.impl;

import com.openapp.application.domain.ApplicationStatusLog;
import com.openapp.application.exception.ApplicationErrorCode;
import com.openapp.application.exception.ApplicationException;
import com.openapp.application.mapper.ApplicationStatusLogMapper;
import com.openapp.application.repository.ApplicationStatusLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 应用状态变更日志数据访问实现类
 * 
 * @author open-app
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ApplicationStatusLogRepositoryImpl implements ApplicationStatusLogRepository {

    private final ApplicationStatusLogMapper applicationStatusLogMapper;

    @Override
    public ApplicationStatusLog create(ApplicationStatusLog logEntry) {
        log.debug("Creating status change log: applicationId={}, oldStatus={}, newStatus={}", 
            logEntry.getApplicationId(), logEntry.getOldStatus(), logEntry.getNewStatus());
        
        int rows = applicationStatusLogMapper.insertLog(logEntry);
        if (rows != 1) {
            throw new ApplicationException(ApplicationErrorCode.DATABASE_ERROR, "创建状态日志失败");
        }
        
        log.info("Status change log created successfully: id={}, applicationId={}", logEntry.getId(), logEntry.getApplicationId());
        return logEntry;
    }

    @Override
    public Optional<ApplicationStatusLog> findById(String id) {
        log.debug("Finding status log by id: {}", id);
        ApplicationStatusLog logEntry = applicationStatusLogMapper.selectById(id);
        return Optional.ofNullable(logEntry);
    }

    @Override
    public List<ApplicationStatusLog> findByApplicationId(String applicationId) {
        log.debug("Finding status logs by application id: {}", applicationId);
        return applicationStatusLogMapper.findByApplicationId(applicationId);
    }

    @Override
    public List<ApplicationStatusLog> findByApplicationIdAndTimeRange(String applicationId, LocalDateTime startTime, LocalDateTime endTime) {
        log.debug("Finding status logs by application id and time range: {}, {} - {}", applicationId, startTime, endTime);
        return applicationStatusLogMapper.findByApplicationIdAndTimeRange(applicationId, startTime, endTime);
    }

    @Override
    public List<ApplicationStatusLog> findByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        log.debug("Finding status logs by time range: {} - {}", startTime, endTime);
        return applicationStatusLogMapper.findByTimeRange(startTime, endTime);
    }

    @Override
    public int countByApplicationId(String applicationId) {
        log.debug("Counting status logs by application id: {}", applicationId);
        return applicationStatusLogMapper.countByApplicationId(applicationId);
    }
}
