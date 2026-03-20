# SDD 快速开始指南

## 🚀 30 秒上手

### 方式 1: 智能入口（推荐 ⭐）

```bash
# 开始新 feature
opencode @sdd "开始 用户登录"

# 继续当前工作
opencode @sdd "继续"

# 查看状态
opencode @sdd "状态"

# 查看帮助
opencode @sdd "帮助"
opencode @sdd-help        # 别名，效果相同
```

### 方式 2: 直接使用子 agents

```bash
# 6 个阶段，按顺序使用
opencode @sdd-spec "用户登录规范"
opencode @sdd-plan "用户登录计划"
opencode @sdd-tasks "用户登录任务"
opencode @sdd-build "实现 TASK-001"
opencode @sdd-review "审查用户登录"
opencode @sdd-validate "验证用户登录"
```

---

## 📋 完整命令列表

### 高频命令

| 命令 | 说明 | 示例 |
|------|------|------|
| `@sdd 开始 [名称]` | 开始新 feature | `@sdd 开始 用户登录` |
| `@sdd 继续` | 继续当前工作 | `@sdd 继续` |
| `@sdd 状态` | 查看进度 | `@sdd 状态` |
| `@sdd 帮助` | 查看帮助 | `@sdd 帮助` |

### 阶段跳转

| 命令 | 阶段 | 说明 |
|------|------|------|
| `@sdd spec [名称]` | 1/6 | 规范编写 |
| `@sdd plan [名称]` | 2/6 | 技术规划 |
| `@sdd tasks [名称]` | 3/6 | 任务分解 |
| `@sdd build [TASK]` | 4/6 | 任务实现 |
| `@sdd review [名称]` | 5/6 | 代码审查 |
| `@sdd validate [名称]` | 6/6 | 最终验证 |

---

## 🎯 工作流

```
1.spec → 2.plan → 3.tasks → 4.build → 5.review → 6.validate
```

### 阶段说明

| # | 阶段 | Agent | 输出 |
|---|------|-------|------|
| 1 | 规范编写 | @sdd-spec | `.specs/[feature]/spec.md` |
| 2 | 技术规划 | @sdd-plan | `.specs/[feature]/plan.md` |
| 3 | 任务分解 | @sdd-tasks | `.specs/[feature]/tasks.md` |
| 4 | 任务实现 | @sdd-build | 代码、测试 |
| 5 | 代码审查 | @sdd-review | 审查报告 |
| 6 | 最终验证 | @sdd-validate | 验证报告 |

---

## 💡 使用技巧

### 新手建议
1. **从 @sdd 开始** - 使用智能入口，自动引导
2. **按顺序执行** - 不要跳过阶段
3. **查看状态** - 随时用 `@sdd 状态` 查看进度

### 高级用法
1. **直接跳转** - `@sdd build TASK-001` 直接实现任务
2. **批量实现** - `@sdd-build 实现 TASK-001 和 TASK-002`
3. **跳过审查** - 紧急情况下直接 `@sdd validate`

---

## 📚 更多文档

- **完整说明**: `SDD_README.md`
- **项目宪法**: `.opencode/constitution.md`
- **示例规范**: `.specs/examples/role-management.md`
- **开发指南**: `.specs/development/docs.md`

---

**版本**: 1.0.0  
**创建日期**: 2026-03-20
