# Technical Plan: 应用管理系统

## 元数据

| 字段 | 值 |
|------|------|
| **标识符** | `APP-MGT-001` |
| **名称** | 应用管理系统 |
| **版本** | 1.0.0 |
| **创建日期** | 2026-03-23 |
| **作者** | SDD Planning Agent |
| **状态** | `proposed` → `planned` |
| **前置依赖** | spec.md (✅ 已完成) |

---

## 1. 架构概述

### 1.1 系统定位

```
┌─────────────────────────────────────────────────────────────────┐
│                        open-app 平台                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐         │
│  │  API Gateway │───▶│ permission-  │───▶│  应用管理    │         │
│  │             │    │    app      │    │   模块      │         │
│  └─────────────┘    └─────────────┘    └─────────────┘         │
│         ▲                   │                    │              │
│         │                   ▼                    ▼              │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐         │
│  │  外部应用    │    │  认证/授权   │    │   数据库     │         │
│  └─────────────┘    └─────────────┘    └─────────────┘         │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

**应用管理模块** 是 open-app 平台的核心基础设施：
- **上游依赖**: permission-app (认证授权)
- **下游被依赖**: API Gateway、事件开放模块、回调开放模块、机器人开放模块

### 1.2 核心组件

| 组件 | 职责 | 技术选型 |
|------|------|----------|
| **ApplicationController** | API 路由处理 | SpringBoot 3.4.15 |
| **ApplicationService** | 应用业务逻辑 | Spring Service |
| **ApplicationMapper** | 数据访问层 | MyBatis-Plus |
| **ApplicationValidator** | 输入验证 | Hibernate Validator |
| **AuditLogger** | 审计日志记录 | Spring AOP + 数据库 |
| **CacheManager** | 缓存管理 | Spring Cache + Redis |

### 1.3 与 permission-app 集成

```
┌──────────────────────────────────────────────────────────────┐
│                    请求流程                                    │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│  1. 客户端请求                                                │
│         │                                                    │
│         ▼                                                    │
│  2. API Gateway (路由 + 限流)                                  │
│         │                                                    │
│         ▼                                                    │
│  3. permission-app 中间件 (JWT 验证)                            │
│         │                                                    │
│         ▼                                                    │
│  4. 应用管理模块 (SpringBoot)                                  │
│     - JWT 解析用户身份                                         │
│     - 业务逻辑处理                                             │
│     - 数据持久化                                              │
│         │                                                    │
│         ▼                                                    │
│  5. 数据库 (MySQL)                                            │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

**集成点**:
- permission-app 提供 JWT 令牌生成和验证
- 应用管理模块从 JWT 中获取 `userId` 和 `roles`
- 权限校验规则：
  - 普通用户：只能管理自己的应用 (`owner_id === userId`)
  - 管理员：可以管理所有应用 (`roles.includes('admin')`)

---

## 2. 数据模型设计

### 2.1 核心表结构

```sql
-- 应用表
CREATE TABLE applications (
    id              VARCHAR(64) PRIMARY KEY COMMENT '应用 ID',
    name            VARCHAR(128) NOT NULL COMMENT '应用名称',
    description     TEXT COMMENT '应用描述',
    icon_url        VARCHAR(512) COMMENT '应用图标 URL',
    type            VARCHAR(32) NOT NULL COMMENT '应用类型',
    status          VARCHAR(16) NOT NULL DEFAULT 'draft' COMMENT '应用状态',
    owner_id        VARCHAR(64) NOT NULL COMMENT '所有者 ID',
    owner_type      VARCHAR(16) NOT NULL COMMENT '所有者类型',
    callback_url    VARCHAR(512) COMMENT '回调 URL',
    
    -- 软删除字段
    deleted_at      TIMESTAMP NULL COMMENT '删除时间',
    deleted_by      VARCHAR(64) NULL COMMENT '删除人',
    
    -- 审计字段
    version         INTEGER NOT NULL DEFAULT 1 COMMENT '版本号 (乐观锁)',
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    created_by      VARCHAR(64) NOT NULL COMMENT '创建人',
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    updated_by      VARCHAR(64) NOT NULL COMMENT '更新人',
    
    INDEX idx_owner_id (owner_id) COMMENT '所有者索引',
    INDEX idx_status (status) COMMENT '状态索引',
    INDEX idx_deleted_at (deleted_at) COMMENT '删除时间索引',
    INDEX idx_owner_status (owner_id, status) COMMENT '所有者 + 状态联合索引',
    UNIQUE INDEX uk_owner_name (owner_id, name) COMMENT '所有者 + 名称唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用表';

-- 应用状态变更审计表
CREATE TABLE application_status_logs (
    id              VARCHAR(64) PRIMARY KEY COMMENT '日志 ID',
    application_id  VARCHAR(64) NOT NULL COMMENT '应用 ID',
    old_status      VARCHAR(16) NOT NULL COMMENT '旧状态',
    new_status      VARCHAR(16) NOT NULL COMMENT '新状态',
    reason          VARCHAR(512) COMMENT '变更原因',
    changed_by      VARCHAR(64) NOT NULL COMMENT '变更人',
    changed_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '变更时间',
    
    INDEX idx_app_id (application_id) COMMENT '应用 ID 索引',
    INDEX idx_changed_at (changed_at) COMMENT '变更时间索引',
    CONSTRAINT fk_status_log_application FOREIGN KEY (application_id) REFERENCES applications(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用状态变更日志表';
```

### 2.2 MyBatis-Plus Entity

```java
// Application.java
package com.openapp.application.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("applications")
public class Application {
    
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    
    private String name;
    
    private String description;
    
    @TableField("icon_url")
    private String iconUrl;
    
    @TableField(fill = FieldFill.INSERT)
    private String type;
    
    @TableField(fill = FieldFill.INSERT)
    private String status;
    
    @TableField("owner_id")
    private String ownerId;
    
    @TableField("owner_type")
    private String ownerType;
    
    @TableField("callback_url")
    private String callbackUrl;
    
    @TableField(fill = FieldFill.INSERT)
    private String createdBy;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.UPDATE)
    private String updatedBy;
    
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updatedAt;
    
    @TableField(fill = FieldFill.INSERT)
    private String deletedBy;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime deletedAt;
    
    @Version
    private Integer version;
}
```

### 2.3 枚举定义

```java
// AppType.java
package com.openapp.application.domain.enums;

public enum AppType {
    SELF_BUILD("self_build", "自建应用"),
    THIRD_PARTY("third_party", "第三方应用"),
    PERSONAL("personal", "个人应用");
    
    private final String code;
    private final String desc;
    
    AppType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public String getCode() { return code; }
    public String getDesc() { return desc; }
}

// AppStatus.java
package com.openapp.application.domain.enums;

public enum AppStatus {
    DRAFT("draft", "草稿"),
    ACTIVE("active", "已启用"),
    DISABLED("disabled", "已禁用"),
    DELETED("deleted", "已删除");
    
    private final String code;
    private final String desc;
    
    AppStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public String getCode() { return code; }
    public String getDesc() { return desc; }
}
```

### 2.4 数据分区策略

**v1.0 暂不需要分区**，原因：
- 预期单开发者应用数量 < 100
- 总应用数量 < 100,000
- 查询主要通过 `owner_id` 索引

**v2.0 考虑分区的触发条件**:
- 单表数据量 > 1000 万行
- 查询性能下降到 P95 > 200ms

---

## 3. API 设计详情

### 3.1 API 规范

