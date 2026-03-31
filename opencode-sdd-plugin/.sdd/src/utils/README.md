# Utils

## 📂 目录简介

SDD 插件的工具类模块，提供子 Feature 管理、任务解析、依赖通知等核心工具功能。

## 📁 目录结构

```
utils/
├── README.md                    # 本文件
├── subfeature-manager.ts        # 子 Feature 管理器
├── subfeature-manager.test.ts   # 子 Feature 测试
├── tasks-parser.ts              # 任务解析器
├── tasks-parser.test.ts         # 任务解析测试
├── dependency-graph.ts          # 依赖关系图
├── dependency-graph.test.ts     # 依赖图测试
├── dependency-notifier.ts       # 依赖通知
├── dependency-notifier.test.ts  # 依赖通知测试
├── workspace.ts                 # 工作空间识别
├── workspace.test.ts            # 工作空间测试
└── compatibility.ts             # 兼容性检查
└── compatibility.test.ts        # 兼容性测试
```

## 📄 文件说明

| 文件/目录 | 类型 | 说明 |
|-----------|------|------|
| README.md | 文件 | 本导航文件 |
| subfeature-manager.ts | 文件 | 子 Feature 目录管理器 |
| tasks-parser.ts | 文件 | 任务解析器（支持并行/串行分组） |
| dependency-graph.ts | 文件 | 依赖关系图构建器 |
| dependency-notifier.ts | 文件 | 依赖变更通知器 |
| workspace.ts | 文件 | 工作空间识别器 |
| compatibility.ts | 文件 | 向前/向后兼容性检查 |

## 📖 核心功能

### Sub-Feature Manager
- Sub-Feature 目录创建和管理
- Feature 层级关系维护
- 目录结构标准化

### Tasks Parser
- tasks.md 文件解析
- 并行/串行任务组识别
- 任务依赖关系提取

### Dependency Graph
- 任务依赖关系图构建
- 依赖链分析
- 循环依赖检测

### Dependency Notifier
- 依赖变更监听
- 自动通知机制
- 级联更新处理

### Workspace
- 工作空间识别
- 优先级：环境变量 > .sdd/ > .specs/
- 路径解析和标准化

### Compatibility
- 向前兼容性检查
- 向后兼容性检查
- 版本兼容性报告

## 🔗 相关目录

- [上级目录](../) - SDD 源代码目录
- [状态管理](../state/) - State 模块
- [测试目录](../../tests/) - 测试代码

---

**最后更新**: 2026-04-01
