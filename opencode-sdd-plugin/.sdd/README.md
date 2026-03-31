# SDD Workspace

## 目录结构

### 运行时结构（安装部署）

```
.sdd/
├── README.md              # 本文件 - SDD 工作空间说明
├── ROADMAP.md             # 版本路线图（@sdd-roadmap 生成）
├── config.json            # SDD 配置（可选）
└── .specs/                # 规范文件目录
    ├── README.md          # 目录说明
    └── [feature]/         # Feature 目录
        ├── README.md      # Feature 导航
        ├── spec.md        # 需求规格
        ├── plan.md        # 技术规划
        ├── tasks.md       # 任务分解
        └── state.json     # 状态文件
```

### 开发结构（当前项目）

```
.sdd/
├── README.md              # 本文件
├── ROADMAP.md             # 版本路线图
├── config.json            # SDD 配置
├── src/                   # 开发源代码（v1.2.11 容器化功能）
│   ├── sdd-multi-module.ts    # 主入口模块
│   ├── state/                 # 状态管理
│   │   ├── manager.ts         # 状态管理器
│   │   ├── migrator.ts        # 状态迁移
│   │   └── schema-v1.2.11.ts  # State Schema v1.2.11
│   └── utils/                 # 工具类
│       ├── subfeature-manager.ts  # 子 Feature 管理器
│       ├── tasks-parser.ts        # 任务解析器（支持并行/串行）
│       ├── dependency-graph.ts    # 依赖关系图
│       ├── dependency-notifier.ts # 依赖通知
│       ├── workspace.ts           # 工作空间识别
│       └── compatibility.ts       # 向前/向后兼容
├── tests/                 # 测试代码
│   └── e2e/               # 端到端测试
│       ├── e2e-test.fixture.ts    # 测试夹具
│       └── main-feature-flow.test.ts  # 主流程测试
└── .specs/
    └── [feature]/         # Feature 目录
```

## 快速开始

### 使用 SDD 工作流

1. 使用 `@sdd 开始 [feature 名称]` 开始新 feature
2. 规范文件将自动创建在 `.sdd/.specs/` 目录
3. 文档会自动维护（@sdd-docs），无需手动创建 README

### 开发 v1.2.11 容器化功能

当前 `.sdd/src/` 包含正在开发的 v1.2.11 容器化功能源代码：

- **状态管理**: `.sdd/src/state/manager.ts` - 分布式 State 管理
- **子 Feature 管理**: `.sdd/src/utils/subfeature-manager.ts` - 子 Feature 目录管理
- **任务解析**: `.sdd/src/utils/tasks-parser.ts` - 支持并行/串行任务组
- **依赖通知**: `.sdd/src/utils/dependency-notifier.ts` - 依赖变更自动通知
- **工作空间识别**: `.sdd/src/utils/workspace.ts` - 优先级：环境变量 > `.sdd/` > `.specs/`

**测试**:
```bash
npm test  # 运行单元测试
npm run test:e2e  # 运行端到端测试
```

## Agents

- `@sdd` - 智能入口（自动路由到正确阶段）
- `@sdd-docs` - 目录导航生成器（自动触发，维护所有 README）
- `@sdd-roadmap` - Roadmap 规划专家（多版本路线图）
- `@sdd-help` - 帮助助手

## 安装说明

**安装脚本只部署运行时**，不部署开发源代码：

```bash
bash install.sh <目标项目目录>
```

部署内容：
- ✅ `.opencode/plugins/sdd/` - 插件运行时（dist/）
- ✅ `.opencode/agents/` - 16 个 Agent 定义
- ✅ `.sdd/` - SDD 工作空间容器（含 README）
- ✅ `.sdd/.specs/` - 规范文件目录（含 README）
- ❌ 不部署 `.sdd/src/` 和 `.sdd/tests/`（开发代码）

## 版本

| 版本 | 状态 | 说明 |
|------|------|------|
| **v1.1.1** | ✅ 已完成 | 16 个 Agent + 基础工作流 |
| **v1.2.11** | 🔄 开发中 | 容器化支持（`.sdd/src/`） |
| **v1.3.0** | 📋 规划中 | 状态面板 + Git Hooks |

---

> **注意**: `.sdd/src/` 和 `.sdd/tests/` 是 v1.2.11 开发目录，安装脚本不会部署到目标项目。
