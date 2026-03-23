# Feature Specification: 应用管理系统

## 元数据

| 字段 | 值 |
|------|------|
| **标识符** | `APP-MGT-001` |
| **名称** | 应用管理系统 |
| **版本** | 1.0.0 |
| **创建日期** | 2026-03-23 |
| **作者** | Summer |
| **优先级** | P0 |
| **状态** | `drafting` → `specified` |

---

## 1. 概述

### 1.1 背景

**open-app** 是企业通讯能力开放平台，将 XXX 通讯系统的核心能力（IM、Meeting、CloudBox 等）开放给业务应用和个人应用。

**应用管理** 是平台的基础设施模块，所有其他模块都依赖它。只有经过注册和管理的应用才能调用平台 API。

### 1.2 问题陈述

应用管理模块主要解决：
- **应用准入**：规范应用的注册和审核流程
- **应用信息管理**：应用信息的 CRUD 操作
- **应用生命周期**：应用从创建到下线的整个生命周期管理

### 1.3 目标用户

| 用户类型 | 说明 |
|----------|------|
| **应用开发者** | 创建和管理自己的应用，获取应用凭证 |

### 1.4 技术栈

| 层级 | 技术 | 版本 | 说明 |
|------|------|------|------|
| **后端框架** | Spring Boot | 3.4.15 | REST API 服务 |
| **语言** | Java | 21 | LTS 版本 |
| **ORM** | MyBatis-Plus | 3.5.x | 数据访问层 |
| **数据库** | MySQL | 5.7+ | 数据持久化 |
| **缓存** | Redis | 6.0+ | 缓存层 |

**模块性质**: 本模块为 **纯后端服务模块**，不包含任何前端 UI 组件。前端工程（如有）将以 `-web` 后缀独立命名（如 `application-management-web`）。

---

## 2. 目标与非目标

### 2.1 Goals（本次要做的）

| 编号 | 功能 | 说明 |
|------|------|------|
| **G1** | 应用注册 | 创建应用、填写应用信息、提交审核 |
| **G2** | 应用编辑 | 修改已创建的应用信息 |
| **G3** | 应用查询 | 查看应用列表、查看应用详情 |
| **G4** | 应用删除 | 软删除/硬删除应用 |
| **G6** | 应用状态管理 | 启用、禁用、下线应用 |

### 2.2 Non-Goals（本次不做）

| 编号 | 功能 | 归属 |
|------|------|------|
| **NG0** | 前端管理界面（应用列表、详情、状态变更等 UI） | 独立前端仓库，命名约定 `*-web` |
| **NG1** | 应用成员管理 | `APP-MEM-001` |
| **NG2** | 应用凭证管理（AppKey/AppSecret） | `permission-app` |
| **NG3** | 移动端应用界面 | 独立前端仓库 |
| **NG4** | 应用数据分析/统计 | 后续 Feature |
| **NG5** | 多环境管理（dev/prod 分离） | 后续 Feature |

---

## 3. 用户故事

| ID | 用户故事 | 验收标准 |
|----|----------|----------|
| **US-001** | 作为应用开发者，我想要创建应用，以便接入 open-app 平台 | 能够成功创建应用，填写基本信息 |
| **US-002** | 作为应用开发者，我想要修改应用信息，以便更新应用配置 | 能够修改应用名称、描述等信息 |
| **US-003** | 作为应用开发者，我想要查看我的应用列表，以便管理我的应用 | 能够查看自己创建的所有应用 |
| **US-004** | 作为应用开发者，我想要查看应用详情，以便了解应用配置 | 能够查看应用的完整信息 |
| **US-005** | 作为应用开发者，我想要删除应用，以便清理不用的应用 | 能够删除自己的应用 |
| **US-006** | 作为平台管理员，我想要启用/禁用应用，以便管理平台上的应用 | 能够变更应用状态 |

---

## 4. 功能需求

### 4.1 应用注册 (FR-001)

**描述**: 应用开发者可以创建新应用

**需求**:
- FR-001.1: 系统必须支持创建新应用
- FR-001.2: 创建应用时必须填写：应用名称、应用描述、应用类型
- FR-001.3: 应用名称在开发者账号下必须唯一
- FR-001.4: 创建应用时自动设置状态为 `draft`（草稿）
- FR-001.5: 创建应用时自动生成应用 ID

