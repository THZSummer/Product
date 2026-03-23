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
 * 创建应用响应 DTO
 * 
 * @author open-app
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateApplicationResponse {

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
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 从实体创建响应
     */
    public static CreateApplicationResponse fromEntity(Application application) {
        return CreateApplicationResponse.builder()
                .id(application.getId())
                .name(application.getName())
                .type(application.getType())
                .status(application.getStatus())
                .createdAt(application.getCreatedAt())
                .build();
    }
}
