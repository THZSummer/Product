# Task Decomposition: 应用管理系统 (APP-MGT-001)

## 元数据

| 字段 | 值 |
|------|------|
| **标识符** | `APP-MGT-001` |
| **名称** | 应用管理系统 |
| **版本** | 1.0.0 |
| **创建日期** | 2026-03-23 |
| **状态** | `planned` → `tasked` |
| **总任务数** | 14 |
| **总预估工时** | 47.5 小时 |

---

## 任务执行波次

```
Wave 1 (无依赖): T2, T3
         │
Wave 2 (依赖 Wave 1): T4, T5, T6
         │
Wave 3 (依赖 Wave 2): T7, T8, T9, T10, T11
         │
Wave 4 (依赖 Wave 3): T12, T13
         │
Wave 5 (收尾): T14
```

**注意**: TASK-001 (数据库迁移) 已跳过，建表脚本将手动执行，脚本保留在源码中即可。

---

## 任务分解

### TASK-001: 数据库表结构设计与迁移

**优先级**: P0  
**复杂度**: M  
**预估工时**: 3 小时  
**执行波次**: 1  
**前置依赖**: 无  
**状态**: ✅ 可开始执行

**描述**:
创建应用管理模块所需的数据库表结构 SQL 脚本，保留到源码中。无需自动执行，手动执行建表。

**涉及文件**:
- [NEW] `application-management/src/main/resources/db/migration/V1__init_applications.sql`
- [NEW] `application-management/src/main/resources/db/migration/V2__init_application_status_logs.sql`

**验收标准**:
- [ ] 创建 applications 表的 SQL 脚本（包含所有必需字段）
- [ ] 创建 application_status_logs 表的 SQL 脚本
- [ ] 包含必要的索引（owner_id, status, deleted_at, 联合索引）
- [ ] 包含唯一约束（owner_id, name）
- [ ] SQL 脚本保留在 `application-management/src/main/resources/db/migration/` 目录
- [ ] SQL 脚本语法正确，可手动执行

**SQL 脚本示例**:
```sql
-- V1__init_applications.sql
CREATE TABLE applications (
    id              VARCHAR(64) PRIMARY KEY COMMENT '应用 ID',
    name            VARCHAR(128) NOT NULL COMMENT '应用名称',
    -- ... 其他字段
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用表';
```

**说明**:
- 本任务完成后，SQL 脚本保留在源码中
- 后续由 DBA 或运维人员手动执行建表
- 不需要配置 Flyway 自动迁移

---

### TASK-002: 领域层 - 实体与枚举

**优先级**: P0  
**复杂度**: S  
**预估工时**: 2 小时  
**执行波次**: 1  
**前置依赖**: 无

**描述**:
创建领域模型，包括 Application 实体类和 AppType、AppStatus 枚举类。

**涉及文件**:
- [NEW] `application-management/src/main/java/com/openapp/application/domain/Application.java`
- [NEW] `application-management/src/main/java/com/openapp/application/domain/enums/AppType.java`
- [NEW] `application-management/src/main/java/com/openapp/application/domain/enums/AppStatus.java`
- [NEW] `application-management/src/main/java/com/openapp/application/domain/StatusTransitions.java`

**验收标准**:
- [ ] Application 实体使用 MyBatis-Plus 注解正确
- [ ] 主键使用 ASSIGN_ID 策略
- [ ] 乐观锁 version 字段配置正确
- [ ] 审计字段自动填充配置正确
- [ ] AppType 枚举包含所有类型（SELF_BUILD, THIRD_PARTY, PERSONAL）
- [ ] AppStatus 枚举包含所有状态（DRAFT, ACTIVE, DISABLED, DELETED）
- [ ] StatusTransitions 实现状态转换验证逻辑

**验证命令**:
```bash
./mvnw test -Dtest=ApplicationTest
# 编译检查
./mvnw compile
```

---

### TASK-003: DTO 层 - 请求/响应对象

**优先级**: P0  
**复杂度**: S  
**预估工时**: 2 小时  
**执行波次**: 1  
**前置依赖**: 无

**描述**:
创建 API 请求和响应 DTO，包括创建/更新请求、列表响应、详情响应、错误响应。