**验收标准**:
- ✅ 能够成功创建应用
- ✅ 必填字段验证正确
- ✅ 应用名称重复时提示错误
- ✅ 创建后状态为 `draft`

---

### 4.2 应用编辑 (FR-002)

**描述**: 应用开发者可以修改已创建的应用的信息

**需求**:
- FR-002.1: 系统必须支持修改应用信息
- FR-002.2: 可修改字段：应用名称、应用描述、应用图标、回调 URL
- FR-002.3: 应用 ID 不可修改
- FR-002.4: 只有应用所有者可以修改应用信息
- FR-002.5: 修改后自动保存，无需审核

**验收标准**:
- ✅ 能够修改允许修改的字段
- ✅ 应用 ID 不可修改
- ✅ 非所有者无法修改
- ✅ 修改后立即可见

---

### 4.3 应用查询 (FR-003)

**描述**: 应用开发者可以查看应用列表和应用详情

**需求**:
- FR-003.1: 系统必须支持查看应用列表
- FR-003.2: 应用列表支持分页
- FR-003.3: 应用列表支持按状态筛选
- FR-003.4: 开发者只能查看自己的应用
- FR-003.5: 系统必须支持查看应用详情
- FR-003.6: 应用详情包含所有字段

**验收标准**:
- ✅ 能够查看应用列表
- ✅ 分页功能正常
- ✅ 筛选功能正常
- ✅ 只能看到自己的应用
- ✅ 能够查看应用详情

---

### 4.4 应用删除 (FR-004)

**描述**: 应用开发者可以删除应用

**需求**:
- FR-004.1: 系统必须支持删除应用
- FR-004.2: 删除采用软删除（标记删除，不物理删除）
- FR-004.3: 只有应用所有者可以删除应用
- FR-004.4: 删除后应用状态变为 `deleted`
- FR-004.5: 删除后 30 天内可恢复
- FR-004.6: 超过 30 天自动物理删除

**验收标准**:
- ✅ 能够删除应用
- ✅ 删除后状态变为 `deleted`
- ✅ 非所有者无法删除
- ✅ 30 天内可恢复
- ✅ 超过 30 天自动物理删除

---

### 4.5 应用状态管理 (FR-005)

**描述**: 管理应用的生命周期状态

**需求**:
- FR-005.1: 系统必须支持以下状态：`draft`、`active`、`disabled`、`deleted`
- FR-005.2: 状态转换规则：
  - `draft` → `active`（启用）
  - `active` → `disabled`（禁用）
  - `disabled` → `active`（启用）
  - 任何状态 → `deleted`（删除）
- FR-005.3: 只有平台管理员可以变更应用状态
- FR-005.4: 状态变更必须记录审计日志
- FR-005.5: 状态变更后实时生效

**验收标准**:
- ✅ 状态定义正确
- ✅ 状态转换规则正确
- ✅ 只有管理员可以变更状态
- ✅ 审计日志记录完整
- ✅ 状态变更实时生效

---

## 5. 非功能需求

### 5.1 性能需求

| 编号 | 需求 | 指标 |
|------|------|------|
| **NFR-001** | 应用查询响应时间 | P95 < 100ms |
| **NFR-002** | 应用创建响应时间 | P95 < 500ms |
| **NFR-003** | 应用列表分页 | 支持每页 10/20/50 条 |
| **NFR-004** | 并发支持 | 支持 1000 并发请求 |

### 5.2 安全需求

| 编号 | 需求 | 说明 |
|------|------|------|
| **NFR-010** | 认证要求 | 所有 API 必须经过认证 |
| **NFR-011** | 授权要求 | 只有应用所有者可以管理自己的应用 |
| **NFR-012** | 管理员权限 | 只有平台管理员可以变更应用状态 |
| **NFR-013** | 数据隔离 | 开发者只能访问自己的应用数据 |
| **NFR-014** | 审计日志 | 所有状态变更必须记录审计日志 |

### 5.3 可用性需求

| 编号 | 需求 | 指标 |
|------|------|------|
| **NFR-020** | 系统可用性 | ≥ 99.9% |
| **NFR-021** | 数据持久性 | ≥ 99.99% |
| **NFR-022** | 错误处理 | 所有错误必须有清晰的错误码和错误信息 |

