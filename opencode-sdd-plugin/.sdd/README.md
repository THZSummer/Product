# SDD 容器化工作空间

现代、可扩展的软件定义开发（Software Defined Development）容器化工作空间。

## 目录结构

```
.sdd/
├── README.md                      # 本文件 - SDD 容器化工作空间说明
├── ROADMAP.md                     # 版本路线图
├── config.json                    # SDD 配置（可选）
├── docs/                          # 文档目录
│   └── migration-guide.md         # 迁移指南
└── .specs/                        # 规范文件隔离目录
    ├── spec.md                    # 主 Feature 规范
    ├── plan.md                    # 整体技术架构
    ├── tasks.md                   # 并行任务分组
    ├── state.json                 # Feature 整体状态
    ├── [sub-feature-id]/          # 子 Feature 目录（同级结构）
    │   ├── spec.md
    │   ├── plan.md
    │   ├── tasks.md
    │   └── state.json
    └── ...
```

## 特性

- **容器化**: `.sdd/` 作为 SDD 工作空间的专用容器，不影响项目本身
- **隔离性**: 规范文件在 `.sdd/.specs/` 目录中完全隔离
- **可扩展**: 支持单 Feature 和多 Sub-Feature 模式
- **向后兼容**: 旧结构项目依然支持
- **子 Feature 模式**: 支持大型项目的并行子 Feature 管理

## 快速开始

1. 使用 `@sdd [feature-name]` 开始新 Feature
2. 规范文件将自动创建在 `.sdd/.specs/[feature-name]/` 目录
3. 采用子 Feature 模式时，子 Feature 存储在同级目录 `.sdd/.specs/[sub-feature-id]/`
4. 文档会自动维护，无需手动创建 README

## 模式

### 单 Feature 模式
适用于相对简单的功能开发，所有规范均在一个 Feature 目录下。

### 多 Sub-Feature 模式  
适用于大型复杂项目，支持拆分为多个子 Feature 并行开发。

## Agents

- `@sdd` - 智能入口，智能识别工作空间并选择适当的操作
- `@sdd-docs` - 目录导航（自动触发）
- `@sdd-roadmap` - Roadmap 规划
- 迁移指南: 查看 [迁移指南](./docs/migration-guide.md) 了解如何从旧结构迁移到新结构

## 配置

可通过 `SDD_WORKSPACE` 环境变量自定义工作空间位置。

## 迁移

需要从旧的 `.specs/` 结构迁移到新的容器化结构？参阅 [迁移指南](./docs/migration-guide.md)。
