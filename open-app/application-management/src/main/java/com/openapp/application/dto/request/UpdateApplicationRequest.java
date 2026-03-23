package com.openapp.application.dto.request;

import com.openapp.application.domain.enums.AppType;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新应用请求 DTO
 * 
 * @author open-app
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateApplicationRequest {

    /**
     * 应用名称
     */
    @Size(max = 128, message = "应用名称长度不能超过 128 个字符")
    private String name;

    /**
     * 应用描述
     */
    @Size(max = 1000, message = "应用描述长度不能超过 1000 个字符")
    private String description;

    /**
     * 应用图标 URL
     */
    @Pattern(
        regexp = "^https?://[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=%]+$",
        message = "图标 URL 格式不正确"
    )
    private String iconUrl;

    /**
     * 应用类型
     */
    private AppType type;

    /**
     * 回调 URL
     */
    @Pattern(
        regexp = "^https?://[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=%]+$",
        message = "回调 URL 格式不正确"
    )
    private String callbackUrl;
}
