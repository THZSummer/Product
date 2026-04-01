# [Feature Name] Specification

> **用途**: [Feature 功能简述]  
> **状态**: [drafting/specified/planned/tasked/implementing/reviewed/validated/completed]  
> **对应 Plugin Version**: [version]

---

## 📋 功能概述

**Feature ID**: `[feature-name]`

**目标**: [一句话描述这个功能的核心目的]

**业务价值**:
- [价值点 1]
- [价值点 2]
- [价值点 3]

---

## 📁 目录结构

```
.specs/[feature-name]/
├── README.md          # 本文件：Feature 导航
├── spec.md           # 需求规格 - 问题及方案
├── plan.md           # 技术规划 - 如何实现
├── tasks.md          # 任务分解 - 工作清单
└── .state.json       # 状态追踪 - 进度信息
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

## 🔍 关联功能

### 依赖的 Feature
- [Feature 列表]

### 被依赖于
- [Feature 列表]

### 相关 Feature
- [Feature 列表]

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
# 进入下一阶段
@sdd spec [feature-name]     # 编写规格说明
@sdd plan [feature-name]     # 创建技术计划
@sdd tasks [feature-name]    # 任务分解
@sdd build [task-id]         # 构建特定任务
@sdd review [feature-name]   # 代码审查
@sdd validate [feature-name] # 最终验证
```

---

## 🔧 工具集成

**自动更新**: 
- 每次阶段变更后，[工具名] 自动更新 .state.json
- [工具名] 监控 [监控的事件]
- [工具名] 在 [场合] 自动生成报表

---

**最后更新**: 2026-03-31  
**维护者**: SDD 团队