**涉及文件**:
- [NEW] `application-management/src/main/java/com/openapp/application/dto/request/CreateApplicationRequest.java`
- [NEW] `application-management/src/main/java/com/openapp/application/dto/request/UpdateApplicationRequest.java`
- [NEW] `application-management/src/main/java/com/openapp/application/dto/request/ChangeStatusRequest.java`
- [NEW] `application-management/src/main/java/com/openapp/application/dto/response/CreateApplicationResponse.java`
- [NEW] `application-management/src/main/java/com/openapp/application/dto/response/UpdateApplicationResponse.java`
- [NEW] `application-management/src/main/java/com/openapp/application/dto/response/ApplicationListResponse.java`
- [NEW] `application-management/src/main/java/com/openapp/application/dto/response/ApplicationDetailResponse.java`
- [NEW] `application-management/src/main/java/com/openapp/application/dto/response/ErrorResponse.java`
- [NEW] `application-management/src/main/java/com/openapp/application/dto/response/PageResponse.java`

**验收标准**:
- [ ] 请求 DTO 包含正确的 validation 注解
- [ ] 响应 DTO 包含所有需要返回的字段
- [ ] 使用 Lombok 简化代码
- [ ] 日期格式统一使用 ISO-8601
- [ ] 错误响应包含 code, message, details 字段

**验证命令**:
```bash
./mvnw compile
```

---

### TASK-004: Mapper 层 - MyBatis 配置

**优先级**: P0  
**复杂度**: S  
**预估工时**: 1.5 小时  
**执行波次**: 2  
**前置依赖**: TASK-001, TASK-002

**描述**:
创建 MyBatis Mapper 接口和 XML 配置文件。

**涉及文件**:
- [NEW] `application-management/src/main/java/com/openapp/application/mapper/ApplicationMapper.java`
- [NEW] `application-management/src/main/java/com/openapp/application/mapper/ApplicationStatusLogMapper.java`
- [NEW] `application-management/src/main/resources/mapper/ApplicationMapper.xml`
- [NEW] `application-management/src/main/resources/mapper/ApplicationStatusLogMapper.xml`
- [MODIFY] `application-management/src/main/resources/application.yml` (MyBatis 配置)

**验收标准**:
- [ ] ApplicationMapper 继承 BaseMapper
- [ ] 自定义查询方法（findById, findByOwnerId, findByStatus）
- [ ] XML 中配置结果映射
- [ ] 软删除查询条件配置
- [ ] 乐观锁更新语句

**验证命令**:
```bash
./mvnw test -Dtest=ApplicationMapperTest
```

---

### TASK-005: Repository 层 - 数据访问

**优先级**: P0  
**复杂度**: S  
**预估工时**: 2 小时  
**执行波次**: 2  
**前置依赖**: TASK-001, TASK-002, TASK-004

**描述**:
创建 Repository 层，封装数据访问逻辑。

**涉及文件**:
- [NEW] `application-management/src/main/java/com/openapp/application/repository/ApplicationRepository.java`
- [NEW] `application-management/src/main/java/com/openapp/application/repository/ApplicationStatusLogRepository.java`

**验收标准**:
- [ ] create 方法实现
- [ ] findById 方法实现（支持软删除过滤）
- [ ] findByOwner 方法实现（支持分页、状态筛选）
- [ ] update 方法实现（乐观锁）
- [ ] softDelete 方法实现
- [ ] restore 方法实现
- [ ] 状态日志记录方法

**验证命令**:
```bash
./mvnw test -Dtest=ApplicationRepositoryTest
```

---

### TASK-006: 异常处理层

**优先级**: P1  
**复杂度**: S  
**预估工时**: 1.5 小时  
**执行波次**: 2  
**前置依赖**: TASK-002, TASK-003

**描述**:
创建异常处理类，包括自定义异常和全局异常处理器。

**涉及文件**:
- [NEW] `application-management/src/main/java/com/openapp/application/exception/ApplicationErrorCode.java`
- [NEW] `application-management/src/main/java/com/openapp/application/exception/ApplicationException.java`
- [NEW] `application-management/src/main/java/com/openapp/application/exception/GlobalExceptionHandler.java`

**验收标准**:
- [ ] ApplicationErrorCode 定义所有错误码
- [ ] ApplicationException 继承 RuntimeException
- [ ] GlobalExceptionHandler 捕获并处理所有异常
- [ ] 返回统一错误响应格式
- [ ] 处理参数验证异常
- [ ] 处理数据库异常

