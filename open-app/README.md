# open-app 项目总体文档

## 📖 项目概述

**open-app** 是一个企业级应用开放平台，提供多个子模块来支持企业应用的开发、管理和集成。

### 核心定位

- **应用开放平台** - 提供企业应用开发、部署、管理的全流程支持
- **权限管理中心** - 统一的权限管理服务（permission-app 子模块）
- **集成网关** - 与飞书等第三方平台深度集成
- **SDD 实践** - 基于 Specification-Driven Development 方法论构建

---

## 🏗️ 子模块架构

```
open-app/
├── permission-app         # 权限管理子模块（核心）
│   ├── RBAC 核心          # 角色权限控制
│   ├── 认证服务          # OAuth 2.0 / JWT
│   └── 审计日志          # 操作记录与合规
│
├── identity-app           # 身份管理子模块（规划中）
│   ├── 用户管理
│   ├── 组织架构
│   └── 外部 IdP 集成
│
├── gateway-app            # API 网关子模块（规划中）
│   ├── 路由管理
│   ├── 限流熔断
│   └── 请求转发
│
└── admin-app              # 管理控制台子模块（规划中）
    ├── 仪表盘
    ├── 配置管理
    └── 监控告警
```

---

## 📦 子模块详情

### 1. permission-app（权限管理）

**状态**: 🟢 开发中  
**版本**: v1.0.0 (MVP)

#### 核心功能
| 功能 | 说明 | 优先级 |
|------|------|--------|
| RBAC 核心 | 角色 - 权限三层模型 | P0 |
| 用户认证 | OAuth 2.0 / JWT | P0 |
| 权限检查 API | 服务端权限验证 | P0 |
| 审计日志 | 敏感操作记录 | P1 |
| 数据级权限 | 部门/项目维度 | P1 |

#### 技术栈
- **语言**: TypeScript
- **运行时**: Node.js >= 18.0
- **框架**: Node.js (待定)

#### 文档
- [权限架构设计](.specs/architecture/adr/ADR-001.md)
- [飞书集成文档](docs/Feishu/)

---

### 2. identity-app（身份管理）- 规划中

**状态**: 📋 待开始  
**预计版本**: v1.1.0

#### 计划功能
- 用户 CRUD 管理
- 组织架构树
- LDAP/AD 集成
- SSO 单点登录

---

### 3. gateway-app（API 网关）- 规划中

**状态**: 📋 待开始  
**预计版本**: v2.0.0

#### 计划功能
- API 路由配置
- 请求限流
- 熔断降级
- 负载均衡

---

### 4. admin-app（管理控制台）- 规划中

**状态**: 📋 待开始  
**预计版本**: v1.1.0

#### 计划功能
- 可视化仪表盘
- 子模块配置管理
- 系统监控
- 告警通知

---

## 🛠️ 开发方法论

### SDD (Specification-Driven Development)

本项目采用 **SDD** 开发方法论，强调规范先行。

#### 开发流程
```
1. 规范编写 (@sdd-spec)
   ↓
2. 技术规划 (@sdd-plan)
   ↓
3. 任务分解 (@sdd-tasks)
   ↓
4. 代码实现 (@sdd-build)
   ↓
5. 代码审查 (@sdd-review)
   ↓
6. 最终验证 (@sdd-validate)
```

#### OpenCode 命令
```bash
# 开始新 feature
opencode @sdd "开始 用户登录"

# 继续工作
opencode @sdd "继续"

# 查看状态
opencode @sdd "状态"

# 查看帮助
opencode @sdd "帮助"
```

---

## 📁 项目结构

```
open-app/
├── .opencode/                    # OpenCode 配置
│   ├── agents/                   # AI Agent 定义
│   │   ├── sdd-1-spec.md        # 规范编写 Agent
│   │   ├── sdd-2-plan.md        # 技术规划 Agent
│   │   ├── sdd-3-tasks.md       # 任务分解 Agent
│   │   ├── sdd-4-build.md       # 代码实现 Agent
│   │   ├── sdd-5-review.md      # 代码审查 Agent
│   │   └── sdd-6-validate.md    # 最终验证 Agent
│   ├── sdd/                      # SDD 工作流配置
│   │   └── state.json           # 状态机
│   ├── constitution.md          # 项目宪法（不可协商原则）
│   └── package.json             # OpenCode 依赖
│
├── .specs/                       # 规范文档
│   ├── project/                  # 项目配置
│   │   └── project.yaml
│   ├── architecture/             # 架构设计
│   │   └── adr/                 # 架构决策记录
│   ├── planning/                 # 规划文档
│   │   ├── roadmap.md           # 路线图
│   │   └── tasks.md             # 任务列表
│   ├── development/              # 开发指南
│   │   ├── docs.md
│   │   └── prompts.md
│   ├── quality/                  # 质量标准
│   │   └── tests.md
│   └── examples/                 # 示例规范
│       └── role-management.md
│
├── docs/                         # 项目文档
│   └── Feishu/                   # 飞书集成文档
│       ├── README.md
│       ├── 认证及授权/
│       ├── API 调用流程/
│       └── 事件/
│
├── README.md                     # 项目简介（本文档）
├── DEV_QUICKSTART.md             # 开发快速指南
└── DEV_WORKFLOW.md               # 开发工作流说明
```

