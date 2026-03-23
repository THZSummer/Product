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
     */
    private String iconUrl;

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
        return ApplicationDetailResponse.builder()
                .id(application.getId())
                .name(application.getName())
                .description(application.getDescription())
                .iconUrl(application.getIconUrl())
                .type(application.getType())
                .status(application.getStatus())
                .ownerId(application.getOwnerId())
                .ownerType(application.getOwnerType())
                .callbackUrl(application.getCallbackUrl())
                .version(application.getVersion())
                .createdAt(application.getCreatedAt())
                .updatedAt(application.getUpdatedAt())
                .createdBy(application.getCreatedBy())
                .updatedBy(application.getUpdatedBy())
                .build();
    }
}
