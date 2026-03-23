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
 * 更新应用响应 DTO
 * 
 * @author open-app
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateApplicationResponse {

    /**
     * 应用 ID
     */
    private String id;

    /**
     * 应用名称
     */
    private String name;

    /**
     * 应用类型
     */
    private AppType type;

    /**
     * 应用状态
     */
    private AppStatus status;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 版本号（用于乐观锁）
     */
    private Integer version;

    /**
     * 从实体创建响应
     */
    public static UpdateApplicationResponse fromEntity(Application application) {
        return UpdateApplicationResponse.builder()
                .id(application.getId())
                .name(application.getName())
                .type(application.getType())
                .status(application.getStatus())
                .updatedAt(application.getUpdatedAt())
                .version(application.getVersion())
                .build();
    }
}
