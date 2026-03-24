package com.openapp.application.service;

import com.openapp.application.domain.Application;
import com.openapp.application.domain.enums.AppStatus;
import com.openapp.application.dto.request.ChangeStatusRequest;
import com.openapp.application.dto.request.CreateApplicationRequest;
import com.openapp.application.dto.request.UpdateApplicationRequest;
import com.openapp.application.dto.response.ApplicationDetailResponse;
import com.openapp.application.dto.response.CreateApplicationResponse;
import com.openapp.application.dto.response.UpdateApplicationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 应用服务接口
 * 定义应用管理的核心业务逻辑
 * 
 * @author open-app
 * @since 1.0.0
 */
public interface ApplicationService {

    /**
     * 创建应用
     *
     * @param request 创建请求
     * @param currentUserId 当前用户 ID
     * @return 创建响应
     */
    CreateApplicationResponse createApplication(CreateApplicationRequest request, String currentUserId);

    /**
     * 获取应用详情
     *
     * @param id 应用 ID
     * @param currentUserId 当前用户 ID
     * @return 应用详情
     */
    ApplicationDetailResponse getApplication(String id, String currentUserId);

    /**
     * 获取应用列表（支持分页、筛选、搜索）
     *
     * @param ownerId 所有者 ID（可选）
     * @param status 状态筛选（可选）
     * @param keyword 搜索关键字（可选，支持名称和描述模糊匹配）
     * @param pageable 分页参数
     * @param currentUserId 当前用户 ID（用于权限过滤）
     * @return 应用分页列表
     */
    Page<ApplicationDetailResponse> listApplications(String ownerId, AppStatus status, String keyword, Pageable pageable, String currentUserId);

    /**
     * 更新应用
     *
     * @param id 应用 ID
     * @param request 更新请求
     * @param currentUserId 当前用户 ID
     * @return 更新响应
     */
    UpdateApplicationResponse updateApplication(String id, UpdateApplicationRequest request, String currentUserId);

    /**
     * 删除应用（软删除）
     *
     * @param id 应用 ID
     * @param currentUserId 当前用户 ID
     */
    void deleteApplication(String id, String currentUserId);

    /**
     * 恢复应用（管理员权限）
     *
     * @param id 应用 ID
     * @param currentUserId 当前用户 ID
     */
    void restoreApplication(String id, String currentUserId);

    /**
     * 变更应用状态
     *
     * @param id 应用 ID
     * @param request 变更请求
     * @param currentUserId 当前用户 ID
     */
    void changeStatus(String id, ChangeStatusRequest request, String currentUserId);
}