- **Base URL**: `/api/v1/applications`
- **认证**: Bearer Token (JWT)
- **内容类型**: `application/json`
- **响应格式**: 统一响应包装

### 3.2 请求/响应 DTO

```java
// CreateApplicationRequest.java
package com.openapp.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateApplicationRequest {
    
    @NotBlank(message = "应用名称不能为空")
    @Size(max = 64, message = "应用名称不能超过 64 个字符")
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5_-]+$", 
             message = "应用名称只能包含中文、字母、数字、下划线和连字符")
    private String name;
    
    @Size(max = 512, message = "描述不能超过 512 个字符")
    private String description;
    
    @NotBlank(message = "应用类型不能为空")
    private String type;
    
    @Pattern(regexp = "^https?://", message = "请输入有效的 URL")
    @Size(max = 512, message = "URL 不能超过 512 个字符")
    private String iconUrl;
    
    @Pattern(regexp = "^https?://", message = "请输入有效的 URL")
    @Size(max = 512, message = "URL 不能超过 512 个字符")
    private String callbackUrl;
}

// CreateApplicationResponse.java
package com.openapp.application.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateApplicationResponse {
    private String id;
    private String name;
    private String type;
    private String status;
    private String ownerId;
    private String createdAt;
}

// UpdateApplicationRequest.java
package com.openapp.application.dto.request;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateApplicationRequest {
    
    @Size(max = 64, message = "应用名称不能超过 64 个字符")
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5_-]+$", 
             message = "应用名称只能包含中文、字母、数字、下划线和连字符")
    private String name;
    
    @Size(max = 512, message = "描述不能超过 512 个字符")
    private String description;
    
    @Pattern(regexp = "^https?://", message = "请输入有效的 URL")
    @Size(max = 512, message = "URL 不能超过 512 个字符")
    private String iconUrl;
    
    @Pattern(regexp = "^https?://", message = "请输入有效的 URL")
    @Size(max = 512, message = "URL 不能超过 512 个字符")
    private String callbackUrl;
}

// ApplicationListResponse.java
package com.openapp.application.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ApplicationListResponse {
    private List<ApplicationListItem> data;
    private Long total;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
    
    @Data
    @Builder
    public static class ApplicationListItem {
        private String id;
        private String name;
        private String type;
        private String status;
        private String iconUrl;
        private String createdAt;
        private String updatedAt;
    }
}

// ApplicationDetailResponse.java
package com.openapp.application.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApplicationDetailResponse {
    private String id;
    private String name;
    private String description;
    private String type;
    private String status;
    private String iconUrl;
    private String callbackUrl;
    private String ownerId;
    private String ownerType;
    private String createdAt;
    private String updatedAt;
}

// ChangeStatusRequest.java
package com.openapp.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangeStatusRequest {
    
    @NotBlank(message = "目标状态不能为空")
    private String status;
    
    private String reason;
}

// ErrorResponse.java
package com.openapp.application.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
@Builder
public class ErrorResponse {
    private String code;
    private String message;
    private Map<String, Object> details;
}
```

### 3.3 错误码定义

```java
// ApplicationErrorCode.java
package com.openapp.application.exception;

import lombok.Getter;

@Getter
public enum ApplicationErrorCode {
    
    // 4xx 客户端错误
    APP_NOT_FOUND("APP_NOT_FOUND", 404, "应用不存在或已删除"),
    ACCESS_DENIED("ACCESS_DENIED", 403, "无权访问此应用"),
    APP_NAME_DUPLICATE("APP_NAME_DUPLICATE", 409, "应用名称已存在，请使用其他名称"),
    INVALID_STATUS_TRANSITION("INVALID_STATUS_TRANSITION", 400, "不允许的状态转换"),
    APP_DELETED("APP_DELETED", 410, "应用已删除，请先恢复应用"),
    VALIDATION_ERROR("VALIDATION_ERROR", 400, "请求参数验证失败"),
    RATE_LIMIT_EXCEEDED("RATE_LIMIT_EXCEEDED", 429, "请求频率超限，请稍后重试"),
    
    // 5xx 服务端错误
    INTERNAL_ERROR("INTERNAL_ERROR", 500, "服务器内部错误"),
    DATABASE_ERROR("DATABASE_ERROR", 500, "数据库操作失败"),
    OPTIMISTIC_LOCK_ERROR("OPTIMISTIC_LOCK_ERROR", 409, "数据已被修改，请刷新后重试");
    
    private final String code;
    private final int httpStatus;
    private final String message;
    
    ApplicationErrorCode(String code, int httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
```

### 3.4 状态转换规则

```java
// StatusTransitions.java
package com.openapp.application.domain;

import com.openapp.application.domain.enums.AppStatus;
import java.util.*;

public class StatusTransitions {
    
    private static final Map<AppStatus, Set<AppStatus>> STATUS_TRANSITIONS = new EnumMap<>(AppStatus.class);
    
    static {
        STATUS_TRANSITIONS.put(AppStatus.DRAFT, EnumSet.of(AppStatus.ACTIVE, AppStatus.DELETED));
        STATUS_TRANSITIONS.put(AppStatus.ACTIVE, EnumSet.of(AppStatus.DISABLED, AppStatus.DELETED));
        STATUS_TRANSITIONS.put(AppStatus.DISABLED, EnumSet.of(AppStatus.ACTIVE, AppStatus.DELETED));
        STATUS_TRANSITIONS.put(AppStatus.DELETED, EnumSet.noneOf(AppStatus.class));
    }
    
    public static boolean isValidTransition(AppStatus fromStatus, AppStatus toStatus) {
        return STATUS_TRANSITIONS.get(fromStatus).contains(toStatus);
    }
    
    public static Set<AppStatus> getAvailableTransitions(AppStatus status) {
        return Collections.unmodifiableSet(STATUS_TRANSITIONS.get(status));
    }
}
```

### 3.5 限流配置

| API | 限流策略 | 说明 |
|-----|----------|------|
| POST /applications | 10 次/分钟/用户 | 创建应用是低频操作 |
| GET /applications | 60 次/分钟/用户 | 列表查询较频繁 |
| GET /applications/:id | 120 次/分钟/用户 | 详情查询高频 |
| PUT /applications/:id | 30 次/分钟/用户 | 更新操作中频 |
| DELETE /applications/:id | 10 次/分钟/用户 | 删除操作低频 |
| PATCH /applications/:id/status | 20 次/分钟/管理员 | 状态变更需审计 |

---

## 4. 集成点

### 4.1 permission-app 集成

**认证流程**:
```java
// JwtAuthFilter.java
package com.openapp.application.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    
    private final JwtTokenProvider jwtTokenProvider;
    
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        
        String token = extractBearerToken(request);
        
        if (token == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "缺少认证令牌");
            return;
        }
        
        try {
            String userId = jwtTokenProvider.getUserId(token);
            List<String> roles = jwtTokenProvider.getRoles(token);
            
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(
                    userId, 
                    null, 
                    Collections.emptyList()
                );
            
            authentication.getDetails().put("roles", roles);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "认证令牌无效或已过期");
            return;
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String extractBearerToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return null;
    }
}

// ApplicationAuthAspect.java
package com.openapp.application.security;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.List;

@Aspect
@Component
public class ApplicationAuthAspect {
    
    @Before("@annotation(RequireOwnerOrAdmin)")
    public void checkOwnerOrAdmin() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId = (String) authentication.getPrincipal();
        List<String> roles = (List<String>) authentication.getDetails();
        
        // 管理员跳过所有者检查
        if (roles.contains("admin") || roles.contains("super_admin")) {
            return;
        }
        
        // 非管理员需要检查是否为应用所有者（在 Service 层实现）
    }
}
```

