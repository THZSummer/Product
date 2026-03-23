package com.openapp.application.controller;

import com.openapp.application.domain.enums.AppStatus;
import com.openapp.application.dto.request.ChangeStatusRequest;
import com.openapp.application.dto.request.CreateApplicationRequest;
import com.openapp.application.dto.request.UpdateApplicationRequest;
import com.openapp.application.dto.response.ApplicationDetailResponse;
import com.openapp.application.dto.response.CreateApplicationResponse;
import com.openapp.application.dto.response.UpdateApplicationResponse;
import com.openapp.application.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<CreateApplicationResponse> createApplication(
            @Valid @RequestBody CreateApplicationRequest request,
            @RequestParam String currentUserId) {
        log.info("Received create application request: name={}", request.getName());
        
        CreateApplicationResponse response = applicationService.createApplication(request, currentUserId);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 获取应用详情
     * GET /api/v1/applications/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApplicationDetailResponse> getApplication(
            @PathVariable String id,
            @RequestParam String currentUserId) {
        log.debug("Received get application request: id={}", id);
        
        ApplicationDetailResponse response = applicationService.getApplication(id, currentUserId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取应用列表（支持分页、筛选）
     * GET /api/v1/applications
     */
    @GetMapping
    public ResponseEntity<Page<ApplicationDetailResponse>> listApplications(
            @RequestParam(required = false) String ownerId,
            @RequestParam(required = false) AppStatus status,
            @PageableDefault(page = 0, size = 20) Pageable pageable,
            @RequestParam String currentUserId) {
        log.debug("Received list applications request: ownerId={}, status={}, page={}, size={}", 
            ownerId, status, pageable.getPageNumber(), pageable.getPageSize());
        
        Page<ApplicationDetailResponse> page = applicationService.listApplications(ownerId, status, pageable, currentUserId);
        
        return ResponseEntity.ok(page);
    }

    /**
     * 更新应用
     * PUT /api/v1/applications/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<UpdateApplicationResponse> updateApplication(
            @PathVariable String id,
            @Valid @RequestBody UpdateApplicationRequest request,
            @RequestParam String currentUserId) {
        log.info("Received update application request: id={}", id);
        
        UpdateApplicationResponse response = applicationService.updateApplication(id, request, currentUserId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 删除应用（软删除）
     * DELETE /api/v1/applications/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(
            @PathVariable String id,
            @RequestParam String currentUserId) {
        log.info("Received delete application request: id={}", id);
        
        applicationService.deleteApplication(id, currentUserId);
        
        return ResponseEntity.noContent().build();
    }

    /**
     * 恢复应用（管理员权限）
     * POST /api/v1/applications/{id}/restore
     */
    @PostMapping("/{id}/restore")
    public ResponseEntity<Void> restoreApplication(
            @PathVariable String id,
            @RequestParam String currentUserId) {
        log.info("Received restore application request: id={}", id);
        
        applicationService.restoreApplication(id, currentUserId);
        
        return ResponseEntity.ok().build();
    }

    /**
     * 变更应用状态（管理员权限）
     * PATCH /api/v1/applications/{id}/status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> changeStatus(
            @PathVariable String id,
            @Valid @RequestBody ChangeStatusRequest request,
            @RequestParam String currentUserId) {
        log.info("Received change status request: id={}, targetStatus={}", id, request.getStatus());
        
        applicationService.changeStatus(id, request, currentUserId);
        
        return ResponseEntity.ok().build();
    }
}