**验证命令**:
```bash
./mvnw compile
```

---

### TASK-007: Service 层 - 业务逻辑

**优先级**: P0  
**复杂度**: M  
**预估工时**: 6 小时  
**执行波次**: 3  
**前置依赖**: TASK-002, TASK-004, TASK-005, TASK-006

**描述**:
创建 Service 层，实现核心业务逻辑。

**涉及文件**:
- [NEW] `application-management/src/main/java/com/openapp/application/service/ApplicationService.java`
- [NEW] `application-management/src/main/java/com/openapp/application/service/impl/ApplicationServiceImpl.java`
- [NEW] `application-management/src/main/java/com/openapp/application/service/ApplicationStatusLogService.java`
- [NEW] `application-management/src/main/java/com/openapp/application/service/impl/ApplicationStatusLogServiceImpl.java`

**验收标准**:
- [ ] createApplication 方法（包含名称唯一性校验）
- [ ] getApplication 方法（权限校验）
- [ ] listApplications 方法（分页、筛选）
- [ ] updateApplication 方法（乐观锁、权限校验）
- [ ] deleteApplication 方法（软删除、权限校验）
- [ ] restoreApplication 方法（管理员权限）
- [ ] changeStatus 方法（状态转换验证、审计日志）
- [ ] 所有方法包含权限校验逻辑

**验证命令**:
```bash
./mvnw test -Dtest=ApplicationServiceTest
```

---

### TASK-008: Controller 层 - API 实现

**优先级**: P0  
**复杂度**: M  
**预估工时**: 4 小时  
**执行波次**: 3  
**前置依赖**: TASK-003, TASK-006, TASK-007

**描述**:
创建 REST Controller，暴露 API 端点。

**涉及文件**:
- [NEW] `application-management/src/main/java/com/openapp/application/controller/ApplicationController.java`
- [NEW] `application-management/src/main/java/com/openapp/application/config/WebMvcConfig.java`

**验收标准**:
- [ ] POST /api/v1/applications 创建应用
- [ ] GET /api/v1/applications 获取应用列表
- [ ] GET /api/v1/applications/{id} 获取应用详情
- [ ] PUT /api/v1/applications/{id} 更新应用
- [ ] DELETE /api/v1/applications/{id} 删除应用
- [ ] PATCH /api/v1/applications/{id}/status 变更状态（管理员）
- [ ] 请求参数验证
- [ ] 响应格式统一包装
- [ ] 路径参数校验

**验证命令**:
```bash
./mvnw test -Dtest=ApplicationControllerTest
```

---

### TASK-009: 安全与认证中间件

**优先级**: P0  
**复杂度**: M  
**预估工时**: 4 小时  
**执行波次**: 3  
**前置依赖**: TASK-002, TASK-006

**描述**:
实现 JWT 认证和权限校验中间件。

**涉及文件**:
- [NEW] `application-management/src/main/java/com/openapp/application/security/JwtAuthFilter.java`
- [NEW] `application-management/src/main/java/com/openapp/application/security/JwtTokenProvider.java`
- [NEW] `application-management/src/main/java/com/openapp/application/security/ApplicationAuthAspect.java`
- [NEW] `application-management/src/main/java/com/openapp/application/security/RequireOwnerOrAdmin.java`
- [NEW] `application-management/src/main/java/com/openapp/application/config/SecurityConfig.java`

**验收标准**:
- [ ] JWT 令牌解析和验证
- [ ] 用户身份从 JWT 中提取
- [ ] 角色信息从 JWT 中提取
- [ ] AOP 切面实现权限校验
- [ ] 自定义注解 @RequireOwnerOrAdmin
- [ ] SecurityConfig 配置正确
- [ ] 未认证请求返回 401
- [ ] 无权限请求返回 403

**验证命令**:
```bash
./mvnw test -Dtest=JwtTokenProviderTest
./mvnw test -Dtest=ApplicationAuthAspectTest
```

---

### TASK-010: 缓存层 - Redis 集成

**优先级**: P1  
**复杂度**: M  
**预估工时**: 3.5 小时  
**执行波次**: 3  
**前置依赖**: TASK-005, TASK-007

**描述**:
实现缓存层，提升查询性能。

