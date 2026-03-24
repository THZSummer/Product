package com.openapp.application.dto.response;

import com.openapp.application.domain.Application;
import com.openapp.application.domain.enums.AppType;
import com.openapp.application.domain.enums.AppStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 应用详情响应 DTO
 * 
 * @author open-app
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDetailResponse {

    /**
     * 应用 ID
     */
    private String id;

    /**
     * 应用名称
     */
    private String name;

    /**
     * 应用描述
     */
    private String description;

    /**
     * 应用图标 URL
     * 注意：如果是 Base64 格式，仅返回前缀用于标识格式，不返回完整数据以减少传输量
     */
    private String iconUrl;

    /**
     * 是否有图标（用于前端判断是否显示默认图标）
     */
    private Boolean hasIcon;

    /**
     * 应用类型
     */
    private AppType type;

    /**
     * 应用状态
     */
    private AppStatus status;

    /**
     * 所有者 ID
     */
    private String ownerId;

    /**
     * 所有者类型（USER: 用户，ORG: 组织）
     */
    private String ownerType;

    /**
     * 回调 URL
     */
    private String callbackUrl;

    /**
     * 版本号（用于乐观锁）
     */
    private Integer version;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 更新人
     */
    private String updatedBy;

    /**
     * 从实体创建响应
     */
    public static ApplicationDetailResponse fromEntity(Application application) {
        ApplicationDetailResponse response = new ApplicationDetailResponse();
        response.setId(application.getId());
        response.setName(application.getName());
        response.setDescription(application.getDescription());
        
        // 处理图标 URL：如果是 Base64 格式，仅返回前缀用于标识格式
        String iconUrl = application.getIconUrl();
        if (iconUrl != null && !iconUrl.isEmpty()) {
            if (iconUrl.startsWith("data:image")) {
                // Base64 格式，仅返回前 50 个字符作为标识
                response.setIconUrl(iconUrl.length() > 50 ? iconUrl.substring(0, 50) + "..." : iconUrl);
                response.setHasIcon(true);
            } else {
                // HTTP/HTTPS URL，完整返回
                response.setIconUrl(iconUrl);
                response.setHasIcon(true);
            }
        } else {
            response.setIconUrl(null);
            response.setHasIcon(false);
        }
        
        response.setType(application.getType());
        response.setStatus(application.getStatus());
        response.setOwnerId(application.getOwnerId());
        response.setOwnerType(application.getOwnerType());
        response.setCallbackUrl(application.getCallbackUrl());
        response.setVersion(application.getVersion());
        response.setCreatedAt(application.getCreatedAt());
        response.setUpdatedAt(application.getUpdatedAt());
        response.setCreatedBy(application.getCreatedBy());
        response.setUpdatedBy(application.getUpdatedBy());
        
        return response;
    }
}