### 4.2 数据库层

```java
// ApplicationMapper.java
package com.openapp.application.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.openapp.application.domain.Application;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ApplicationMapper extends BaseMapper<Application> {
    
    @Select("SELECT * FROM applications WHERE id = #{id} AND deleted_at IS NULL")
    Application findById(String id);
    
    @Select("SELECT * FROM applications WHERE owner_id = #{ownerId} AND deleted_at IS NULL")
    List<Application> findByOwnerId(String ownerId);
}

// ApplicationRepository.java
package com.openapp.application.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.openapp.application.domain.Application;
import com.openapp.application.domain.enums.AppStatus;
import com.openapp.application.domain.enums.AppType;
import com.openapp.application.mapper.ApplicationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ApplicationRepository {
    
    private final ApplicationMapper applicationMapper;
    
    public Application create(Application application) {
        applicationMapper.insert(application);
        return application;
    }
    
    public Application findById(String id) {
        return applicationMapper.findById(id);
    }
    
    public Page<Application> findByOwner(
        String ownerId, 
        int page, 
        int pageSize, 
        AppStatus status, 
        AppType type
    ) {
        QueryWrapper<Application> wrapper = new QueryWrapper<>();
        wrapper.eq("owner_id", ownerId);
        wrapper.isNull("deleted_at");
        
        if (status != null) {
            wrapper.eq("status", status.getCode());
        }
        if (type != null) {
            wrapper.eq("type", type.getCode());
        }
        
        wrapper.orderByDesc("created_at");
        
        return applicationMapper.selectPage(
            new Page<>(page, pageSize), 
            wrapper
        );
    }
    
    public Application update(Application application, Integer version) {
        application.setVersion(version + 1);
        applicationMapper.updateById(application);
        return application;
    }
    
    public void softDelete(String id, String deletedBy) {
        Application application = new Application();
        application.setId(id);
        application.setStatus(AppStatus.DELETED.getCode());
        application.setDeletedAt(java.time.LocalDateTime.now());
        application.setDeletedBy(deletedBy);
        applicationMapper.updateById(application);
    }
    
    public void restore(String id) {
        Application application = new Application();
        application.setId(id);
        application.setStatus(AppStatus.DISABLED.getCode());
        application.setDeletedAt(null);
        application.setDeletedBy(null);
        applicationMapper.updateById(application);
    }
}
```

### 4.3 缓存策略

```java
// CacheConfig.java
package com.openapp.application.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(5));
        
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(defaultConfig)
            .withCacheConfiguration("applicationDetail", 
                RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(5)))
            .withCacheConfiguration("applicationList", 
                RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(1)))
            .build();
    }
}

// ApplicationCacheService.java
package com.openapp.application.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.openapp.application.domain.Application;

@Service
@RequiredArgsConstructor
public class ApplicationCacheService {
    
    @Cacheable(value = "applicationDetail", key = "#appId")
    public Application getApplication(String appId) {
        // 实际数据从 Service 获取，这里只是缓存注解
        return null;
    }
    
    @CacheEvict(value = "applicationDetail", key = "#appId")
    public void evictApplicationCache(String appId) {
        // 缓存失效
    }
    
    @CacheEvict(value = "applicationList", allEntries = true)
    public void evictListCache() {
        // 列表缓存失效
    }
}
```

---

## 5. 技术决策

### 5.1 技术栈选择

| 层级 | 技术选型 | 版本 | 理由 |
|------|----------|------|------|
| **前端运行时** | Node.js | 20 LTS | 团队熟悉，生态丰富 |
| **后端框架** | SpringBoot | 3.4.15 | Java 生态标准，企业级支持 |
| **语言** | Java | 21 | LTS 版本，虚拟线程等新特性 |
| **ORM** | MyBatis | 3.0.4 | SQL 可控，性能优秀 |
| **验证** | Hibernate Validator | 8.x | Jakarta EE 标准 |
| **缓存** | Redis | 6.0 | 高性能，支持多种数据结构 |
| **数据库** | MySQL | 5.7 | 团队熟悉，运维成熟 |
| **日志** | Logback | 1.4.x | SpringBoot 默认 |

### 5.2 项目模块结构

#### 5.2.1 整体模块结构设计
根据技术架构需求，项目将分为以下几个核心模块：

```
open-app/
├── common/                             # 通用基础包
│   ├── auth-core/                      # 认证基础组件
│   ├── constants/                      # 全局常量
│   ├── exceptions/                     # 全局异常处理
│   ├── utils/                          # 通用工具
│   └── validation/                     # 通用验证组件
├── applications/                       # 认证授权服务
│   ├── permission-server/              # 权限服务 (已存在)
│   ├── member-server/                  # 用户会员服务 (APP-MEM-001)
│   └── app-mgt-server/                 # 应用管理服务 (此模块 - APP-MGT-001)
├── gateway/                           # API网关
├── infra/                             # 基础设施
│   └── mysql-container/                # MySQL容器配置
│   └── redis-container/                # Redis容器配置
└── pom.xml                            # 聚合pom
```

### 5.3 应用管理模块目录结构

#### 5.3.1 模块在 open-app 中的位置

```
open-app/                              # 项目根目录
├── pom.xml                            # 父 POM (多模块配置)
├── application-management/            # 应用管理模块 ⭐
│   ├── pom.xml                       # 模块 POM 配置
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/openapp/application/
│   │   │   │   ├── ApplicationApplication.java     # SpringBoot 启动类
│   │   │   │   ├── controller/                     # 控制器层 (REST API)
│   │   │   │   ├── service/                        # 服务层 (业务逻辑)
│   │   │   │   ├── repository/                     # 数据访问层
│   │   │   │   ├── domain/                         # 领域层 (实体/枚举)
│   │   │   │   ├── dto/                            # 数据传输对象
│   │   │   │   ├── mapper/                         # MyBatis Mapper
│   │   │   │   ├── security/                       # 安全认证
│   │   │   │   ├── cache/                          # 缓存处理
│   │   │   │   ├── config/                         # 配置类
│   │   │   │   └── exception/                      # 异常处理
│   │   │   └── resources/
│   │   │       ├── application.yml                # 应用配置
│   │   │       ├── application-dev.yml            # 开发环境配置
│   │   │       ├── application-prod.yml           # 生产环境配置
│   │   │       └── db/migration/                  # 数据库迁移脚本
│   │   │           ├── V1__init_applications.sql
│   │   │           └── V2__init_application_status_logs.sql
│   │   └── test/                                   # 测试代码
│   │       ├── java/com/openapp/application/
│   │       │   ├── controller/                    # 控制器测试
│   │       │   ├── service/                       # 服务测试
│   │       │   └── integration/                   # 集成测试
│   │       └── resources/                         # 测试资源
│   │           └── application-test.yml
│   └── README.md                          # 模块说明文档
│
├── permission-management/               # 权限管理模块 (未来)
├── api-gateway/                        # API 网关模块 (未来)
└── docs/                               # 文档目录
```

#### 5.3.2 分层架构说明

