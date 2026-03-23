package com.openapp.application.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.openapp.application.domain.Application;
import com.openapp.application.domain.enums.AppStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 应用 Mapper 接口
 * 继承 MyBatis-Plus BaseMapper，提供基础 CRUD 操作
 * 
 * @author open-app
 * @since 1.0.0
 */
@Mapper
public interface ApplicationMapper extends BaseMapper<Application> {

    /**
     * 根据 ID 查询应用（包含软删除过滤）
     *
     * @param id 应用 ID
     * @return 应用实体，不存在返回 null
     */
    Application findById(@Param("id") String id);

    /**
     * 根据所有者 ID 查询应用列表
     *
     * @param ownerId 所有者 ID
     * @return 应用列表
     */
    List<Application> findByOwnerId(@Param("ownerId") String ownerId);

    /**
     * 根据状态查询应用列表
     *
     * @param status 应用状态
     * @return 应用列表
     */
    List<Application> findByStatus(@Param("status") AppStatus status);

    /**
     * 根据所有者 ID 和状态查询应用列表
     *
     * @param ownerId 所有者 ID
     * @param status 应用状态
     * @return 应用列表
     */
    List<Application> findByOwnerIdAndStatus(@Param("ownerId") String ownerId, @Param("status") AppStatus status);

    /**
     * 根据所有者 ID 和名称查询应用（用于唯一性校验）
     *
     * @param ownerId 所有者 ID
     * @param name 应用名称
     * @return 应用实体，不存在返回 null
     */
    Application findByOwnerIdAndName(@Param("ownerId") String ownerId, @Param("name") String name);

    /**
     * 检查名称是否已存在（排除指定 ID）
     *
     * @param ownerId 所有者 ID
     * @param name 应用名称
     * @param excludeId 排除的应用 ID
     * @return 是否存在
     */
    boolean existsByOwnerIdAndNameExcludeId(@Param("ownerId") String ownerId, @Param("name") String name, @Param("excludeId") String excludeId);

    /**
     * 软删除应用
     *
     * @param id 应用 ID
     * @param deletedBy 删除人 ID
     * @return 影响行数
     */
    int softDelete(@Param("id") String id, @Param("deletedBy") String deletedBy);

    /**
     * 恢复已软删除的应用
     *
     * @param id 应用 ID
     * @return 影响行数
     */
    int restore(@Param("id") String id);

    /**
     * 更新应用（包含乐观锁版本控制）
     *
     * @param application 应用实体
     * @return 影响行数
     */
    int updateWithVersion(Application application);

    /**
     * 查询所有应用（包含已删除的，用于管理员）
     *
     * @return 应用列表
     */
    List<Application> findAllWithDeleted();
}
