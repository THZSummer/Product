# SDD 源代码目录

## 📂 目录简介

存储 SDD 插件 v1.2.11 版本的开发源代码，包含状态管理、工具类等核心功能模块。

## 📁 目录结构

```
src/
├── README.md                    # 本文件
├── sdd-multi-module.ts          # 主入口模块
├── state/                       # 状态管理模块
│   ├── manager.ts               # 状态管理器
│   ├── migrator.ts              # 状态迁移器
│   ├── schema-v1.2.11.ts        # State Schema v1.2.11
│   ├── manager.test.ts          # 状态管理器测试
│   ├── migrator.test.ts         # 迁移器测试
│   └── schema-v1.2.11.test.ts   # Schema 测试
└── utils/                       # 工具类模块
    ├── subfeature-manager.ts    # 子 Feature 管理器
    ├── tasks-parser.ts          # 任务解析器
    ├── dependency-graph.ts      # 依赖关系图
    ├── dependency-notifier.ts   # 依赖通知
    ├── workspace.ts             # 工作空间识别
    ├── compatibility.ts         # 兼容性检查
    └── *.test.ts                # 各模块测试文件
```

## 📄 文件说明

| 文件/目录 | 类型 | 说明 |
|-----------|------|------|
| README.md | 文件 | 本导航文件 |
| sdd-multi-module.ts | 文件 | 主入口模块，多 Feature 支持 |
| state/ | 目录 | 状态管理模块 |
| utils/ | 目录 | 工具类模块 |

## 📖 核心功能

### 状态管理 (state/)
- **manager.ts**: 分布式 State 管理
- **migrator.ts**: State 版本迁移
- **schema-v1.2.11.ts**: 状态 Schema 定义

### 工具类 (utils/)
- **subfeature-manager.ts**: 子 Feature 目录管理
- **tasks-parser.ts**: 任务解析器（支持并行/串行）
- **dependency-graph.ts**: 依赖关系图构建
- **dependency-notifier.ts**: 依赖变更自动通知
- **workspace.ts**: 工作空间识别（优先级：环境变量 > .sdd/ > .specs/）
- **compatibility.ts**: 向前/向后兼容性检查

## 🔗 相关目录

- [上级目录](../) - SDD 工作空间根目录
- [状态管理](./state/) - State 模块
- [工具类](./utils/) - Utils 模块
- [测试目录](../tests/) - 测试代码

---

**最后更新**: 2026-04-01
