# SDD 开发指南

本指南说明如何在 open-app 项目中使用 Specification-Driven Development (SDD) 工作流。

## 快速开始

### 1. 初始化 SDD（已完成）
```bash
# 目录结构已创建
# Agent 已配置
# 状态机已就绪
```

### 2. 开始新 Feature

```bash
# 步骤 1: 编写规范
opencode @sdd-spec 开始编写 [feature-name] 的规范

# 步骤 2: 澄清模糊点（可选）
opencode @sdd-spec 澄清 [feature-name] 规范中的模糊点

# 步骤 3: 技术规划
opencode @sdd-plan 为 [feature-name] 创建技术计划

# 步骤 4: 任务分解
opencode @sdd-tasks 将 [feature-name] 计划分解为任务

# 步骤 5: 实现
opencode @build 实现 TASK-001

# 步骤 6: 验证
opencode @sdd-validate 验证 [feature-name] 的实现
```

## 状态流转

```
drafting → specified → clarified → planned → tasked → implementing → validating → completed
```

## 目录结构

```
open-app/
├── .opencode/
│   ├── agents/           # SDD Agent 配置
│   │   ├── sdd-spec.md
│   │   ├── sdd-plan.md
│   │   ├── sdd-tasks.md
│   │   └── sdd-validate.md
│   ├── sdd/
│   │   ├── state.json    # 状态机
│   │   ├── hooks.json    # Hook 配置
│   │   └── api-docs/     # API 文档缓存
│   └── constitution.md   # 项目宪法

.specs/
├── [feature-name]/
│   ├── spec.md       # Feature 规范
│   ├── spec.json     # 机器可读规范
│   ├── plan.md       # 技术计划
│   ├── tasks.md      # 任务分解
│   └── retro.md      # 复盘记录
├── architecture/
│   └── adr/          # 架构决策记录
├── development/
│   ├── docs.md       # 本文件
│   └── prompts.md    # AI 交互日志
├── planning/
│   ├── roadmap.md    # 路线图
│   └── tasks.md      # 任务看板
├── project/
│   └── project.yaml  # 项目配置
└── quality/
    └── tests.md      # 测试策略
```

## 权限管理最佳实践

### RBAC 模型
```
用户 → 角色 → 权限 → 资源
```

### ABAC 模型
```
权限 = f(用户属性，资源属性，环境条件)
```

### 规范编写要点
1. 明确权限粒度（菜单级/功能级/数据级）
2. 定义角色层级关系
3. 说明权限继承规则
4. 描述权限变更流程

## 规范编写最佳实践

### 好的需求
✅ FR-001: 管理员可以创建新角色并分配权限
✅ NFR-001: 权限检查响应时间 < 50ms

### 坏的需求
❌ 系统应该有权限控制
❌ 用户可以访问允许的内容

### 规则
1. 使用唯一标识符（FR-XXX, NFR-XXX, EC-XXX）
2. 必须可测试/可验证
3. 避免模糊词汇（快速、友好、健壮）
4. 明确边界情况

## 外部 API 文档缓存

在规划涉及外部服务的 Feature 前，先缓存 API 文档：

```bash
# 缓存 API 文档
# 将文档保存到 .opencode/sdd/api-docs/[service].json
```

## 任务复杂度定义

| 等级 | 标准 | 执行方式 |
|------|------|----------|
| S | 单文件，<50 行 | 自动批量 |
| M | 多文件，<200 行 | 逐个执行 |
| L | 复杂变更，>200 行 | 人工监督 |

## 验证检查清单

- [ ] 所有 FR 都有对应实现
- [ ] 所有 NFR 都满足
- [ ] 所有 EC 都有处理
- [ ] 符合宪法要求（特别是权限安全）
- [ ] 没有孤立代码
- [ ] 权限逻辑有完整测试覆盖

## 常用命令参考

| 目的 | 命令 |
|------|------|
| 查看状态 | `opencode @sdd-spec 查看当前状态` |
| 开始规范 | `opencode @sdd-spec 开始编写 [feature]` |
| 澄清规范 | `opencode @sdd-spec 澄清 [feature]` |
| 技术规划 | `opencode @sdd-plan 规划 [feature]` |
| 任务分解 | `opencode @sdd-tasks 分解 [feature]` |
| 验证实现 | `opencode @sdd-validate 验证 [feature]` |

---

**版本**: 1.0.0  
**最后更新**: 2026-03-20  
**项目**: open-app
