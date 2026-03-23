package com.openapp.application.service.impl;

import com.openapp.application.domain.ApplicationStatusLog;
import com.openapp.application.domain.enums.AppStatus;
import com.openapp.application.repository.ApplicationStatusLogRepository;
import com.openapp.application.service.ApplicationStatusLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 应用状态变更日志服务实现类
 * 
 * @author open-app
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationStatusLogServiceImpl implements ApplicationStatusLogService {

    private final ApplicationStatusLogRepository statusLogRepository;

    @Override
    @Transactional
    public ApplicationStatusLog logStatusChange(String applicationId, AppStatus oldStatus, AppStatus newStatus,
                                                 String reason, String changedBy) {
        log.info("Logging status change: applicationId={}, from={}, to={}, userId={}", 
            applicationId, oldStatus, newStatus, changedBy);

        ApplicationStatusLog statusLog = new ApplicationStatusLog(
            applicationId,
            oldStatus != null ? oldStatus.name() : null,
            newStatus != null ? newStatus.name() : null,
            reason,
            changedBy
        );

        ApplicationStatusLog saved = statusLogRepository.create(statusLog);
        log.debug("Status log created: id={}, applicationId={}", saved.getId(), applicationId);

        return saved;
    }

    @Override
    public List<ApplicationStatusLog> getStatusHistory(String applicationId) {
        log.debug("Getting status history: applicationId={}", applicationId);
        return statusLogRepository.findByApplicationId(applicationId);
    }

    @Override
    public List<ApplicationStatusLog> getStatusHistoryByTimeRange(String applicationId, LocalDateTime startTime, LocalDateTime endTime) {
        log.debug("Getting status history by time range: applicationId={}, start={}, end={}", 
            applicationId, startTime, endTime);
        return statusLogRepository.findByApplicationIdAndTimeRange(applicationId, startTime, endTime);
    }

    @Override
    public int getStatusChangeCount(String applicationId) {
        log.debug("Getting status change count: applicationId={}", applicationId);
        return statusLogRepository.countByApplicationId(applicationId);
    }
}
