package com.openapp.application.controller;

import com.openapp.application.domain.enums.AppStatus;
import com.openapp.application.dto.request.ChangeStatusRequest;
import com.openapp.application.dto.request.CreateApplicationRequest;
import com.openapp.application.dto.request.UpdateApplicationRequest;
import com.openapp.application.dto.response.ApiResponse;
import com.openapp.application.dto.response.ApplicationDetailResponse;
import com.openapp.application.dto.response.CreateApplicationResponse;
import com.openapp.application.dto.response.PageResponse;
import com.openapp.application.dto.response.UpdateApplicationResponse;
import com.openapp.application.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 应用管理 REST Controller
 * 提供应用的 CRUD 和状态管理 API
 * 
 * @author open-app
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    /**
     * 创建应用
     * POST /api/v1/applications
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CreateApplicationResponse>> createApplication(
            @Valid @RequestBody CreateApplicationRequest request,
            @RequestParam String currentUserId) {
        log.info("Received create application request: name={}", request.getName());
        
        CreateApplicationResponse response = applicationService.createApplication(request, currentUserId);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("创建成功", response));
    }

    /**
     * 获取应用详情
     * GET /api/v1/applications/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ApplicationDetailResponse>> getApplication(
            @PathVariable String id,
            @RequestParam String currentUserId) {
        log.debug("Received get application request: id={}", id);
        
        ApplicationDetailResponse response = applicationService.getApplication(id, currentUserId);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 获取应用列表（支持分页、筛选、搜索）
     * GET /api/v1/applications
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ApplicationDetailResponse>>> listApplications(
            @RequestParam(required = false) String ownerId,
            @RequestParam(required = false) AppStatus status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam String currentUserId) {
        log.debug("Received list applications request: ownerId={}, status={}, keyword={}, page={}, size={}", 
            ownerId, status, keyword, page, pageSize);
        
        // 创建 Pageable（页码从 1 开始转为从 0 开始）
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        // Spring Page 转换为 PageResponse（页码从 0 开始转为从 1 开始）
        Page<ApplicationDetailResponse> resultPage = applicationService.listApplications(ownerId, status, keyword, pageable, currentUserId);
        PageResponse<ApplicationDetailResponse> pageResponse = PageResponse.of(
                resultPage.getContent(),
                resultPage.getTotalElements(),
                page,  // 使用请求的页码
                pageSize
        );
        
        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    /**
     * 更新应用
     * PUT /api/v1/applications/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UpdateApplicationResponse>> updateApplication(
            @PathVariable String id,
            @Valid @RequestBody UpdateApplicationRequest request,
            @RequestParam String currentUserId) {
        log.info("Received update application request: id={}", id);
        
        UpdateApplicationResponse response = applicationService.updateApplication(id, request, currentUserId);
        
        return ResponseEntity.ok(ApiResponse.success("更新成功", response));
    }

    /**
     * 删除应用（软删除）
     * DELETE /api/v1/applications/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteApplication(
            @PathVariable String id,
            @RequestParam String currentUserId) {
        log.info("Received delete application request: id={}", id);
        
        applicationService.deleteApplication(id, currentUserId);
        
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }

    /**
     * 恢复应用（管理员权限）
     * POST /api/v1/applications/{id}/restore
     */
    @PostMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<Void>> restoreApplication(
            @PathVariable String id,
            @RequestParam String currentUserId) {
        log.info("Received restore application request: id={}", id);
        
        applicationService.restoreApplication(id, currentUserId);
        
        return ResponseEntity.ok(ApiResponse.success("恢复成功", null));
    }

    /**
     * 变更应用状态（管理员权限）
     * PATCH /api/v1/applications/{id}/status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> changeStatus(
            @PathVariable String id,
            @Valid @RequestBody ChangeStatusRequest request,
            @RequestParam String currentUserId) {
        log.info("Received change status request: id={}, targetStatus={}", id, request.getStatus());
        
        applicationService.changeStatus(id, request, currentUserId);
        
        return ResponseEntity.ok(ApiResponse.success("状态变更成功", null));
    }
}