---

## 🗺️ 路线图

### v1.0.0 - MVP (2026-Q2)
**主题**: permission-app 核心功能

| 模块 | Feature | 状态 |
|------|---------|------|
| permission-app | 用户认证系统 | 📋 待开始 |
| permission-app | 角色管理 | 📋 待开始 |
| permission-app | RBAC 核心逻辑 | 📋 待开始 |
| permission-app | 基础 API | 📋 待开始 |

### v1.1.0 - 增强 (2026-Q3)
**主题**: permission-app 增强 + admin-app 启动

| 模块 | Feature | 状态 |
|------|---------|------|
| permission-app | 数据级权限 | 📋 待开始 |
| permission-app | 审计日志 | 📋 待开始 |
| admin-app | 管理控制台 | 📋 待开始 |

### v2.0.0 - 扩展 (2026-Q4)
**主题**: identity-app + gateway-app

| 模块 | Feature | 状态 |
|------|---------|------|
| identity-app | 用户管理 | 📋 待开始 |
| identity-app | 外部 IdP 集成 | 📋 待开始 |
| gateway-app | API 网关 | 📋 待开始 |

---

## 🔐 核心原则

### 项目宪法约束

所有子模块必须遵守 `.opencode/constitution.md` 定义的原则：

1. **服务端验证** - 所有权限检查必须在服务端执行
2. **审计日志** - 敏感操作必须有审计日志
3. **最小权限** - 遵循最小权限原则
4. **默认拒绝** - 默认拒绝，显式允许
5. **规范先行** - 规范必须先于实现

---

## 🧪 测试策略

### 测试层级
| 层级 | 范围 | 覆盖率要求 |
|------|------|------------|
| 单元测试 | 各子模块内部 | ≥ 80% |
| 集成测试 | 子模块间接口 | 100% |
| E2E 测试 | 完整业务流程 | 核心流程 100% |

### 权限测试矩阵
- 角色 × 权限 组合测试
- 越权访问测试
- 并发权限变更测试

---

## 📦 部署架构

### 环境规划
```
┌─────────────────────────────────────────────────────────────┐
│  User Layer: 用户 │ 管理员 │ 开发者                          │
├─────────────────────────────────────────────────────────────┤
│  Application Layer: permission-app │ identity-app │ ...     │
├──────────────────┬──────────────────────────────────────────┤
│  Auth Layer      │           Event Layer                     │
│  • app_token     │   • 用户事件 (contact.*)                 │
│  • tenant_token  │   • 消息事件 (im.*)                      │
│  • user_token    │   • 应用事件 (app.*)                     │
│  (OAuth 2.0)     │                                           │
├─────────────────────────────────────────────────────────────┤
│  API Layer: 联系人 │ 消息 │ 云文档 │ 日历                     │
├─────────────────────────────────────────────────────────────┤
│  Security: IP 白名单 │ 凭证加密 │ 事件签名                   │
└─────────────────────────────────────────────────────────────┘
```

---

## 🤝 贡献指南

### 提交代码前

1. 阅读 [项目宪法](.opencode/constitution.md)
2. 遵循 SDD 工作流
3. 确保测试通过
4. 更新相关文档

### 代码审查清单

- [ ] 代码符合项目宪法
- [ ] 权限检查在服务端执行
- [ ] 敏感操作有审计日志
- [ ] 遵循最小权限原则
- [ ] 单元测试覆盖

---

## 📚 文档导航

| 文档 | 路径 | 说明 |
|------|------|------|
| **快速指南** | `DEV_QUICKSTART.md` | 30 秒上手开发 |
| **开发工作流** | `DEV_WORKFLOW.md` | 完整开发流程说明 |
| **项目宪法** | `.opencode/constitution.md` | 不可协商原则 |
| **路线图** | `.specs/planning/roadmap.md` | 版本规划 |
| **架构决策** | `.specs/architecture/adr/` | 技术决策记录 |
| **飞书集成** | `docs/Feishu/` | 飞书平台集成文档 |

---

## 📞 联系方式

- **项目仓库**: github.com/THZSummer/Product
- **问题反馈**: 提交 Issue

---

**版本**: 1.0.0  
**创建日期**: 2026-03-20  
**最后更新**: 2026-03-23  
**项目**: open-app