| 层级 | 包路径 | 职责 | 示例类 |
|------|--------|------|--------|
| **控制器层** | `controller` | 处理 HTTP 请求，参数验证 | `ApplicationController` |
| **服务层** | `service` | 业务逻辑编排，事务管理 | `ApplicationService`, `ApplicationServiceImpl` |
| **数据访问层** | `repository` | 数据库 CRUD 操作 | `ApplicationRepository` |
| **领域层** | `domain` | 领域模型，业务规则 | `Application`, `AppType`, `AppStatus` |
| **DTO 层** | `dto` | 数据传输对象 | `CreateApplicationRequest`, `ApplicationDetailResponse` |
| **Mapper 层** | `mapper` | MyBatis ORM 映射 | `ApplicationMapper`, `ApplicationMapper.xml` |
| **安全层** | `security` | 认证授权，JWT 处理 | `JwtAuthFilter`, `JwtTokenProvider` |
| **缓存层** | `cache` | Redis 缓存管理 | `ApplicationCacheService` |
| **配置层** | `config` | Spring 配置类 | `CacheConfig`, `SecurityConfig` |
| **异常层** | `exception` | 异常定义与处理 | `ApplicationErrorCode`, `GlobalExceptionHandler` |

#### 5.3.3 核心类说明

**启动类**:
```java
// ApplicationApplication.java
package com.openapp.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApplicationApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationApplication.class, args);
    }
}
```

**控制器层**:
```java
// ApplicationController.java
package com.openapp.application.controller;

import com.openapp.application.dto.request.CreateApplicationRequest;
import com.openapp.application.dto.response.ApplicationDetailResponse;
import com.openapp.application.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {
    
    private final ApplicationService applicationService;
    
    @PostMapping
    public ResponseEntity<ApplicationDetailResponse> createApplication(
        @Valid @RequestBody CreateApplicationRequest request) {
        // ...
    }
}
```

**领域层**:
```java
// Application.java (实体类)
package com.openapp.application.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Application {
    private String id;
    private String name;
    private String description;
    private String iconUrl;
    private AppType type;
    private AppStatus status;
    private String ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer version;
}
```

**服务层**:
```java
// ApplicationService.java (接口)
package com.openapp.application.service;

import com.openapp.application.domain.Application;
import com.openapp.application.dto.request.CreateApplicationRequest;
import java.util.List;

public interface ApplicationService {
    Application createApplication(CreateApplicationRequest request, String ownerId);
    Application getApplication(String appId);
    List<Application> listApplications(String ownerId, int page, int pageSize);
    void updateApplication(String appId, CreateApplicationRequest request);
    void deleteApplication(String appId, String operatorId);
}
```

**数据访问层**:
```java
// ApplicationRepository.java
package com.openapp.application.repository;

import com.openapp.application.domain.Application;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ApplicationRepository {
    
    int insert(Application application);
    
    Application findById(@Param("id") String id);
    
    List<Application> findByOwnerId(
        @Param("ownerId") String ownerId,
        @Param("offset") int offset,
        @Param("limit") int limit
    );
    
    int update(Application application);
    
    int delete(@Param("id") String id);
}
```

#### 5.3.4 配置文件说明

**主配置文件** (`application.yml`):
```yaml
spring:
  application:
    name: application-management
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}

server:
  port: ${SERVER_PORT:8080}

# 数据库配置
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/open_app?useSSL=false&serverTimezone=UTC
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver

# MyBatis 配置
mybatis:
  mapper-locations: classpath:mapper/**/*.xml
  type-aliases-package: com.openapp.application.domain
  configuration:
    map-underscore-to-camel-case: true
    cache-enabled: true

# Redis 配置
spring:
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
```

**开发环境配置** (`application-dev.yml`):
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/open_app_dev?useSSL=false
    username: root
    password: root123
  
  data:
    redis:
      host: localhost
      port: 6379

logging:
  level:
    com.openapp.application: DEBUG
    org.apache.ibatis: DEBUG
```

**生产环境配置** (`application-prod.yml`):
```yaml
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST}:${DB_PORT}/open_app?useSSL=true
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 10
  
  data:
    redis:
      host: ${REDIS_HOST}
      port: ${REDIS_PORT}

logging:
  level:
    com.openapp.application: INFO
    org.apache.ibatis: WARN
```

#### 5.3.5 测试目录结构

```
src/test/
├── java/com/openapp/application/
│   ├── controller/
│   │   └── ApplicationControllerTest.java        # 控制器单元测试
│   ├── service/
│   │   └── ApplicationServiceTest.java           # 服务层单元测试
│   ├── repository/
│   │   └── ApplicationRepositoryTest.java        # 数据访问层测试
│   ├── domain/
│   │   └── ApplicationTest.java                  # 领域实体测试
│   └── integration/
│       └── ApplicationIntegrationTest.java       # 集成测试
└── resources/
    ├── application-test.yml                      # 测试配置
    └── data/
        └── init-test-data.sql                    # 测试数据初始化
