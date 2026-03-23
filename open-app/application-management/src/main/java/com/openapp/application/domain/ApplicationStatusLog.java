package com.openapp.application.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 应用状态变更日志实体
 * 映射 application_status_logs 数据库表
 * 
 * @author open-app
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("application_status_logs")
public class ApplicationStatusLog {

    /**
     * 日志 ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 应用 ID
     */
    @TableField("application_id")
    private String applicationId;

    /**
     * 变更前的状态
     */
    @TableField("old_status")
    private String oldStatus;

    /**
     * 变更后的状态
     */
    @TableField("new_status")
    private String newStatus;

    /**
     * 变更原因说明
     */
    @TableField("reason")
    private String reason;

    /**
     * 变更人 ID
     */
    @TableField("changed_by")
    private String changedBy;

    /**
     * 变更时间
     */
    @TableField("changed_at")
    private LocalDateTime changedAt;

    /**
     * 构造函数 - 自动设置变更时间
     */
    public ApplicationStatusLog(String applicationId, String oldStatus, String newStatus, String reason, String changedBy) {
        this.applicationId = applicationId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.reason = reason;
        this.changedBy = changedBy;
        this.changedAt = LocalDateTime.now();
    }
}