**涉及文件**:
- [NEW] `application-management/src/main/java/com/openapp/application/cache/ApplicationCacheService.java`
- [NEW] `application-management/src/main/java/com/openapp/application/config/CacheConfig.java`
- [MODIFY] `application-management/pom.xml` (Redis 依赖)
- [MODIFY] `application-management/src/main/resources/application.yml` (Redis 配置)

**验收标准**:
- [ ] Redis 连接配置正确
- [ ] Spring Cache 启用
- [ ] 应用详情缓存（TTL: 5 分钟）
- [ ] 应用列表缓存（TTL: 1 分钟）
- [ ] 缓存失效逻辑（更新/删除时）
- [ ] 缓存穿透防护
- [ ] 缓存 key 命名规范

**验证命令**:
```bash
./mvnw test -Dtest=ApplicationCacheServiceTest
# 验证 Redis 连接
redis-cli ping
```

---

### TASK-011: 审计日志服务

**优先级**: P1  
**复杂度**: S  
**预估工时**: 2.5 小时  
**执行波次**: 3  
**前置依赖**: TASK-004, TASK-005, TASK-007

**描述**:
实现状态变更审计日志功能。

**涉及文件**:
- [NEW] `application-management/src/main/java/com/openapp/application/service/impl/ApplicationStatusLogServiceImpl.java`
- [NEW] `application-management/src/main/java/com/openapp/application/domain/ApplicationStatusLog.java`
- [NEW] `application-management/src/main/java/com/openapp/application/dto/request/StatusChangeAudit.java`

**验收标准**:
- [ ] 状态变更时自动记录日志
- [ ] 日志包含旧状态、新状态、变更原因、变更人、变更时间
- [ ] 审计日志查询接口
- [ ] 日志不可修改

**验证命令**:
```bash
./mvnw test -Dtest=ApplicationStatusLogServiceTest
```

---

### TASK-012: 单元测试

**优先级**: P0  
**复杂度**: M  
**预估工时**: 8 小时  
**执行波次**: 4  
**前置依赖**: TASK-007, TASK-008, TASK-009

**描述**:
编写单元测试，确保代码覆盖率达标。

**涉及文件**:
- [NEW] `application-management/src/test/java/com/openapp/application/service/ApplicationServiceTest.java`
- [NEW] `application-management/src/test/java/com/openapp/application/service/ApplicationStatusLogServiceTest.java`
- [NEW] `application-management/src/test/java/com/openapp/application/controller/ApplicationControllerTest.java`
- [NEW] `application-management/src/test/java/com/openapp/application/security/JwtTokenProviderTest.java`
- [NEW] `application-management/src/test/java/com/openapp/application/security/ApplicationAuthAspectTest.java`
- [NEW] `application-management/src/test/java/com/openapp/application/domain/StatusTransitionsTest.java`
- [NEW] `application-management/src/test/java/com/openapp/application/repository/ApplicationRepositoryTest.java`
- [NEW] `application-management/src/test/resources/application-test.yml`

**验收标准**:
- [ ] Service 层覆盖率 ≥ 60%
- [ ] 所有业务逻辑有对应测试
- [ ] 边界情况测试完整
- [ ] 异常场景测试完整
- [ ] 测试可独立运行
- [ ] Mock 使用合理

**验证命令**:
```bash
./mvnw test
./mvnw test jacoco:report
# 查看覆盖率报告
open target/site/jacoco/index.html
```

---

### TASK-013: 集成测试

**优先级**: P1  
**复杂度**: M  
**预估工时**: 6 小时  
**执行波次**: 4  
**前置依赖**: TASK-008, TASK-009, TASK-010

**描述**:
编写 API 集成测试，验证端到端功能。

**涉及文件**:
- [NEW] `application-management/src/test/java/com/openapp/application/integration/ApplicationApiIntegrationTest.java`
- [NEW] `application-management/src/test/java/com/openapp/application/integration/StatusChangeIntegrationTest.java`
- [NEW] `application-management/src/test/java/com/openapp/application/integration/AuthIntegrationTest.java`

**验收标准**:
- [ ] 创建应用完整流程测试
- [ ] 查询应用完整流程测试
- [ ] 更新应用完整流程测试
- [ ] 删除应用完整流程测试
- [ ] 状态变更完整流程测试
- [ ] 权限校验集成测试
- [ ] 缓存集成测试
- [ ] 数据库事务测试