```

**测试配置示例** (`application-test.yml`):
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  
  data:
    redis:
      host: localhost
      port: 6379

mybatis:
  configuration:
    map-underscore-to-camel-case: true

logging:
  level:
    com.openapp.application: DEBUG
```
app-mgt-server/
├── pom.xml                            # 模块独立配置
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/openapp/application/
│   │   │       ├── ApplicationCoreApplication.java    # 启动类
│   │   │       ├── controller/                         # 控制层
│   │   │       │   ├── ApplicationController.java     # 应用管理控制器
│   │   │       │   └── StatusLogController.java       # 状态日志控制器
│   │   │       ├── service/                           # 业务逻辑层
│   │   │       │   ├── ApplicationService.java        # 应用服务接口
│   │   │       │   ├── impl/
│   │   │       │   │   └── ApplicationServiceImpl.java # 应用服务实现
│   │   │       │   └── StatusLogService.java          # 状态日志服务
│   │   │       ├── repository/                        # 数据访问层
│   │   │       │   ├── ApplicationRepository.java     # 应用仓储
│   │   │       │   └── StatusLogRepository.java       # 状态日志仓储
│   │   │       ├── infrastructure/                    # 基础设施层
│   │   │       │   ├── persistence/                   # 持久化相关
│   │   │       │   │   ├── mapper/
│   │   │       │   │   │   ├── ApplicationMapper.java # 应用Mapper
│   │   │       │   │   │   ├── StatusLogMapper.java   # 状态日志Mapper
│   │   │       │   │   │   └── ApplicationMapper.xml   # XML映射文件
│   │   │       │   │   ├── entity/                    # 持久化实体
│   │   │       │   │   │   ├── ApplicationDO.java     # 应用数据对象
│   │   │       │   │   │   └── StatusLogDO.java       # 状态日志数据对象
│   │   │       │   ├── cache/                         # 缓存实现
│   │   │       │   │   ├── ApplicationCacheAdapter.java # 应用缓存适配器
│   │   │       │   │   └── annotation/                # 缓存注解
│   │   │       │   │       └── CacheLock.java         # 缓存锁注解
│   │   │       │   ├── gateway/                       # 服务网关
│   │   │       │   │   ├── PermissionGatewayInterface.java # 权限网关
│   │   │       │   │   └── impl/
│   │   │       │   │       └── PermissionGatewayImpl.java # 权限网关实现
│   │   │       ├── domain/                            # 领域层
│   │   │       │   ├── model/                         # 领域模型
│   │   │       │   │   ├── ApplicationEntity.java     # 应用领域实体
│   │   │       │   │   ├── StatusChange.java          # 状态变更对象
│   │   │       │   │   ├── ValueObject/               # 值对象
│   │   │       │   │   │   ├── ApplicationName.java   # 应用名称值对象
│   │   │       │   │   │   └── CallbackUrl.java       # 回调URL值对象
│   │   │       │   ├── service/                       # 领域服务
│   │   │       │   │   └── DomainAuthService.java     # 领域认证服务
│   │   │       │   ├── factory/                       # 领域工厂
│   │   │       │   │   └── ApplicationFactory.java    # 应用工厂
│   │   │       │   ├── repository/                    # 领域仓储接口
│   │   │       │   │   └── ApplicationDomainRepository.java # 领域仓储接口
│   │   │       │   └── event/                         # 领域事件
│   │   │       │       ├── ApplicationCreatedEvent.java # 应用创建事件
│   │   │       │       └── StatusChangedEvent.java    # 状态变更事件
│   │   │       ├── application/                       # 应用服务层
│   │   │       │   ├── command/                       # 命令对象
│   │   │       │   │   ├── CreateApplicationCmd.java  # 创建应用命令
│   │   │       │   │   ├── UpdateApplicationCmd.java  # 更新应用命令
│   │   │       │   │   └── ChangeStatusCmd.java       # 变更状态命令
│   │   │       │   ├── dto/                           # 数据传输对象
│   │   │       │   │   ├── request/                   # 请求DTOs
│   │   │       │   │   │   ├── CreateApplicationRequest.java
│   │   │       │   │   │   ├── UpdateApplicationRequest.java
│   │   │       │   │   │   ├── SearchApplicationRequest.java
│   │   │       │   │   │   └── ChangeStatusRequest.java
│   │   │       │   │   ├── response/                  # 响应DTOs
│   │   │       │   │   │   ├── ApplicationDetailResponse.java
│   │   │       │   │   │   ├── ApplicationListResponse.java
│   │   │       │   │   │   └── CreateApplicationResponse.java
│   │   │       │   ├── service/                       # 应用服务
│   │   │       │   │   ├── ApplicationQueryService.java # 应用查询服务
│   │   │       │   │   └── ApplicationCommandService.java # 应用命令服务
│   │   │       ├── crosscutting/                      # 横切关注点
│   │   │       │   ├── security/                      # 安全组件
│   │   │       │   │   ├── JwtAuthenticationFilter.java # JWT认证过滤器
│   │   │       │   │   ├── SecurityContextHolder.java  # 安全上下文持有者
│   │   │       │   │   └── annotation/
│   │   │       │   │       ├── RequireOwnerOrAdmin.java # 所有者权限注解
│   │   │       │   │       └── Authentication.java      # 认证注解
│   │   │       │   ├── cacheaspect/                   # 缓存切面
│   │   │       │   │   ├── CacheLockAspect.java       # 缓存锁切面
│   │   │       │   │   └── CacheableAspect.java       # 可缓存切面
│   │   │       │   ├── exceptionhandlers/             # 异常处理器
│   │   │       │   │   └── GlobalExceptionHandler.java   # 全局异常处理器
│   │   │       │   └── logging/                       # 日志组件
│   │   │       │       ├── AuditLogger.java           # 审计日志器
│   │   │       │       ├── OperationLog.java          # 操作日志
│   │   │       │       └── annotation/
│   │   │       │           ├── Audit.java             # 审计注解
│   │   │       │           └── IgnoreAudit.java       # 忽略审计注解
│   │   │       ├── config/                            # 配置类
│   │   │       │   ├── DatabaseConfig.java            # 数据库配置
│   │   │       │   ├── SecurityConfig.java            # 安全配置
│   │   │       │   ├── CacheConfig.java               # 缓存配置
│   │   │       │   ├── MybatisConfig.java             # MyBatis配置
│   │   │       │   ├── EventConfig.java               # 事件配置
│   │   │       │   ├── CorsConfig.java                # CORS配置
│   │   │       │   ├── RateLimitConfig.java           # 限流配置
│   │   │       │   └── ValidatorConfig.java           # 验证器配置
│   │   │       ├── utils/                             # 工具类
│   │   │       │   ├── RequestUtils.java              # 请求工具
│   │   │       │   ├── SecurityUtils.java             # 安全工具
│   │   │       │   └── ValidationUtils.java           # 验证工具
│   │   │       └── constant/                          # 常量类
│   │   │           ├── AppConstants.java              # 应用常量
│   │   │           ├── ErrorCodes.java                # 错误码
│   │   │           └── Permissions.java               # 权限常量
│   │   └── resources/
│   │       ├── application.yml                       # 配置文件
│   │       ├── application-dev.yml                   # 开发配置
│   │       ├── application-prod.yml                  # 生产配置
│   │       ├── application-test.yml                  # 测试配置
│   │       ├── mapper/
│   │       │   └── ApplicationMapper.xml             # MyBatis映射文件
│   │       ├── db/
│   │       │   └── migration/                        # 数据库迁移脚本
│   │       │       ├── V1__init_applications.sql     # 初始化应用表
│   │       │       └── V2__add_status_audit_log.sql  # 添加状态审核日志
│   │       └── i18n/
│   │           ├── messages.properties               # 默认消息资源
│   │           └── messages_en_US.properties         # 英文消息资源
│   └── test/
│       ├── java/
│       │   └── com/openapp/application/
│       │       ├── controller/
│       │       │   ├── ApplicationControllerIntegrationTest.java # 集成测试
│       │       ├── service/
│       │       │   ├── ApplicationServiceTest.java    # 服务单元测试
│       │       ├── domain/
│       │       │   ├── ApplicationEntityTest.java     # 领域实体测试
│       │       │   └── factory/
│       │       │       └── ApplicationFactoryTest.java # 工厂测试
│       │       └── repository/
│       │           └── ApplicationRepositoryTest.java  # 仓储测试
│       ├── resources/
│       │   ├── application-test.yml                 # 测试配置
│       │   └── data/
│       │       └── init-test-data.sql               # 测试数据初始化
│       └── contracts/
│           └── application-contracts.yml            # 契约测试定义
└── Dockerfile                          # Docker构建文件
```

#### 5.3.2 技术栈兼容性保障
- **Java 21**: 采用虚拟线程、Switch Pattern Matching等新特性提升性能
- **SpringBoot 3.4.15**: 支持Jakarta EE 10，全面采用最新的编程模型
- **MyBatis 3.0.4**: 结合MyBatis-Plus增强功能，简化CRUD操作
- **MySQL 5.7**: 兼容性好，满足项目功能需求

### 5.4 安全考虑

| 安全项 | 措施 |
|--------|------|
| **SQL 注入** | MyBatis 参数化查询，统一数据访问层封装 |
| **XSS** | 响应头设置 `Content-Type: application/json` |
| **CSRF** | API 使用 JWT，不需要 CSRF 防护 |
| **输入验证** | Hibernate Validator 严格验证 + 自定义验证器 |
| **敏感数据** | 不在日志中记录敏感信息，使用脱敏注解 |
| **权限控制** | Spring Security + AOP + 自定义认证过滤器三层防护 |
| **并发控制** | 乐观锁机制 + 分布式锁防止并发修改 |
| **接口鉴权** | JWT Token解析 + 权限网关双重验证 |

---

## 6. 性能考虑

### 6.1 缓存策略

| 数据类型 | 缓存策略 | TTL | 失效时机 |
|----------|----------|-----|----------|
| 应用详情 | 缓存 | 5 分钟 | 更新/删除/状态变更 |
| 应用列表 | 缓存 | 1 分钟 | 创建/删除/状态变更 |
| 名称唯一性 | 缓存 | 1 分钟 | 创建/更新名称 |

### 6.2 数据库优化

```sql
-- 慢查询日志
-- 启用 MySQL 慢查询日志，阈值 100ms
slow_query_log = 1
long_query_time = 0.1

