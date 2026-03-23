package com.openapp.application.repository;

import com.openapp.application.domain.Application;
import com.openapp.application.domain.enums.AppStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 应用数据访问接口
 * 封装数据访问逻辑，提供统一的Repository层
 * 
 * @author open-app
 * @since 1.0.0
 */
public interface ApplicationRepository {

    /**
     * 创建新应用
     *
     * @param app 应用实体
     * @return 创建后的应用实体（包含生成的 ID）
     */
    Application create(Application app);

    /**
     * 根据 ID 查询应用（包含软删除过滤）
     *
     * @param id 应用 ID
     * @return 应用实体，不存在返回 Optional.empty()
     */
    Optional<Application> findById(String id);

    /**
     * 根据所有者 ID 查询应用列表（支持分页）
     *
     * @param ownerId 所有者 ID
     * @param pageable 分页参数
     * @return 应用分页列表
     */
    Page<Application> findByOwner(String ownerId, Pageable pageable);

    /**
     * 根据状态查询应用列表
     *
     * @param status 应用状态
     * @return 应用列表
     */
    List<Application> findByStatus(AppStatus status);

    /**
     * 根据所有者 ID 和状态查询应用列表（支持分页）
     *
     * @param ownerId 所有者 ID
     * @param status 应用状态
     * @param pageable 分页参数
     * @return 应用分页列表
     */
    Page<Application> findByOwnerIdAndStatus(String ownerId, AppStatus status, Pageable pageable);

    /**
     * 更新应用（包含乐观锁版本控制）
     *
     * @param app 应用实体
     * @return 更新后的应用实体
     * @throws OptimisticLockException 版本号不匹配时抛出
     */
    Application update(Application app);

    /**
     * 软删除应用
     *
     * @param id 应用 ID
     * @param deletedBy 删除人 ID
     * @return 是否删除成功
     */
    boolean softDelete(String id, String deletedBy);

    /**
     * 恢复已软删除的应用
     *
     * @param id 应用 ID
     * @return 是否恢复成功
     */
    boolean restore(String id);

    /**
     * 检查应用名称在所有者下是否唯一
     *
     * @param ownerId 所有者 ID
     * @param name 应用名称
     * @return 如果名称已存在返回 true
     */
    boolean existsByOwnerIdAndName(String ownerId, String name);

    /**
     * 检查应用名称在所有者下是否唯一（排除指定 ID）
     *
     * @param ownerId 所有者 ID
     * @param name 应用名称
     * @param excludeId 排除的应用 ID
     * @return 如果名称已存在返回 true
     */
    boolean existsByOwnerIdAndNameExcludeId(String ownerId, String name, String excludeId);

    /**
     * 根据所有者 ID 和名称查询应用
     *
     * @param ownerId 所有者 ID
     * @param name 应用名称
     * @return 应用实体，不存在返回 Optional.empty()
     */
    Optional<Application> findByOwnerIdAndName(String ownerId, String name);

    /**
     * 查询所有应用（包含已删除的，用于管理员）
     *
     * @return 应用列表
     */
    List<Application> findAllWithDeleted();

    /**
     * 查询所有应用（支持分页）
     *
     * @param pageable 分页参数
     * @return 应用分页列表
     */
    Page<Application> findAll(Pageable pageable);

    /**
     * 根据 ID 查询应用（包含已删除的，用于管理员）
     *
     * @param id 应用 ID
     * @return 应用实体，不存在返回 Optional.empty()
     */
    Optional<Application> findByIdWithDeleted(String id);

    /**
     * 统计所有者下的应用数量
     *
     * @param ownerId 所有者 ID
     * @return 应用数量
     */
    long countByOwnerId(String ownerId);

    /**
     * 统计指定状态的应用数量
     *
     * @param status 应用状态
     * @return 应用数量
     */
    long countByStatus(AppStatus status);
}
