package com.openapp.application.service.impl;

import com.openapp.application.domain.Application;
import com.openapp.application.domain.StatusTransitions;
import com.openapp.application.domain.enums.AppStatus;
import com.openapp.application.dto.request.ChangeStatusRequest;
import com.openapp.application.dto.request.CreateApplicationRequest;
import com.openapp.application.dto.request.UpdateApplicationRequest;
import com.openapp.application.dto.response.ApplicationDetailResponse;
import com.openapp.application.dto.response.CreateApplicationResponse;
import com.openapp.application.dto.response.UpdateApplicationResponse;
import com.openapp.application.exception.ApplicationErrorCode;
import com.openapp.application.exception.ApplicationException;
import com.openapp.application.mapper.ApplicationMapper;
import com.openapp.application.repository.ApplicationRepository;
import com.openapp.application.service.ApplicationService;
import com.openapp.application.service.ApplicationStatusLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 应用服务实现类
 * 
 * @author open-app
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationStatusLogService statusLogService;

    @Override
    @Transactional
    public CreateApplicationResponse createApplication(CreateApplicationRequest request, String currentUserId) {
        log.info("Creating application: name={}, type={}, userId={}", request.getName(), request.getType(), currentUserId);

        // 检查应用名称在所有者下是否唯一
        if (applicationRepository.existsByOwnerIdAndName(currentUserId, request.getName())) {
            throw new ApplicationException(ApplicationErrorCode.APPLICATION_NAME_DUPLICATE,
                "应用名称 '" + request.getName() + "' 已存在");
        }

        // 构建应用实体
        Application application = new Application(
            request.getName(),
            request.getType(),
            currentUserId,
            "USER"
        );
        application.setDescription(request.getDescription());
        application.setIconUrl(request.getIconUrl());
        application.setCallbackUrl(request.getCallbackUrl());
        application.setCreatedBy(currentUserId);
        application.setUpdatedBy(currentUserId);

        // 保存应用
        Application saved = applicationRepository.create(application);
        log.info("Application created: id={}, name={}", saved.getId(), saved.getName());

        return CreateApplicationResponse.fromEntity(saved);
    }

    @Override
    public ApplicationDetailResponse getApplication(String id, String currentUserId) {
        log.debug("Getting application: id={}, userId={}", id, currentUserId);

        Application application = applicationRepository.findById(id)
            .orElseThrow(() -> new ApplicationException(ApplicationErrorCode.APPLICATION_NOT_FOUND,
                "应用不存在：" + id));

        // 检查已删除
        if (application.isDeleted()) {
            throw new ApplicationException(ApplicationErrorCode.APPLICATION_DELETED,
                "应用已被删除：" + id);
        }

        // 权限校验：所有者或管理员
        checkOwnerOrAdmin(application, currentUserId);

        return ApplicationDetailResponse.fromEntity(application);
    }

    @Override
    public Page<ApplicationDetailResponse> listApplications(String ownerId, AppStatus status, 
                                                            String keyword, Pageable pageable, String currentUserId) {
        log.debug("Listing applications: ownerId={}, status={}, keyword={}, page={}, size={}", 
            ownerId, status, keyword, pageable.getPageNumber(), pageable.getPageSize());

        Page<Application> page;

        // 根据参数查询
        if (keyword != null && !keyword.isEmpty()) {
            // 搜索模式：按关键字搜索名称或描述
            page = applicationRepository.findAll(keyword, pageable);
        } else if (ownerId != null && !ownerId.isEmpty()) {
            // 查询指定所有者的应用
            if (status != null) {
                page = applicationRepository.findByOwnerIdAndStatus(ownerId, status, pageable);
            } else {
                page = applicationRepository.findByOwner(ownerId, pageable);
            }
        } else {
            // 查询所有应用（管理员功能）
            page = applicationRepository.findAll(null, pageable);
        }

        // 权限过滤：非管理员只能查看自己的应用
        List<ApplicationDetailResponse> content = page.getContent().stream()
            .filter(app -> isOwnerOrAdmin(app, currentUserId))
            .map(ApplicationDetailResponse::fromEntity)
            .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    @Override
    @Transactional
    public UpdateApplicationResponse updateApplication(String id, UpdateApplicationRequest request, 
                                                        String currentUserId) {
        log.info("Updating application: id={}, userId={}", id, currentUserId);

        Application application = applicationRepository.findById(id)
            .orElseThrow(() -> new ApplicationException(ApplicationErrorCode.APPLICATION_NOT_FOUND,
                "应用不存在：" + id));

        // 检查已删除
        if (application.isDeleted()) {
            throw new ApplicationException(ApplicationErrorCode.APPLICATION_DELETED,
                "应用已被删除：" + id);
        }

        // 权限校验：所有者或管理员
        checkOwnerOrAdmin(application, currentUserId);

        // 检查应用名称唯一性（如果修改了名称）
        if (request.getName() != null && !request.getName().equals(application.getName())) {
            if (applicationRepository.existsByOwnerIdAndNameExcludeId(
                    application.getOwnerId(), request.getName(), id)) {
                throw new ApplicationException(ApplicationErrorCode.APPLICATION_NAME_DUPLICATE,
                    "应用名称 '" + request.getName() + "' 已存在");
            }
            application.setName(request.getName());
        }

        // 更新字段
        if (request.getDescription() != null) {
            application.setDescription(request.getDescription());
        }
        if (request.getIconUrl() != null) {
            application.setIconUrl(request.getIconUrl());
        }
        if (request.getType() != null) {
            application.setType(request.getType());
        }
        if (request.getCallbackUrl() != null) {
            application.setCallbackUrl(request.getCallbackUrl());
        }

        application.setUpdatedBy(currentUserId);

        // 保存（包含乐观锁检查）
        try {
            Application updated = applicationRepository.update(application);
            log.info("Application updated: id={}, version={}", updated.getId(), updated.getVersion());
            return UpdateApplicationResponse.fromEntity(updated);
        } catch (org.springframework.orm.ObjectOptimisticLockingFailureException e) {
            throw new ApplicationException(ApplicationErrorCode.OPTIMISTIC_LOCK_ERROR,
                "数据已被修改，请刷新后重试");
        }
    }

    @Override
    @Transactional
    public void deleteApplication(String id, String currentUserId) {
        log.info("Deleting application: id={}, userId={}", id, currentUserId);

        Application application = applicationRepository.findById(id)
            .orElseThrow(() -> new ApplicationException(ApplicationErrorCode.APPLICATION_NOT_FOUND,
                "应用不存在：" + id));

        // 检查已删除
        if (application.isDeleted()) {
            throw new ApplicationException(ApplicationErrorCode.APPLICATION_DELETED,
                "应用已被删除：" + id);
        }

        // 权限校验：所有者或管理员
        checkOwnerOrAdmin(application, currentUserId);

        // 执行软删除
        application.softDelete(currentUserId);
        application.setStatus(AppStatus.ARCHIVED);
        application.setUpdatedBy(currentUserId);
        
        applicationRepository.update(application);
        
        log.info("Application deleted: id={}", id);
    }

    @Override
    @Transactional
    public void restoreApplication(String id, String currentUserId) {
        log.info("Restoring application: id={}, userId={}", id, currentUserId);

        Application application = applicationRepository.findByIdWithDeleted(id)
            .orElseThrow(() -> new ApplicationException(ApplicationErrorCode.APPLICATION_NOT_FOUND,
                "应用不存在：" + id));

        // 检查是否已删除
        if (!application.isDeleted()) {
            throw new ApplicationException(ApplicationErrorCode.INVALID_OPERATION_FOR_STATUS,
                "应用未被删除，无需恢复：" + id);
        }

        // 恢复操作需要管理员权限
        // 这里简化处理，实际项目中应从用户上下文获取角色
        // 假设 currentUserId 为 "admin" 或特定 ID 表示管理员
        if (!isAdmin(currentUserId)) {
            throw new ApplicationException(ApplicationErrorCode.PERMISSION_DENIED,
                "恢复应用需要管理员权限");
        }

        // 恢复应用
        application.restore();
        application.setUpdatedBy(currentUserId);
        
        applicationRepository.update(application);
        
        log.info("Application restored: id={}", id);
    }

    @Override
    @Transactional
    public void changeStatus(String id, ChangeStatusRequest request, String currentUserId) {
        log.info("Changing application status: id={}, from={}, to={}, userId={}", 
            id, request.getStatus(), currentUserId);

        Application application = applicationRepository.findById(id)
            .orElseThrow(() -> new ApplicationException(ApplicationErrorCode.APPLICATION_NOT_FOUND,
                "应用不存在：" + id));

        // 检查已删除
        if (application.isDeleted()) {
            throw new ApplicationException(ApplicationErrorCode.APPLICATION_DELETED,
                "应用已被删除：" + id);
        }

        // 状态变更需要管理员权限
        if (!isAdmin(currentUserId)) {
            throw new ApplicationException(ApplicationErrorCode.PERMISSION_DENIED,
                "状态变更需要管理员权限");
        }

        AppStatus currentStatus = application.getStatus();
        AppStatus targetStatus = request.getStatus();

        // 验证状态转换是否有效
        if (!StatusTransitions.isValidTransition(currentStatus, targetStatus)) {
            throw new ApplicationException(ApplicationErrorCode.INVALID_STATUS_TRANSITION,
                "无效的状态转换：从 " + currentStatus.getDescription() + " 到 " + targetStatus.getDescription());
        }

        // 记录状态变更日志
        String reason = StatusTransitions.getTransitionReason(currentStatus, targetStatus);
        statusLogService.logStatusChange(application.getId(), currentStatus, targetStatus, 
            request.getReason() != null ? request.getReason() : reason, currentUserId);

        // 更新状态
        application.setStatus(targetStatus);
        application.setUpdatedBy(currentUserId);
        
        applicationRepository.update(application);
        
        log.info("Application status changed: id={}, from={}, to={}", id, currentStatus, targetStatus);
    }

    /**
     * 检查用户是否为应用所有者或管理员
     */
    private void checkOwnerOrAdmin(Application application, String currentUserId) {
        if (!isOwnerOrAdmin(application, currentUserId)) {
            throw new ApplicationException(ApplicationErrorCode.PERMISSION_DENIED,
                "您没有权限访问此应用");
        }
    }

    /**
     * 判断用户是否为应用所有者或管理员
     */
    private boolean isOwnerOrAdmin(Application application, String currentUserId) {
        if (currentUserId == null) {
            return false;
        }
        // 检查是否为所有者
        if (application.getOwnerId().equals(currentUserId)) {
            return true;
        }
        // 检查是否为管理员
        return isAdmin(currentUserId);
    }

    /**
     * 判断用户是否为管理员
     * 实际项目中应从用户上下文或权限系统获取
     */
    private boolean isAdmin(String currentUserId) {
        // 简化实现：特定 ID 或角色表示管理员
        // 实际应从 JWT 或用户上下文中获取角色信息
        return "admin".equals(currentUserId) || currentUserId.startsWith("admin_");
    }
}
