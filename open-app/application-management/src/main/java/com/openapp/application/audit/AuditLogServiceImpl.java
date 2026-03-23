package com.openapp.application.audit;

import com.openapp.application.domain.ApplicationStatusLog;
import com.openapp.application.domain.enums.AppStatus;
import com.openapp.application.repository.ApplicationStatusLogRepository;
import com.openapp.application.service.ApplicationStatusLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 审计日志服务实现类
 * 
 * @author open-app
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final ApplicationStatusLogRepository statusLogRepository;
    private final ApplicationStatusLogService statusLogService;

    @Override
    public void logStatusChange(String applicationId, AppStatus oldStatus, AppStatus newStatus,
                                 String reason, String changedBy) {
        log.info("Auditing status change: applicationId={}, from={}, to={}, userId={}", 
            applicationId, oldStatus, newStatus, changedBy);

        // 使用 ApplicationStatusLogService 记录状态变更
        statusLogService.logStatusChange(applicationId, oldStatus, newStatus, reason, changedBy);
    }

    @Override
    public void logCreate(String resourceType, String resourceId, String newData, String operatedBy) {
        log.info("Auditing create: resourceType={}, resourceId={}, userId={}", 
            resourceType, resourceId, operatedBy);

        // 可以在这里扩展实现，记录到专门的审计日志表
        // 目前使用 ApplicationStatusLog 作为审计日志存储
    }

    @Override
    public void logUpdate(String resourceType, String resourceId, String oldData, String newData,
                          String reason, String operatedBy) {
        log.info("Auditing update: resourceType={}, resourceId={}, userId={}", 
            resourceType, resourceId, operatedBy);

        // 可以在这里扩展实现，记录到专门的审计日志表
    }

    @Override
    public void logDelete(String resourceType, String resourceId, String oldData, String operatedBy) {
        log.info("Auditing delete: resourceType={}, resourceId={}, userId={}", 
            resourceType, resourceId, operatedBy);

        // 可以在这里扩展实现，记录到专门的审计日志表
    }

    @Override
    public List<AuditEvent> getAuditLogs(String resourceId) {
        log.debug("Getting audit logs: resourceId={}", resourceId);

        // 从 ApplicationStatusLog 转换为 AuditEvent
        List<ApplicationStatusLog> logs = statusLogRepository.findByApplicationId(resourceId);
        
        return logs.stream()
            .map(log -> AuditEvent.builder()
                .id(log.getId())
                .type(AuditEvent.EventType.STATUS_CHANGE)
                .resourceType("APPLICATION")
                .resourceId(log.getApplicationId())
                .oldValue(log.getOldStatus())
                .newValue(log.getNewStatus())
                .reason(log.getReason())
                .operatedBy(log.getChangedBy())
                .operatedAt(log.getChangedAt())
                .build())
            .collect(Collectors.toList());
    }

    @Override
    public List<AuditEvent> getAuditLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        log.debug("Getting audit logs by time range: start={}, end={}", startTime, endTime);

        List<ApplicationStatusLog> logs = statusLogRepository.findByTimeRange(startTime, endTime);
        
        return logs.stream()
            .map(log -> AuditEvent.builder()
                .id(log.getId())
                .type(AuditEvent.EventType.STATUS_CHANGE)
                .resourceType("APPLICATION")
                .resourceId(log.getApplicationId())
                .oldValue(log.getOldStatus())
                .newValue(log.getNewStatus())
                .reason(log.getReason())
                .operatedBy(log.getChangedBy())
                .operatedAt(log.getChangedAt())
                .build())
            .collect(Collectors.toList());
    }
}