### 5.4 兼容性需求

| 编号 | 需求 | 说明 |
|------|------|------|
| **NFR-030** | API 版本 | API 必须有版本号（v1） |
| **NFR-031** | 向后兼容 | API 变更必须向后兼容 |

---

## 6. 数据模型

### 6.1 应用表 (applications)

```sql
CREATE TABLE applications (
    id              VARCHAR(64) PRIMARY KEY,
    name            VARCHAR(128) NOT NULL,
    description     TEXT,
    icon_url        VARCHAR(512),
    type            VARCHAR(32) NOT NULL,  -- self_build, third_party, personal
    status          VARCHAR(16) NOT NULL DEFAULT 'draft',  -- draft, active, disabled, deleted
    owner_id        VARCHAR(64) NOT NULL,  -- 应用所有者 ID
    owner_type      VARCHAR(16) NOT NULL,  -- user, organization
    callback_url    VARCHAR(512),
    deleted_at      TIMESTAMP NULL,
    deleted_by      VARCHAR(64) NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      VARCHAR(64) NOT NULL,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by      VARCHAR(64) NOT NULL,
    
    INDEX idx_owner_id (owner_id),
    INDEX idx_status (status),
    INDEX idx_deleted_at (deleted_at),
    UNIQUE KEY uk_owner_name (owner_id, name)
);
```

### 6.2 应用状态枚举

```typescript
enum AppStatus {
  DRAFT = 'draft',       // 草稿
  ACTIVE = 'active',     // 已启用
  DISABLED = 'disabled', // 已禁用
  DELETED = 'deleted'    // 已删除
}
```

### 6.3 应用类型枚举

```typescript
enum AppType {
  SELF_BUILD = 'self_build',    // 自建应用
  THIRD_PARTY = 'third_party',  // 第三方应用
  PERSONAL = 'personal'         // 个人应用
}
```

---

## 7. API 设计

**设计原则**: API-First，所有接口设计为 RESTful 风格，供以下调用方使用：
- 前端管理控制台（独立前端工程，命名约定 `*-web`）
- 第三方系统集成
- 移动端应用

**API 规范**: 
- 所有接口遵循 RESTful 设计规范
- 响应格式统一（data, code, message）
- 使用 JWT 进行身份认证
- 使用 HTTPS 保证传输安全

### 7.1 创建应用

```http
POST /api/v1/applications
Content-Type: application/json
Authorization: Bearer {access_token}

{
  "name": "我的应用",
  "description": "应用描述",
  "type": "self_build",
  "icon_url": "https://example.com/icon.png",
  "callback_url": "https://example.com/callback"
}

Response: 201 Created
{
  "id": "app_xxxxxxxxxxxx",
  "name": "我的应用",
  "status": "draft",
  "created_at": "2026-03-23T10:00:00Z"
}
```

### 7.2 获取应用列表

```http
GET /api/v1/applications?page=1&page_size=20&status=active
Authorization: Bearer {access_token}

Response: 200 OK
{
  "data": [
    {
      "id": "app_xxxxxxxxxxxx",
      "name": "我的应用",
      "type": "self_build",
      "status": "active",
      "created_at": "2026-03-23T10:00:00Z"
    }
  ],
  "total": 10,
  "page": 1,
  "page_size": 20
}
```

### 7.3 获取应用详情

```http
GET /api/v1/applications/{app_id}
Authorization: Bearer {access_token}

Response: 200 OK
{
  "id": "app_xxxxxxxxxxxx",
  "name": "我的应用",
  "description": "应用描述",
  "type": "self_build",
  "status": "active",
  "icon_url": "https://example.com/icon.png",
  "callback_url": "https://example.com/callback",
  "owner_id": "user_xxxxxxxxxxxx",
  "created_at": "2026-03-23T10:00:00Z",
  "updated_at": "2026-03-23T10:00:00Z"
}
```

### 7.4 更新应用

```http
PUT /api/v1/applications/{app_id}
Content-Type: application/json
Authorization: Bearer {access_token}

{
  "name": "更新后的应用名称",
  "description": "更新后的描述",
  "icon_url": "https://example.com/new-icon.png",
  "callback_url": "https://example.com/new-callback"
}

Response: 200 OK
```