-- 查询优化示例
-- 使用覆盖索引减少回表
CREATE INDEX idx_app_list_covering 
  ON applications(owner_id, status, created_at DESC);

-- 定期分析表统计信息
ANALYZE TABLE applications;
```

### 6.3 连接池配置

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/open_app?useSSL=false&serverTimezone=UTC&useUnicode=true&characterEncoding=utf8
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 20000
      idle-timeout: 300000
      max-lifetime: 900000
```

### 6.4 MyBatis 配置

```yaml
# application.yml
mybatis:
  mapper-locations: classpath:mapper/**/*.xml
  type-aliases-package: com.openapp.application.domain
  configuration:
    map-underscore-to-camel-case: true
    cache-enabled: true
    lazy-loading-enabled: false
    aggressive-lazy-loading: false
    log-impl: org.apache.ibatis.logging.slf4j.Slf4jImpl
```

---

## 7. 测试策略

### 7.1 测试金字塔

```
        /\
       /  \
      / E2E \      10% - 关键业务流程
     /------\
    /  集成  \    30% - API 集成测试
   /--------\
  /   单元    \  60% - Service/Mapper 单元测试
 /------------\
```

### 7.2 单元测试

```java
// ApplicationServiceTest.java
package com.openapp.application.service;

import com.openapp.application.domain.Application;
import com.openapp.application.dto.request.CreateApplicationRequest;
import com.openapp.application.exception.ApplicationErrorCode;
import com.openapp.application.exception.ApplicationException;
import com.openapp.application.repository.ApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {
    
    @Mock
    private ApplicationRepository repository;
    
    @InjectMocks
    private ApplicationService service;
    
    @BeforeEach
    void setUp() {
        service = new ApplicationServiceImpl(repository);
    }
    
    @Test
    void shouldCreateApplication() {
        // Given
        CreateApplicationRequest request = new CreateApplicationRequest();
        request.setName("测试应用");
        request.setType("self_build");
        
        Application mockApp = new Application();
        mockApp.setId("app_123");
        mockApp.setName("测试应用");
        mockApp.setStatus("draft");
        
        when(repository.create(any())).thenReturn(mockApp);
        
        // When
        Application result = service.createApplication(request, "user_123");
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("app_123");
        assertThat(result.getStatus()).isEqualTo("draft");
    }
    
    @Test
    void shouldThrowExceptionWhenNameDuplicate() {
        // Given
        CreateApplicationRequest request = new CreateApplicationRequest();
        request.setName("重复名称");
        request.setType("self_build");
        
        when(repository.create(any()))
            .thenThrow(new ApplicationException(ApplicationErrorCode.APP_NAME_DUPLICATE));
        
        // When & Then
        assertThatThrownBy(() -> service.createApplication(request, "user_123"))
            .isInstanceOf(ApplicationException.class)
            .hasFieldOrPropertyWithValue("errorCode", ApplicationErrorCode.APP_NAME_DUPLICATE);
    }
}
```

### 7.3 集成测试

```java
// ApplicationControllerTest.java
package com.openapp.application.controller;

import com.openapp.application.dto.request.CreateApplicationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ApplicationControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void shouldCreateApplication() throws Exception {
        CreateApplicationRequest request = new CreateApplicationRequest();
        request.setName("测试应用");
        request.setType("self_build");
        request.setDescription("测试描述");
        
        mockMvc.perform(post("/api/v1/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + getAuthToken())
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.name").value("测试应用"))
            .andExpect(jsonPath("$.data.status").value("draft"));
    }
    
    @Test
    void shouldReturn401WhenNoToken() throws Exception {
        CreateApplicationRequest request = new CreateApplicationRequest();
        request.setName("测试应用");
        request.setType("self_build");
        
        mockMvc.perform(post("/api/v1/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized());
    }
    
    private String getAuthToken() {
        // 测试环境获取 Token 的方法
        return "test_token";
    }
}
```

### 7.4 E2E 测试

关键业务流程 E2E 测试：
1. 创建应用 → 查看列表 → 查看详情 → 更新 → 删除
2. 管理员状态变更流程
3. 并发更新冲突处理

---

## 8. 部署考虑

### 8.1 环境配置

```yaml
# application.yml
spring:
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:development}
  
  datasource:
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/open_app?useSSL=false&serverTimezone=UTC
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}

jwt:
  secret: ${JWT_SECRET}
  issuer: ${JWT_ISSUER:open-app}
  expiration: ${JWT_EXPIRATION:3600000}

server:
  port: ${SERVER_PORT:8080}

logging:
  level:
    root: ${LOG_LEVEL:INFO}
    com.openapp.application: ${APP_LOG_LEVEL:DEBUG}
```

### 8.2 数据库迁移

```bash
# 使用 Flyway 进行数据库迁移
# src/main/resources/db/migration/V1__init_applications.sql

# 开发环境
./mvnw flyway:migrate

# 生产环境（自动）
# 应用启动时自动执行
```

### 8.3 回滚计划

**代码回滚**:
1. 保留上一个稳定版本的 Docker 镜像
2. Kubernetes 回滚：`kubectl rollout undo deployment/application-service`

**数据回滚**:
1. Flyway 支持回滚脚本
2. 软删除数据可恢复
3. 定期备份数据库（每日）

---

## 9. 文件影响分析

### 9.1 需要创建的文件