**验证命令**:
```bash
./mvnw test -Dtest=*IntegrationTest
```

---

### TASK-014: 文档与配置完善

**优先级**: P2  
**复杂度**: S  
**预估工时**: 3 小时  
**执行波次**: 5  
**前置依赖**: 所有任务完成

**描述**:
完善项目配置和文档。

**涉及文件**:
- [NEW] `application-management/src/main/resources/application-dev.yml`
- [NEW] `application-management/src/main/resources/application-prod.yml`
- [NEW] `application-management/README.md` (模块说明)
- [NEW] `.specs/application-management/api.md` (API 文档)
- [MODIFY] `application-management/src/main/resources/application.yml`
- [MODIFY] `.opencode/sdd/state.json` (更新状态)
- [MODIFY] `.specs/application-management/spec.json` (更新状态)

**验收标准**:
- [ ] 开发环境配置完整
- [ ] 生产环境配置完整
- [ ] 环境变量配置说明
- [ ] API 文档完整
- [ ] state.json 更新为 tasked
- [ ] 启动类配置正确

**验证命令**:
```bash
./mvnw spring-boot:run
# 验证应用启动
curl http://localhost:8080/actuator/health
```

---

## 任务依赖图

```
TASK-001 (数据库迁移) ──┬──> TASK-004 (Mapper) ──> TASK-005 (Repository) ──┬──> TASK-007 (Service) ──┬──> TASK-008 (Controller) ──> TASK-013 (集成测试)
                        │                                                  │                         │
TASK-002 (领域层) ──────┤──> TASK-006 (异常) ──────────────────────────────┤──> TASK-009 (安全) ──────┤
                        │                                                  │                         │
TASK-003 (DTO) ─────────┘                                                  │──> TASK-010 (缓存) ──────┘
                                                                           │
TASK-002 (领域层) ─────────────────────────────────────────────────────────┘──> TASK-011 (审计日志)

                                                                                      │
                                                                                      ▼
所有任务完成 ───────────────────────────────────────────────────────────────────> TASK-014 (文档)
                                                                                      │
                                                                                      ▼
所有测试完成 ───────────────────────────────────────────────────────────────────> TASK-012 (单元测试)
```

---

## 任务优先级汇总

| 优先级 | 任务数量 | 任务列表 |
|--------|----------|----------|
| **P0** | 9 | T001, T002, T003, T004, T005, T007, T008, T009, T012 |
| **P1** | 4 | T006, T010, T011, T013 |
| **P2** | 1 | T014 |

---

## 任务复杂度分布

| 复杂度 | 任务数量 | 任务列表 |
|--------|----------|----------|
| **S** | 8 | T002, T003, T004, T005, T006, T011, T014 |
| **M** | 6 | T001, T007, T008, T009, T010, T012, T013 |
| **L** | 0 | - |

---

## 执行波次汇总

| 波次 | 任务数量 | 任务列表 | 预估工时 |
|------|----------|----------|----------|
| **Wave 1** | 3 | T001, T002, T003 | 7 小时 |
| **Wave 2** | 3 | T004, T005, T006 | 5 小时 |
| **Wave 3** | 5 | T007, T008, T009, T010, T011 | 20 小时 |
| **Wave 4** | 2 | T012, T013 | 14 小时 |
| **Wave 5** | 1 | T014 | 3 小时 |

---

## 验收标准汇总

### 功能验收
- [ ] 能够成功创建应用
- [ ] 能够修改应用信息
- [ ] 能够查看应用列表和详情
- [ ] 能够删除应用（软删除）
- [ ] 能够变更应用状态（管理员）
- [ ] 权限控制正确（只能管理自己的应用）

### 非功能验收
- [ ] 应用查询响应时间 P95 < 100ms
- [ ] 支持 1000 并发请求
- [ ] 所有错误有清晰的错误码
- [ ] 审计日志记录完整
- [ ] 单元测试覆盖率 ≥ 60%
- [ ] 集成测试覆盖率 ≥ 30%

---

## 下一步行动

1. 确认任务分解无误
2. 更新状态文件
3. 开始实现任务（按波次顺序）
4. 每个任务完成后标记为完成
5. 所有任务完成后进入 Review 阶段

---

**状态**: `planned` → `tasked`  
**下一步**: 开始实现任务（使用 @sdd-4-build 构建每个任务）