### 7.5 删除应用

```http
DELETE /api/v1/applications/{app_id}
Authorization: Bearer {access_token}

Response: 204 No Content
```

### 7.6 变更应用状态（管理员）

```http
PATCH /api/v1/applications/{app_id}/status
Content-Type: application/json
Authorization: Bearer {admin_access_token}

{
  "status": "disabled",
  "reason": "违规操作"
}

Response: 200 OK
```

---

## 8. 边界情况

### 8.1 错误处理 (EC-001)

| 场景 | 错误码 | 错误信息 | 处理方式 |
|------|--------|----------|----------|
| 应用不存在 | APP_NOT_FOUND | 应用不存在或已删除 | 返回 404 |
| 无权访问 | ACCESS_DENIED | 无权访问此应用 | 返回 403 |
| 应用名称重复 | APP_NAME_DUPLICATE | 应用名称已存在 | 返回 409 |
| 无效的状态转换 | INVALID_STATUS_TRANSITION | 不允许的状态转换 | 返回 400 |
| 应用已删除 | APP_DELETED | 应用已删除，请先恢复 | 返回 410 |

### 8.2 极端情况 (EC-002)

| 场景 | 处理方式 |
|------|----------|
| 单开发者创建大量应用 | 限制单开发者最大应用数量（100 个） |
| 应用名称包含敏感词 | 应用名称需要审核 |
| 并发修改同一应用 | 使用乐观锁，version 字段控制 |
| 软删除后 30 天内恢复 | 支持恢复操作，恢复后状态为 `disabled` |

### 8.3 并发场景 (EC-003)

| 场景 | 处理方式 |
|------|----------|
| 多人同时修改应用 | 乐观锁，version 字段控制 |
| 状态变更与应用编辑冲突 | 状态变更优先级更高 |
| 删除过程中有查询请求 | 删除操作加锁，查询等待 |

---

## 9. 依赖关系

### 9.1 外部依赖

| 依赖 | 说明 | 状态 |
|------|------|------|
| **permission-app** | 用户认证和授权 | 需要 |
| **数据库** | PostgreSQL/MySQL | 需要 |

### 9.2 被依赖

| 模块 | 依赖内容 |
|------|----------|
| **permission-app** | 需要应用 ID 进行权限校验 |
| **API Gateway** | 需要应用 ID 进行 API 调用鉴权 |
| **事件开放模块** | 需要应用 ID 进行事件订阅 |
| **回调开放模块** | 需要应用 ID 进行回调配置 |
| **机器人开放模块** | 需要应用 ID 创建机器人 |

### 9.3 相关仓库

| 仓库 | 说明 |
|------|------|
| **open-app** (当前) | 后端服务，包含本模块 |
| **open-app-web** (规划中) | 前端管理控制台，调用本模块 API |
| **open-app-mobile** (规划中) | 移动端应用 |

---

## 10. 开放问题

| 编号 | 问题 | 状态 | 负责人 |
|------|------|------|--------|
| **OQ-001** | 应用审核流程是否需要？ | 待讨论 | Product |
| **OQ-002** | 应用名称是否需要全局唯一（还是开发者账号下唯一）？ | 已确认：开发者账号下唯一 | Product |
| **OQ-003** | 软删除后 30 天是否足够？ | 待讨论 | Product |
| **OQ-004** | 是否需要支持应用转移（变更所有者）？ | 后续 Feature | Product |

---

## 11. 验收标准

### 11.1 功能验收

- [ ] 能够成功创建应用
- [ ] 能够修改应用信息
- [ ] 能够查看应用列表和详情
- [ ] 能够删除应用（软删除）
- [ ] 能够变更应用状态（管理员）
- [ ] 权限控制正确（只能管理自己的应用）

### 11.2 非功能验收

- [ ] 应用查询响应时间 P95 < 100ms
- [ ] 支持 1000 并发请求
- [ ] 所有错误有清晰的错误码
- [ ] 审计日志记录完整

---

## 12. 修订历史

| 版本 | 日期 | 作者 | 变更说明 |
|------|------|------|----------|
| 1.0.0 | 2026-03-23 | Summer | 初始版本 |

---

**状态**: `drafting` → `specified`  
**下一步**: Technical Planning (`@sdd 继续`)