#### 9.1.1 主应用模块文件 (app-mgt-server/src/main/java/com/openapp/application/)
```
[NEW] app-mgt-server/src/main/java/com/openapp/application/ApplicationCoreApplication.java
# 启动类 - 应用管理服务主入口

# 控制器层文件
[NEW] app-mgt-server/src/main/java/com/openapp/application/controller/ApplicationController.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/controller/StatusLogController.java

# 领域层文件
[NEW] app-mgt-server/src/main/java/com/openapp/application/domain/model/ApplicationEntity.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/domain/model/StatusChange.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/domain/model/ValueObject/ApplicationName.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/domain/model/ValueObject/CallbackUrl.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/domain/service/DomainAuthService.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/domain/factory/ApplicationFactory.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/domain/repository/ApplicationDomainRepository.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/domain/event/ApplicationCreatedEvent.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/domain/event/StatusChangedEvent.java

# 应用服务层文件
[NEW] app-mgt-server/src/main/java/com/openapp/application/application/command/CreateApplicationCmd.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/application/command/UpdateApplicationCmd.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/application/command/ChangeStatusCmd.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/application/dto/request/CreateApplicationRequest.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/application/dto/request/UpdateApplicationRequest.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/application/dto/request/SearchApplicationRequest.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/application/dto/request/ChangeStatusRequest.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/application/dto/response/ApplicationDetailResponse.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/application/dto/response/ApplicationListResponse.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/application/dto/response/CreateApplicationResponse.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/application/service/ApplicationQueryService.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/application/service/ApplicationCommandService.java

# 业务服务层文件
[NEW] app-mgt-server/src/main/java/com/openapp/application/service/ApplicationService.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/service/impl/ApplicationServiceImpl.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/service/StatusLogService.java

# 基础设施层 - 持久化相关
[NEW] app-mgt-server/src/main/java/com/openapp/application/infrastructure/persistence/entity/ApplicationDO.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/infrastructure/persistence/entity/StatusLogDO.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/infrastructure/persistence/mapper/ApplicationMapper.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/infrastructure/persistence/mapper/StatusLogMapper.java
[NEW] app-mgt-server/src/main/resources/mapper/ApplicationMapper.xml

# 基础设施层 - 缓存相关
[NEW] app-mgt-server/src/main/java/com/openapp/application/infrastructure/cache/ApplicationCacheAdapter.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/infrastructure/cache/annotation/CacheLock.java

# 基础设施层 - 网关接口
[NEW] app-mgt-server/src/main/java/com/openapp/application/infrastructure/gateway/PermissionGatewayInterface.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/infrastructure/gateway/impl/PermissionGatewayImpl.java

# 基础设施层 - 仓储实现
[NEW] app-mgt-server/src/main/java/com/openapp/application/repository/ApplicationRepository.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/repository/StatusLogRepository.java

# 横切关注点 - 安全组件
[NEW] app-mgt-server/src/main/java/com/openapp/application/crosscutting/security/JwtAuthenticationFilter.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/crosscutting/security/SecurityContextHolder.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/crosscutting/security/annotation/RequireOwnerOrAdmin.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/crosscutting/security/annotation/Authentication.java

# 横切关注点 - 缓存切面
[NEW] app-mgt-server/src/main/java/com/openapp/application/crosscutting/cacheaspect/CacheLockAspect.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/crosscutting/cacheaspect/CacheableAspect.java

# 横切关注点 - 异常处理器
[NEW] app-mgt-server/src/main/java/com/openapp/application/crosscutting/exceptionhandlers/GlobalExceptionHandler.java

# 横切关注点 - 日志组件
[NEW] app-mgt-server/src/main/java/com/openapp/application/crosscutting/logging/AuditLogger.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/crosscutting/logging/OperationLog.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/crosscutting/logging/annotation/Audit.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/crosscutting/logging/annotation/IgnoreAudit.java

# 配置类
[NEW] app-mgt-server/src/main/java/com/openapp/application/config/DatabaseConfig.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/config/SecurityConfig.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/config/CacheConfig.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/config/MybatisConfig.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/config/EventConfig.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/config/CorsConfig.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/config/RateLimitConfig.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/config/ValidatorConfig.java

# 工具类
[NEW] app-mgt-server/src/main/java/com/openapp/application/utils/RequestUtils.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/utils/SecurityUtils.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/utils/ValidationUtils.java

# 常量类
[NEW] app-mgt-server/src/main/java/com/openapp/application/constant/AppConstants.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/constant/ErrorCodes.java
[NEW] app-mgt-server/src/main/java/com/openapp/application/constant/Permissions.java
```

#### 9.1.2 资源配置文件 (app-mgt-server/src/main/resources/)
```
[NEW] app-mgt-server/src/main/resources/application.yml
[NEW] app-mgt-server/src/main/resources/application-dev.yml
[NEW] app-mgt-server/src/main/resources/application-prod.yml
[NEW] app-mgt-server/src/main/resources/application-test.yml
[NEW] app-mgt-server/src/main/resources/db/migration/V1__init_applications.sql
[NEW] app-mgt-server/src/main/resources/db/migration/V2__add_status_audit_log.sql
[NEW] app-mgt-server/src/main/resources/i18n/messages.properties
[NEW] app-mgt-server/src/main/resources/i18n/messages_en_US.properties
```

#### 9.1.3 测试文件 (app-mgt-server/src/test/java/com/openapp/application/)
```
[NEW] app-mgt-server/src/test/java/com/openapp/application/controller/ApplicationControllerIntegrationTest.java
[NEW] app-mgt-server/src/test/java/com/openapp/application/service/ApplicationServiceTest.java
[NEW] app-mgt-server/src/test/java/com/openapp/application/domain/ApplicationEntityTest.java
[NEW] app-mgt-server/src/test/java/com/openapp/application/domain/factory/ApplicationFactoryTest.java
[NEW] app-mgt-server/src/test/java/com/openapp/application/repository/ApplicationRepositoryTest.java
[NEW] app-mgt-server/src/test/resources/application-test.yml
[NEW] app-mgt-server/src/test/resources/data/init-test-data.sql
[NEW] app-mgt-server/src/test/contracts/application-contracts.yml
```

#### 9.1.4 根目录及配置文件
```
[NEW] app-mgt-server/pom.xml
[NEW] app-mgt-server/Dockerfile
[NEW] .specs/application-management/plan.md (本文件)
[NEW] .specs/architecture/adr/ADR-002.md (缓存架构决策)
```

### 9.2 需要修改的文件

```
[MODIFY] pom.xml - 在聚合项目中添加 app-mgt-server 子模块依赖
[MODIFY] app-mgt-server/pom.xml - 应用管理模块的独立依赖配置，包含 Java 21、Spring Boot 3.4.15、MyBatis 3.0.4、MySQL 5.7 等必要依赖
[MODIFY] src/main/resources/application.yml - 如有全局配置需要，否则仅在 app-mgt-server 中使用独立配置
[MODIFY] .opencode/sdd/state.json - 更新状态为 planned
[MODIFY] apps/permission-server/pom.xml - 如需要与权限服务通信，则添加相关依赖或配置
```

### 9.3 需要删除/移除的文件（如重构场景）

```
[DELETE] N/A - 此次为新增功能模块，暂无文件删除需求
```

### 9.4 保持不变的文件

```
[IGNORE] apps/permission-server/* - 由其他模块负责维护
[IGNORE] gateway/* - API网关模块
[IGNORE] common/* - 通用模块
[IGNORE] .gitignore - 版本忽略配置已覆盖新模块
```

---

## 10. 风险评估

### 10.1 技术风险

| 风险 | 概率 | 影响 | 缓解措施 |
|------|------|------|----------|
| MyBatis-Plus 性能问题 | 低 | 中 | 基准测试，必要时使用原生 SQL |
| Redis 缓存穿透 | 中 | 中 | 布隆过滤器 + 空值缓存 |
| 乐观锁冲突频繁 | 低 | 低 | 监控冲突率，调整重试策略 |
| JWT 令牌泄露 | 低 | 高 | 短有效期 + Refresh Token 机制 |

### 10.2 依赖风险

| 依赖 | 风险 | 缓解措施 |
|------|------|----------|
| permission-app | 认证接口变更 | 定义清晰的接口契约，版本控制 |
| MySQL | 数据库连接池耗尽 | 监控连接数，设置合理限制 |
| Redis | 缓存雪崩 | 随机 TTL + 多级缓存 |

### 10.3 时间风险

| 风险 | 概率 | 影响 | 缓解措施 |
|------|------|------|----------|
| 需求变更 | 中 | 中 | 明确范围，变更需评审 |
| 集成测试复杂 | 中 | 低 | 提前准备测试数据 |
| 性能不达标 | 低 | 中 | 预留性能优化时间 |

---

## 11. 开放技术问题

| 编号 | 问题 | 建议 | 状态 |
|------|------|------|------|
| **TQ-001** | 是否需要支持应用标签/分类？ | v1.0 暂不支持，后续扩展 | 待确认 |
| **TQ-002** | 应用图标是否需要上传服务？ | 建议使用外部图床/CDN | 待确认 |
| **TQ-003** | 是否需要应用版本管理？ | v1.0 不支持，后续扩展 | 待确认 |
| **TQ-004** | 软删除恢复期 30 天是否足够？ | 建议参考行业实践（通常 30-90 天） | 待产品确认 |

---

## 12. 下一步行动

### 12.1 计划审批后

