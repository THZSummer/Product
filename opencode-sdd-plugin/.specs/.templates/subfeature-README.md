# [Sub-Feature Name] Specification

> **用途**: [Sub-Feature 功能简述]  
> **状态**: [drafting/specified/planned/tasked/implementing/reviewed/validated/completed]  
> **父 Feature**: [parent-feature-name]  
> **层级**: sub-feature

---

## 📋 子功能概述

**Sub-Feature ID**: `[subfeature-name]`

**父 Feature**: `[parent-feature]`

**目标**: [一句话描述这个子功能的核心目的]

**业务价值**:
- [价值点 1]
- [价值点 2]
- [价值点 3]

---

## 📁 父级目录结构

```
.specs/[parent-feature]/
├── sub-features/[sub-feature-name]/
│   ├── README.md          # 本文件：Sub-Feature 导航
│   ├── spec.md           # 需求规格 - 问题及方案
│   ├── plan.md           # 技术规划 - 如何实现
│   ├── tasks.md          # 任务分解 - 工作清单
│   └── .state.json       # 状态追踪 - 进度信息
```

---

## 🎯 阶段进展

| 阶段 | 状态 | 说明 |
|------|------|------|
| spec | [ ] | @sdd-spec 待执行 |
| plan | [ ] | @sdd-plan 待执行 |
| tasks | [ ] | @sdd-tasks 待执行 |
| build | [ ] | @sdd-build 待执行 |
| review | [ ] | @sdd-review 待执行 |
| validate | [ ] | @sdd-validate 待执行 |

---

## 🔍 关联信息

### 依赖的 Component/Feature
- [组件/功能列表]

### 影响的其他子功能
- [Sub-feature 列表]

### 被父 Feature 调用方式
- [调用方式/接口]

---

## 📈 当前进度

- **SPEC PHASE**: [ ] 待启动 | [ ] 进行中 | [ ] 已完成
- **PLAN PHASE**: [ ] 待启动 | [ ] 进行中 | [ ] 已完成
- **TASKS PHASE**: [ ] 待启动 | [ ] 进行中 | [ ] 已完成
- **BUILD PHASE**: [ ] 待启动 | [ ] 进行中 | [ ] 已完成
- **REVIEW PHASE**: [ ] 待启动 | [ ] 进行中 | [ ] 已完成
- **VALIDATE PHASE**: [ ] 待启动 | [ ] 进行中 | [ ] 已完成

---

## 🚀 快速命令

```bash
# 进入下一阶段 (在父 Feature 目录下运行)
@sdd spec [parent-feature]/sub-features/[sub-feature-name]     # 编写规格说明
@sdd plan [parent-feature]/sub-features/[sub-feature-name]     # 创建技术计划
@sdd tasks [parent-feature]/sub-features/[sub-feature-name]    # 任务分解
@sdd build [sub-task-id]                                       # 构建特定任务
@sdd review [parent-feature]/sub-features/[sub-feature-name]   # 代码审查
@sdd validate [parent-feature]/sub-features/[sub-feature-name] # 最终验证
```

---

## 🔗 与父 Feature 集成点

**API 接口**:
- [接口列表]

**状态同步**:
- 作为父 Feature 的一部分参与整体状态追踪
- 子功能完成时自动触发父 Feature 状态更新检测

---

**最后更新**: 2026-03-31  
**维护者**: SDD 团队