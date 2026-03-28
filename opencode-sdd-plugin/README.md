# OpenCode SDD Plugin

规范驱动开发 (Specification-Driven Development) 插件，为 OpenCode 提供结构化的 6 阶段工作流。

## 📁 项目结构

```
opencode-sdd-plugin/
├── src/                        # 源码目录（开发者维护）
│   ├── index.ts                # 插件入口
│   ├── agents/                 # Agent 注册代码
│   ├── commands/               # 命令定义
│   ├── state/                  # 状态机
│   └── templates/              # 模板文件（源码一部分）
│       ├── agents/             # Agent 模板
│       └── config/             # 配置模板
│
├── dist/                       # 构建产物（完整可部署）
│   ├── opencode.json           # 配置模板
│   ├── index.js                # 编译后入口
│   ├── agents/                 # 编译后 Agent 代码
│   ├── commands/               # 编译后命令
│   ├── state/                  # 编译后状态机
│   └── templates/agents/       # 生成的 agent 定义（14 个.md）
│
├── .opencode/                  # 安装文件（本地测试用，不应提交）
│   └── plugins/sdd/            # 插件安装目录
│
├── build-agents.cjs            # Agent 生成脚本
├── install.ps1                 # 安装脚本 (Windows)
├── install.sh                  # 安装脚本 (Linux/macOS)
├── package.json
├── tsconfig.json
└── .gitignore
```

**目录说明：**
| 目录 | 用途 | 是否提交 |
|------|------|----------|
| `src/` | 源码 | ✅ 是 |
| `dist/` | 构建产物 | ✅ 是 |
| `.opencode/` | 本地安装测试 | ❌ 否 |

## 🚀 安装

### 一键安装（推荐）

**Linux/macOS:**
```bash
bash install.sh <目标项目目录>
# 或 (确保脚本可执行)
chmod +x install.sh
./install.sh <目标项目目录>
```

⚠️ **注意**: 必须使用 `bash`，不要用 `sh install.sh`！

**Windows:**
```powershell
powershell -ExecutionPolicy Bypass -File "install.ps1" <目标项目目录>
```

### 手动安装

```bash
# 1. 构建
npm install
npm run build

# 2. 复制 dist/ 到目标项目
cp -r dist/* <target-project>/.opencode/plugins/sdd/

# 3. 复制 agents
cp dist/templates/agents/* <target-project>/.opencode/agents/
```

## 🎯 使用方法

### 智能入口

使用 `@sdd` 作为统一入口，自动根据当前状态路由到正确阶段：

```bash
@sdd 开始 用户登录功能
@sdd 继续
@sdd 状态
```

### 分阶段执行

直接调用特定阶段 Agent：

```bash
@sdd-1-spec "用户需要登录和注册功能"
@sdd-2-plan "基于规范制定开发计划"
@sdd-3-tasks "拆解为具体任务"
@sdd-4-build "实现代码"
@sdd-5-review "代码审查"
@sdd-6-validate "验证功能"
```

### 流程守护规则

`/sdd` 命令提供流程守护，防止跳过阶段：

| 命令 | 说明 |
|------|------|
| `/sdd init` | 初始化 SDD 工作流 |
| `/sdd specify <feature>` | 创建规范（阶段 1） |
| `/sdd status` | 查看当前状态 |
| `/sdd reset` | 重置工作流 |

> ⚠️ **防跳过机制**: 状态机确保必须按顺序完成各阶段，无法跳过。

## 📊 6 阶段工作流详解

| 阶段 | Agent | 输入 | 输出 | 默认模型 |
|------|-------|------|------|----------|
| **1. 规范** | `@sdd-1-spec` | 用户需求 | `SDD_SPEC.md` | qwen3.5-plus |
| **2. 规划** | `@sdd-2-plan` | 规范文档 | `SDD_PLAN.md` | qwen3.5-plus |
| **3. 任务** | `@sdd-3-tasks` | 计划文档 | `SDD_TASKS.md` | qwen3.5-plus |
| **4. 实现** | `@sdd-4-build` | 任务清单 | 源代码 | qwen3-coder-plus |
| **5. 审查** | `@sdd-5-review` | 实现代码 | 审查报告 | qwen3-coder-plus |
| **6. 验证** | `@sdd-6-validate` | 审查报告 | 验证结果 | qwen3-coder-plus |

**工作流说明：**
1. **规范阶段**: 将模糊需求转化为结构化技术规范
2. **规划阶段**: 制定技术方案和实现路径
3. **任务阶段**: 拆解为可执行的具体任务
4. **实现阶段**: 编写代码实现功能
5. **审查阶段**: 代码质量检查和改进建议
6. **验证阶段**: 确保功能符合规范

## ⚙️ 配置文件说明

插件行为由 `.opencode/plugins/sdd/opencode.json` 配置：

```json
{
  "agents": {
    "sdd": {
      "model": "qwen3.5-plus"
    },
    "sdd-1-spec": {
      "model": "qwen3.5-plus"
    },
    "sdd-2-plan": {
      "model": "qwen3.5-plus"
    },
    "sdd-3-tasks": {
      "model": "qwen3.5-plus"
    },
    "sdd-4-build": {
      "model": "qwen3-coder-plus"
    },
    "sdd-5-review": {
      "model": "qwen3-coder-plus"
    },
    "sdd-6-validate": {
      "model": "qwen3-coder-plus"
    }
  },
  "mode": "agent"
}
```

**配置项说明：**

| 配置项 | 说明 | 可选值 |
|--------|------|--------|
| `model` | 指定 Agent 使用的模型 | `qwen3.5-plus`, `qwen3-coder-plus` 等 |
| `mode` | Agent 运行模式 | `agent` (默认), `chat` |

**模型选择建议：**
- **qwen3.5-plus**: 适合规范、规划、任务等思考型任务
- **qwen3-coder-plus**: 适合编码、审查、验证等技术型任务

## 🔨 开发命令

```bash
# 安装依赖
npm install

# 构建（agent + TypeScript）
npm run build

# 仅构建 agent
npm run build:agents

# 监听 TypeScript 编译
npm run dev

# 清理构建产物
npm run clean

# 本地测试
npm run test
```

## 📋 版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| v1.2.0 | 规划中 | Phase 2: Skills + TUI + MCP + Structured Output |
| v1.1.0 | 2026-03-25 | 权限配置统一 + 模板格式统一 |
| v1.0.0 | 2026-03-20 | 首发版本：6 阶段工作流 + 14 个 Agent |

详细变更记录请参见 [CHANGELOG.md](./CHANGELOG.md)

## 🔗 参考链接

- [OpenCode 官方文档](https://opencode.ai/docs)
- [OpenCode Plugin 开发](https://opencode.ai/docs/plugins)
- [OpenCode Agent 系统](https://opencode.ai/docs/agents)
- [OpenCode MCP 集成](https://opencode.ai/docs/mcp-servers)

## 📄 许可证

MIT License
