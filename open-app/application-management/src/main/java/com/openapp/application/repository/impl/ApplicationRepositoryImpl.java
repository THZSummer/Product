package com.openapp.application.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.openapp.application.domain.Application;
import com.openapp.application.domain.enums.AppStatus;
import com.openapp.application.exception.ApplicationErrorCode;
import com.openapp.application.exception.ApplicationException;
import com.openapp.application.mapper.ApplicationMapper;
import com.openapp.application.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 应用数据访问实现类
 * 
 * @author open-app
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ApplicationRepositoryImpl implements ApplicationRepository {

    private final ApplicationMapper applicationMapper;

    @Override
    public Application create(Application app) {
        log.debug("Creating application: {}", app.getName());
        
        // 设置默认值
        if (app.getVersion() == null) {
            app.setVersion(1);
        }
        if (app.getCreatedAt() == null) {
            app.setCreatedAt(LocalDateTime.now());
        }
        if (app.getUpdatedAt() == null) {
            app.setUpdatedAt(LocalDateTime.now());
        }
        if (app.getStatus() == null) {
            app.setStatus(AppStatus.DRAFT);
        }
        
        int rows = applicationMapper.insert(app);
        if (rows != 1) {
            throw new ApplicationException(ApplicationErrorCode.DATABASE_ERROR, "创建应用失败");
        }
        
        log.info("Application created successfully: id={}, name={}", app.getId(), app.getName());
        return app;
    }

    @Override
    public Optional<Application> findById(String id) {
        log.debug("Finding application by id: {}", id);
        Application app = applicationMapper.findById(id);
        return Optional.ofNullable(app);
    }

    @Override
    public org.springframework.data.domain.Page<Application> findByOwner(String ownerId, Pageable pageable) {
        log.debug("Finding applications by owner: {}, page: {}, size: {}", ownerId, pageable.getPageNumber(), pageable.getPageSize());
        
        // 转换为 MyBatis-Plus 的 Page
        Page<Application> page = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        
        // 构建查询条件
        LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Application::getOwnerId, ownerId)
               .isNull(Application::getDeletedAt)
               .orderByDesc(Application::getCreatedAt);
        
        Page<Application> result = applicationMapper.selectPage(page, wrapper);
        
        // 转换为 Spring Data Page
        return new org.springframework.data.domain.PageImpl<>(
            result.getRecords(),
            pageable,
            result.getTotal()
        );
    }

    @Override
    public List<Application> findByStatus(AppStatus status) {
        log.debug("Finding applications by status: {}", status);
        return applicationMapper.findByStatus(status);
    }

    @Override
    public org.springframework.data.domain.Page<Application> findByOwnerIdAndStatus(String ownerId, AppStatus status, Pageable pageable) {
        log.debug("Finding applications by owner: {} and status: {}, page: {}, size: {}", ownerId, status, pageable.getPageNumber(), pageable.getPageSize());
        
        // 转换为 MyBatis-Plus 的 Page
        Page<Application> page = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        
        // 构建查询条件
        LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Application::getOwnerId, ownerId)
               .eq(Application::getStatus, status)
               .isNull(Application::getDeletedAt)
               .orderByDesc(Application::getCreatedAt);
        
        Page<Application> result = applicationMapper.selectPage(page, wrapper);
        
        // 转换为 Spring Data Page
        return new org.springframework.data.domain.PageImpl<>(
            result.getRecords(),
            pageable,
            result.getTotal()
        );
    }

    @Override
    public Application update(Application app) {
        log.debug("Updating application: id={}, version={}", app.getId(), app.getVersion());
        
        // 更新时间
        app.setUpdatedAt(LocalDateTime.now());
        
        // 使用乐观锁更新
        int rows = applicationMapper.updateWithVersion(app);
        
        if (rows == 0) {
            // 更新失败，可能是版本号不匹配
            log.warn("Failed to update application: id={}, version={}", app.getId(), app.getVersion());
            throw new ApplicationException(ApplicationErrorCode.OPTIMISTIC_LOCK_ERROR, "更新失败，数据已被修改");
        }
        
        log.info("Application updated successfully: id={}", app.getId());
        return app;
    }

    @Override
    public boolean softDelete(String id, String deletedBy) {
        log.debug("Soft deleting application: id={}, deletedBy={}", id, deletedBy);
        
        int rows = applicationMapper.softDelete(id, deletedBy);
        
        if (rows == 0) {
            log.warn("Failed to soft delete application: id={}", id);
            return false;
        }
        
        log.info("Application soft deleted successfully: id={}", id);
        return true;
    }

    @Override
    public boolean restore(String id) {
        log.debug("Restoring application: id={}", id);
        
        int rows = applicationMapper.restore(id);
        
        if (rows == 0) {
            log.warn("Failed to restore application: id={}", id);
            return false;
        }
        
        log.info("Application restored successfully: id={}", id);
        return true;
    }

    @Override
    public boolean existsByOwnerIdAndName(String ownerId, String name) {
        log.debug("Checking if application name exists: ownerId={}, name={}", ownerId, name);
        
        Application app = applicationMapper.findByOwnerIdAndName(ownerId, name);
        return app != null;
    }

    @Override
    public boolean existsByOwnerIdAndNameExcludeId(String ownerId, String name, String excludeId) {
        log.debug("Checking if application name exists (excluding id): ownerId={}, name={}, excludeId={}", ownerId, name, excludeId);
        return applicationMapper.existsByOwnerIdAndNameExcludeId(ownerId, name, excludeId);
    }

    @Override
    public Optional<Application> findByOwnerIdAndName(String ownerId, String name) {
        log.debug("Finding application by owner and name: ownerId={}, name={}", ownerId, name);
        Application app = applicationMapper.findByOwnerIdAndName(ownerId, name);
        return Optional.ofNullable(app);
    }

    @Override
    public List<Application> findAllWithDeleted() {
        log.debug("Finding all applications (including deleted)");
        return applicationMapper.findAllWithDeleted();
    }

    @Override
    public org.springframework.data.domain.Page<Application> findAll(String keyword, Pageable pageable) {
        log.debug("Finding all applications, keyword={}, page: {}, size: {}", keyword, pageable.getPageNumber(), pageable.getPageSize());
        
        // 使用 MyBatis-Plus 分页查询
        Page<Application> mpPage = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        
        // 构建查询条件（@TableLogic 会自动添加软删除条件）
        LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Application::getCreatedAt);
        
        // 添加搜索条件（名称或描述模糊匹配）
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w
                .like(Application::getName, keyword)
                .or()
                .like(Application::getDescription, keyword)
            );
        }
        
        // 执行分页查询
        Page<Application> result = applicationMapper.selectPage(mpPage, wrapper);
        
        // 转换为 Spring Data Page
        return new org.springframework.data.domain.PageImpl<>(
            result.getRecords(),
            PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()),
            result.getTotal()
        );
    }

    @Override
    public Optional<Application> findByIdWithDeleted(String id) {
        log.debug("Finding application by id (including deleted): {}", id);
        
        LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Application::getId, id);
        
        Application app = applicationMapper.selectOne(wrapper);
        return Optional.ofNullable(app);
    }

    @Override
    public long countByOwnerId(String ownerId) {
        log.debug("Counting applications by owner: {}", ownerId);
        
        LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Application::getOwnerId, ownerId)
               .isNull(Application::getDeletedAt);
        
        return applicationMapper.selectCount(wrapper);
    }

    @Override
    public long countByStatus(AppStatus status) {
        log.debug("Counting applications by status: {}", status);
        
        LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Application::getStatus, status)
               .isNull(Application::getDeletedAt);
        
        return applicationMapper.selectCount(wrapper);
    }
}
