# SDD 规范文件目录

> **用途**: 存储所有 Feature 的规范文档 (spec/plan/tasks)  
> **状态**: 活跃开发中  
> **版本**: 1.2.11

---

## 📁 目录结构

```
.sdd/.specs/
├── README.md                    # 本文件：目录导航和使用指南
├── ROADMAP.md                   # 产品版本路线图
├── state.json                   # 当前活跃 Feature 状态
│
├── [feature]/                   # Feature 目录
│   ├── README.md               # Feature 总览和导航
│   ├── spec.md                 # 需求规格说明
│   ├── plan.md                 # 技术规划文档
│   ├── tasks.md                # 任务分解文档
│   └── state.json              # Feature 状态文件
│
└── .templates/                  # 模板目录（可选）
└── examples/                    # 示例目录（可选）
└── architecture/                # 架构文档（可选）
```

---

## 📂 当前 Feature 列表

| Feature | 状态 | 版本 | 说明 |
|---------|------|------|------|
| [sdd-plugin-baseline](./sdd-plugin-baseline/) | ✅ completed | v1.1.0 | Phase 1 基线功能 |
| [sdd-plugin-phase2](./sdd-plugin-phase2/) | 📋 specified | v1.2.0 | Phase 2 能力增强 |
| [sdd-multi-module](./sdd-multi-module/) | 🔄 implementing | v1.2.11 | 子 Feature 化支持 |
| [sdd-plugin-roadmap](./sdd-plugin-roadmap/) | ✅ completed | v1.2.0 | Roadmap Agent |
| [roadmap-update](./roadmap-update/) | 📋 tasked | - | 路线图更新 |
| [feature-readme-template](./feature-readme-template/) | ✅ completed | v1.0.0 | README 模板生成器 |

## 📂 辅助目录

| 目录 | 用途 |
|------|------|
| [.templates/](./.templates/) | README 模板 |
| [examples/](./examples/) | 使用示例 |
| [architecture/](./architecture/) | 架构文档 |

---

## 🚀 快速开始

### 开始新 Feature

```bash
# 1. 运行 @sdd 开始新 feature
@sdd 开始 [feature-name]

# 2. 自动生成目录结构
.sdd/.specs/[feature-name]/
├── spec.md
├── plan.md
├── tasks.md
└── state.json

# 3. 按流程逐步推进
@sdd spec [feature-name]   # 规范编写
@sdd plan [feature-name]   # 技术规划
@sdd tasks [feature-name]  # 任务分解
@sdd build [TASK-XXX]      # 任务实现
@sdd review [feature-name] # 代码审查
@sdd validate [feature-name] # 最终验证
```

### 查看状态

```bash
# 查看当前 Feature 状态
@sdd 状态

# 查看所有 Feature 进度
@sdd roadmap
```

---

## 📖 文档说明

### spec.md - 需求规格说明
- **用途**: 描述功能目标、用户故事、功能需求
- **阶段**: 1/6 (specified)
- **模板**: 遵循标准 spec 格式

### plan.md - 技术规划文档
- **用途**: 技术架构设计、数据结构、实施计划
- **阶段**: 2/6 (planned)
- **依赖**: 需要 spec.md 完成

### tasks.md - 任务分解文档
- **用途**: 原子任务分解、并行分组、工时评估
- **阶段**: 3/6 (tasked)
- **依赖**: 需要 plan.md 完成

### state.json - 状态文件
- **用途**: 追踪 Feature 当前阶段和进度
- **格式**: JSON
- **自动更新**: 阶段完成时自动更新

---

## 🔧 辅助工具

| 工具 | 用途 | 命令 |
|------|------|------|
| @sdd | SDD 智能入口 | `@sdd 开始 [feature]` |
| @sdd-spec | 规范编写 | `@sdd spec [feature]` |
| @sdd-plan | 技术规划 | `@sdd plan [feature]` |
| @sdd-tasks | 任务分解 | `@sdd tasks [feature]` |
| @sdd-build | 任务实现 | `@sdd build [TASK-XXX]` |
| @sdd-review | 代码审查 | `@sdd review [feature]` |
| @sdd-validate | 最终验证 | `@sdd validate [feature]` |
| @sdd-roadmap | Roadmap 规划 | `@sdd roadmap` |
| @sdd-docs | 目录导航生成 | 自动触发 |

---

## 📊 状态说明

| 状态 | 说明 | 下一阶段 |
|------|------|----------|
| drafting | 规范起草中 | specified |
| specified | 规范完成 | planned |
| planned | 规划完成 | tasked |
| tasked | 任务分解完成 | implementing |
| implementing | 实现中 | reviewed |
| reviewed | 审查通过 | validated |
| validated | 验证通过 | completed |
| completed | 已完成 | - |

---

## ⚠️ 注意事项

1. **不要手动编辑** `state.json`，由系统自动维护
2. **阶段不可跳过**，必须按顺序完成
3. **定期查看** ROADMAP.md 了解整体规划
4. **目录已迁移**: 2026-04-01 从 `.specs/` 迁移到 `.sdd/.specs/`

---

## 🔗 相关文档

- [ROADMAP.md](./ROADMAP.md) - 产品版本路线图
- [SDD 工作流指南](../../docs/sdd-workflow.md) - 完整工作流说明
- [OpenCode 文档](https://opencode.ai/docs/) - OpenCode 官方文档

---

**最后更新**: 2026-04-01 (目录迁移完成)  
**维护者**: SDD 团队
