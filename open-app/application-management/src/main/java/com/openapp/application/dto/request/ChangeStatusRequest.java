package com.openapp.application.dto.request;

import com.openapp.application.domain.enums.AppStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 变更应用状态请求 DTO
 * 
 * @author open-app
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeStatusRequest {

    /**
     * 目标状态
     */
    @NotNull(message = "目标状态不能为空")
    private AppStatus status;

    /**
     * 变更原因
     */
    @NotBlank(message = "变更原因不能为空")
    @Size(max = 500, message = "变更原因长度不能超过 500 个字符")
    private String reason;
}
