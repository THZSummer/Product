# permission-app - SDD 工作流配置

> **Specification-Driven Development for Permission Management System**
> 
> 基于 OpenCode SDD 框架，专为权限管理系统定制的开发工作流。

---

## 🚀 快速开始

### 前置条件（已完成 ✅）
- [x] SDD Agent 已配置（`.opencode/agents/`）
- [x] 状态机已就绪（`.opencode/sdd/state.json`）
- [x] 目录结构已创建（`.specs/`）
- [x] 项目宪法已定义（`.opencode/constitution.md`）

### 开始第一个 Feature

```bash
# 1. 编写规范
opencode @sdd-spec "开始编写 用户登录 的规范"

# 2. （可选）澄清模糊点
opencode @sdd-spec "澄清 用户登录 规范中的模糊点"

# 3. 技术规划
opencode @sdd-plan "为 用户登录 创建技术计划"

# 4. 任务分解
opencode @sdd-tasks "将 用户登录 计划分解为任务"

# 5. 实现任务
opencode @sdd-build "实现 TASK-001"

# 6. 代码审查
opencode @sdd-review "审查 用户登录 的实现"

# 7. 验证
opencode @sdd-validate "验证 用户登录 的实现"
```

---

## 📋 工作流状态机

```
drafting → specified → clarified → planned → tasked → implementing → validating → completed
```

### 状态说明

| 状态 | 说明 | 前置条件 | 对应 Agent |
|------|------|----------|------------|
| `drafting` | 规范起草中 | 无 | @sdd-spec |
| `specified` | 规范完成 | 完成所有必要章节 | @sdd-spec |
| `clarified` | 模糊点已澄清 | 完成澄清问答 | @sdd-spec |
| `planned` | 技术计划完成 | 外部 API 文档已缓存 | @sdd-plan |
| `tasked` | 任务已分解 | 计划已确认 | @sdd-tasks |
| `implementing` | 实现中 | 任务已分配 | @sdd-build |
| `reviewed` | 代码审查通过 | 代码审查完成 | @sdd-review |
| `validating` | 验证中 | 所有任务完成 | @sdd-validate |
| `completed` | 已完成 | 验证通过 | - |

---

## 🤖 SDD Agents 列表

| Agent | 用途 | 调用方式 | 阶段 |
|-------|------|----------|------|
| `@sdd-spec` | 规范编写 | `opencode @sdd-spec ...` | 规范 |
| `@sdd-plan` | 技术规划 | `opencode @sdd-plan ...` | 规划 |
| `@sdd-tasks` | 任务分解 | `opencode @sdd-tasks ...` | 规划 |
| `@sdd-build` | 任务实现 | `opencode @sdd-build ...` | 实现 |
| `@sdd-review` | 代码审查 | `opencode @sdd-review ...` | 审查 |
| `@sdd-validate` | 最终验证 | `opencode @sdd-validate ...` | 验证 |

> 💡 **所有 SDD Agents 都是专用的**，不依赖 OpenCode 内置 agents，确保工作流完整性和一致性。

---

## 📁 目录结构

```
permission-app/
├── .opencode/
│   ├── agents/              # SDD Agent 配置
│   │   ├── sdd-spec.md
│   │   ├── sdd-plan.md
│   │   ├── sdd-tasks.md
│   │   └── sdd-validate.md
│   ├── sdd/
│   │   ├── state.json       # 状态机
│   │   ├── hooks.json       # Hook 配置
│   │   └── api-docs/        # API 文档缓存
│   └── constitution.md      # 项目宪法（含权限安全规则）
│
├── .specs/
│   ├── [feature-name]/      # Feature 规范目录
│   │   ├── spec.md          # Feature 规范
│   │   ├── plan.md          # 技术计划
│   │   └── tasks.md         # 任务分解
│   ├── architecture/
│   │   └── adr/             # 架构决策记录
│   ├── development/
│   │   ├── docs.md          # 开发指南
│   │   └── prompts.md       # 提示词库
│   ├── planning/
│   │   ├── roadmap.md       # 路线图
│   │   └── tasks.md         # 任务看板
│   ├── project/
│   │   └── project.yaml     # 项目配置
│   ├── quality/
│   │   └── tests.md         # 测试策略（含权限测试）
│   └── examples/
│       └── role-management.md # 示例规范
│
└── docs/                    # 项目文档
```

---

## 🔐 权限管理最佳实践

### 宪法约束
项目宪法定义了不可协商的规则：
- 权限检查必须在服务端执行
- 敏感操作必须有审计日志
- 遵循最小权限原则
- 默认拒绝，显式允许

### 规范编写要点
1. 明确权限粒度（菜单级/功能级/数据级）
2. 定义角色层级关系
3. 说明权限继承规则
4. 描述权限变更流程

### 测试要求
- 权限矩阵测试（角色 × 权限）
- 越权访问测试
- 并发权限变更测试

---

## 📝 规范编写最佳实践

### ✅ 好的需求
```
FR-001: 管理员可以创建新角色并分配权限
NFR-001: 权限检查响应时间 < 50ms
EC-001: 删除内置角色返回 403 Forbidden
```

### ❌ 坏的需求
```
系统应该有权限控制          ← 太模糊
用户可以访问允许的内容      ← 循环定义
处理各种错误情况            ← 不具体
```

---

## 📚 关键文档

| 文档 | 路径 | 说明 |
|------|------|------|
| **主入口** | `SDD_README.md` | 本文件 |
| 开发指南 | `.specs/development/docs.md` | 详细开发流程 |
| 提示词库 | `.specs/development/prompts.md` | AI 交互模板 |
| 示例规范 | `.specs/examples/role-management.md` | 角色管理规范示例 |
| 项目宪法 | `.opencode/constitution.md` | 不可协商原则 |
| 测试策略 | `.specs/quality/tests.md` | 权限测试要求 |
| 路线图 | `.specs/planning/roadmap.md` | 版本规划 |

---

## 🎯 下一步

1. **阅读示例规范**: `.specs/examples/role-management.md`
2. **阅读项目宪法**: `.opencode/constitution.md`
3. **开始第一个 Feature**: 使用 `@sdd-spec` 开始编写规范

---

## 📊 当前状态

| 配置项 | 状态 |
|--------|------|
| SDD Agent | ✅ 已配置 (4 个) |
| 状态机 | ✅ 已就绪 |
| 项目宪法 | ✅ 已定义 |
| 示例规范 | ✅ 已创建 |
| 开发文档 | ✅ 已创建 |

---

**版本**: 1.0.0  
**创建日期**: 2026-03-20  
**项目**: permission-app  
**参考**: [OpenCode SDD Framework](../../../OPENCODE_SDD.md)
