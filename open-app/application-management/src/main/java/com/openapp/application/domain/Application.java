package com.openapp.application.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.openapp.application.domain.enums.AppStatus;
import com.openapp.application.domain.enums.AppType;

import java.time.LocalDateTime;

/**
 * 应用实体 - 表示应用管理系统的核心实体
 * 映射 applications 数据库表结构
 * SQL 定义参考 V1__init_applications.sql
 */
@TableName("applications")
public class Application {

    /**
     * 应用唯一标识符
     * VARCHAR(64) - 使用 UUID 或雪花算法生成
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @TableField("id")
    private String id;

    /**
     * 应用名称
     * VARCHAR(128) - 所有者 (owner_id) 下唯一
     */
    @TableField("name")
    private String name;

    /**
     * 应用描述 - 详细介绍应用功能
     * TEXT 类型
     */
    @TableField("description")
    private String description;

    /**
     * 应用图标 URL，支持 http/https 路径
     * VARCHAR(512)
     */
    @TableField("icon_url")
    private String iconUrl;

    /**
     * 应用类型
     * VARCHAR(32) - WEB/MOBILE/DESKTOP/API/OTHER
     */
    @TableField("type")
    private AppType type;

    /**
     * 应用状态
     * VARCHAR(16) - draft/pending/active/suspended/archived
     */
    @TableField("status")
    private AppStatus status;

    /**
     * 所有者 ID
     * VARCHAR(64) - 用户或组织 ID
     */
    @TableField("owner_id")
    private String ownerId;

    /**
     * 所有者类型
     * VARCHAR(16) - USER/ORGANIZATION
     */
    @TableField("owner_type")
    private String ownerType;

    /**
     * 回调 URL，用于状态变更通知
     */
    @TableField("callback_url")
    private String callbackUrl;

    /**
     * 版本号用于乐观锁控制
     */
    @Version
    @TableField("version")
    private Integer version = 1;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    /**
     * 创建人 ID
     */
    @TableField("created_by")
    private String createdBy;

    /**
     * 更新人 ID
     */
    @TableField("updated_by")
    private String updatedBy;

    /**
     * 删除标记 - 软删除用的时间戳
     */
    @TableField("deleted_at")
    private LocalDateTime deletedAt;

    /**
     * 删除人 ID
     */
    @TableField("deleted_by")
    private String deletedBy;

    // Constructors
    public Application() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = AppStatus.DRAFT; // 默认状态下新建应用
    }

    public Application(String name, AppType type, String ownerId, String ownerType) {
        this.name = name;
        this.type = type;
        this.ownerId = ownerId;
        this.ownerType = ownerType;
        this.status = AppStatus.DRAFT; // 默认状态下新建应用
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.version = 1;
    }

    // Getter and Setter methods

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public AppType getType() {
        return type;
    }

    public void setType(AppType type) {
        this.type = type;
    }

    public AppStatus getStatus() {
        return status;
    }

    public void setStatus(AppStatus status) {
        this.status = status;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(String ownerType) {
        this.ownerType = ownerType;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    /**
     * 检查实体是否已软删除
     *
     * @return 如果已删除返回 true，否则返回 false
     */
    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    /**
     * 执行软删除操作
     *
     * @param deletedBy 删除人 ID
     */
    public void softDelete(String deletedBy) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }

    /**
     * 恢复软删除的记录
     */
    public void restore() {
        this.deletedAt = null;
        this.deletedBy = null;
        // 恢复后状态从 DELETED 变为 DRAFT，但这里应根据需求变更状态
        // 按照任务设计，恢复到 DRAFT 状态
        this.status = AppStatus.DRAFT;
    }

    /**
     * 更新时间戳，应在每次修改实体时调用
     */
    public void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Application{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", ownerId='" + ownerId + '\'' +
                ", ownerType='" + ownerType + '\'' +
                ", version=" + version +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