1. ✅ 创建 ADR-002 (缓存架构决策)
2. ✅ 更新 state.json 状态为 `planned`
3. ⏳ 创建完整目录结构：`app-mgt-server/` 模块
4. ⏳ 等待计划审批
5. ⏳ 进入任务分解阶段 (@sdd-3-tasks)

### 12.2 任务分解预览

任务将按照六边形架构(MVC-Hexagonal-DDD)按以下层次分解：

#### 12.2.1 配置层任务
- T1.1: 创建 app-mgt-server 顶层模块及其 pom.xml
- T1.2: 配置 application.yml 及环境特定配置
- T1.3: 配置 Dockerfile

#### 12.2.2 领域层任务
- T2.1: 实现领域实体 (ApplicationEntity, StatusChange)
- T2.2: 实现值对象 (ApplicationName, CallbackUrl)
- T2.3: 实现领域服务和工厂
- T2.4: 实现领域事件和规则

#### 12.2.3 应用服务层任务
- T3.1: 实现命令对象和查询对象
- T3.2: 实现应用服务 (ApplicationQueryService, ApplicationCommandService)
- T3.3: 实现 DTO 对象

#### 12.2.4 业务服务层任务
- T4.1: 实现业务服务接口和服务实现
- T4.2: 实现业务逻辑和流程

#### 12.2.5 基础设施层任务
- T5.1: 实现数据对象 (DO) 和 MyBatisMapper
- T5.2: 实现仓储接口和实现
- T5.3: 实现缓存适配器和注解
- T5.4: 实现网关接口和实现

#### 12.2.6 接口适配层任务
- T6.1: 实现控制器 (Controller)
- T6.2: 实现安全组件和过滤器
- T6.3: 实现横切关注点 (日志、异常处理、切面)

#### 12.2.7 测试任务
- T7.1: 实现单元测试 (服务层、领域层)
- T7.2: 实现集成测试 (控制器、仓储)
- T7.3: 实现契约测试 (API 合同验证)

#### 12.2.8 部署任务
- T8.1: 更新整体构建配置 (CI/CD)
- T8.2: 更新部署文档
- T8.3: 准备上线清单

---

## 附录

### A. 状态机图

```
┌─────────────────────────────────────────────────────────────┐
│                    应用状态机                                 │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│    ┌──────┐      启用      ┌──────┐      禁用      ┌────────┐│
│    │ DRAFT│ ─────────────▶ │ACTIVE│ ─────────────▶ │DISABLED││
│    └──────┘               └──────┘               └────────┘│
│       │                      │                      │       │
│       │                      │                      │       │
│       │         删除          │         删除          │  删除   │
│       ▼                      ▼                      ▼       │
│    ┌─────────────────────────────────────────────────────┐  │
│    │                    DELETED                          │  │
│    │              (30 天后自动物理删除)                    │  │
│    └─────────────────────────────────────────────────────┘  │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### B. API 端点汇总

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| POST | /api/v1/applications | 创建应用 | 认证用户 |
| GET | /api/v1/applications | 获取应用列表 | 认证用户 |
| GET | /api/v1/applications/:id | 获取应用详情 | 所有者/管理员 |
| PUT | /api/v1/applications/:id | 更新应用 | 所有者 |
| DELETE | /api/v1/applications/:id | 删除应用 | 所有者 |
| PATCH | /api/v1/applications/:id/status | 变更状态 | 管理员 |

### C. Maven 依赖配置

#### C.1 父级 pom.xml (根项目聚合配置)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.openapp</groupId>
    <artifactId>open-app</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>pom</packaging>

    <modules>
        <module>common</module>
        <module>apps</module>
        <module>gateway</module>
        <module>infra</module>
    </modules>

    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <spring.boot.version>3.4.15</spring.boot.version>
        <java.version>21</java.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring.boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>21</source>
                    <target>21</target>
                    <compilerArgs>--enable-preview</compilerArgs>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

#### C.2 apps/pom.xml (应用程序聚合模块)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>com.openapp</groupId>
        <artifactId>open-app</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>apps</artifactId>
    <packaging>pom</packaging>

    <modules>
        <module>permission-server</module>
        <module>member-server</module>
        <module>app-mgt-server</module>
    </modules>
</project>
```

#### C.3 app-mgt-server/pom.xml (应用管理服务模块)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.openapp</groupId>
        <artifactId>apps</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>app-mgt-server</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <properties>
        <java.version>21</java.version>
        <spring.boot.version>3.4.15</spring.boot.version>
        <mybatis.version>3.0.4</mybatis.version>
        <mysql.version>5.1.49</mysql.version>
        <mybatis.plus.version>3.5.3.2</mybatis.plus.version>
        <lombok.version>1.18.28</lombok.version>
    </properties>

    <dependencies>
        <!-- SpringBoot Web Starter -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <!-- SpringBoot Configuration Processor -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- MyBatis Spring Boot Starter -->
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>${mybatis.version}</version>
        </dependency>

        <!-- MyBatis Plus - MyBatis 增强工具 -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>${mybatis.plus.version}</version>
        </dependency>

        <!-- MySQL Connector -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>${mysql.version}</version>
        </dependency>

        <!-- HikariCP - 默认连接池 -->
        <dependency>
            <groupId>com.zaxxer</groupId>
            <artifactId>HikariCP</artifactId>
        </dependency>

        <!-- SpringBoot Data JPA (可选，用于复杂查询) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <!-- SpringBoot Data Redis -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>

        <!-- SpringBoot Validation -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- SpringBoot Security -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>

        <!-- JWT Support -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>0.11.5</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>0.11.5</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>0.11.5</version>
            <scope>runtime</scope>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>${lombok.version}</version>
            <optional>true</optional>
        </dependency>

        <!-- Jackson Modules -->
        <dependency>
            <groupId>com.fasterxml.jackson.module</groupId>
            <artifactId>jackson-module-parameter-names</artifactId>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.datatype</groupId>
            <artifactId>jackson-datatype-jsr310</artifactId>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.datatype</groupId>
            <artifactId>jackson-datatype-jdk8</artifactId>
        </dependency>

        <!-- Commons Lang3 -->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
        </dependency>

        <!-- Apache Commons Text -->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-text</artifactId>
            <version>1.10.0</version>
        </dependency>

        <!-- Validation API -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter-test</artifactId>
            <version>${mybatis.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>21</source>
                    <target>21</target>
                    <compilerArgs>--enable-preview</compilerArgs>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### D. 与其他模块的交互关系

#### D1. 与 permission-server 模块交互

app-mgt-server 通过 Gateway API 接口与 permission-server 模块进行交互，而不是直接依赖：

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│ app-mgt-server  │────▶│ Gateway API     │────▶│ permission-     │
│ (Application    │     │ (Permission     │     │ server         │
│ Domain)         │◀────│ Gateway)        │◀────│                │
└─────────────────┘     └─────────────────┘     └─────────────────┘
```

- 采用依赖倒置原则(DIP)，app-mgt-server 依赖于抽象的 PermissionGatewayInterface
- 运行时动态注入 PermissionGatewayImpl 实现
- 保证了 Domain 层的独立性，不违反六边形架构边界

#### D2. 数据库分离策略

app-mgt-server 拥有自己的独立数据库 schema：
- `app_mgt_db` 专门存储应用管理相关数据
- 通过服务间通信而非数据库直连与其他模块交互
- 保证了数据一致性边界和事务边界的一致性

---

**状态**: `proposed` → `planned`  
**下一步**: Task Decomposition (`@sdd-3-tasks`)  
**模块分类**: `core-application-management` (核心应用管理服务)
