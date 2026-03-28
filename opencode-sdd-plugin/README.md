# opencode-sdd-plugin

OpenCode Specification-Driven Development (SDD) 插件

## 📁 项目结构

```
opencode-sdd-plugin/
├── src/                        # 源码目录
│   ├── index.ts                # 插件入口
│   ├── agents/                 # Agent 注册代码
│   ├── commands/               # 命令定义
│   ├── state/                  # 状态机
│   └── templates/              # 模板文件（源码一部分）
│       ├── agents/             # Agent 模板
│       │   ├── sdd-spec.md.hbs
│       │   ├── sdd-plan.md.hbs
│       │   ├── sdd-tasks.md.hbs
│       │   ├── sdd-build.md.hbs
│       │   ├── sdd-review.md.hbs
│       │   ├── sdd-validate.md.hbs
│       │   ├── sdd.md.hbs
│       │   └── sdd-help.md.hbs
│       └── config/             # 配置模板
│           └── opencode.json.hbs
│
├── dist/                       # 构建产物（完整可部署）
│   ├── opencode.json           # 配置模板
│   ├── index.js
│   ├── agents/
│   ├── commands/
│   ├── state/
│   └── templates/agents/       # 生成的 agent 定义（14 个.md）
│
├── build-agents.cjs            # Agent 生成脚本
├── install.ps1                 # 安装脚本 (Windows)
├── install.sh                  # 安装脚本 (Linux/macOS)
├── package.json
└── tsconfig.json
```

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

## 🔨 开发

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
```

## 📚 使用

```bash
# 智能入口
@sdd 开始 用户登录

# 各阶段 agent
@sdd-1-spec "规范"
@sdd-2-plan "计划"
@sdd-3-tasks "任务"
@sdd-4-build "实现"
@sdd-5-review "审查"
@sdd-6-validate "验证"
```

## 🎯 Agent 列表

| Agent | 阶段 | 默认模型 |
|-------|------|----------|
| @sdd | 入口 | qwen3.5-plus |
| @sdd-1-spec | 1/6 规范 | qwen3.5-plus |
| @sdd-2-plan | 2/6 规划 | qwen3.5-plus |
| @sdd-3-tasks | 3/6 任务 | qwen3.5-plus |
| @sdd-4-build | 4/6 实现 | qwen3-coder-plus |
| @sdd-5-review | 5/6 审查 | qwen3-coder-plus |
| @sdd-6-validate | 6/6 验证 | qwen3-coder-plus |

> 💡 **模型配置**: 所有 Agent 的模型配置在 `opencode.json` 中，用户可根据需要修改。

## 📝 模板说明

**源模板**（9 个，位于 `src/templates/`）：

**`agents/`** (8 个):
- `sdd-{spec,plan,tasks,build,review,validate}.md.hbs` - 6 个阶段模板
- `sdd.md.hbs` - 智能入口
- `sdd-help.md.hbs` - 帮助文档

**`config/`** (1 个):
- `opencode.json.hbs` - 配置模板

**构建产物**（`dist/` 包含所有部署需要的文件）：
- `opencode.json` - 配置（从 `config/` 复制）
- `templates/agents/*.md` - 14 个 agent 定义（从 `agents/` 生成）
- `*.js`, `*.d.ts` - TypeScript 编译产物

**生成文件**（14 个，位于 `dist/templates/agents/`）：
- 6 个带序号版：`sdd-{1-6}-{spec,plan,...}.md`
- 6 个短名版：`sdd-{spec,plan,...}.md`
- 2 个特殊：`sdd.md`, `sdd-help.md`

构建脚本自动从 8 个源模板生成 14 个 agent 文件，无需手动维护重复内容。

## 🏗️ 架构说明

### 插件结构（符合 OpenCode 官方规范）

```typescript
export const SDDPlugin = async ({ 
  project,    // 项目信息
  client,     // OpenCode SDK 客户端
  $,          // Bun Shell API
  directory,  // 工作目录
  worktree    // Git worktree 路径
}) => {
  return {
    // Hooks
    "session.created": async (input) => { ... },
    "file.edited": async (input) => { ... },
    
    // 自定义工具
    tool: {
      sdd_init: { ... },
      sdd_specify: { ... }
    }
  };
};
```

### 可用 Hooks

| Hook | 用途 | 状态 |
|------|------|------|
| `session.created` | 会话创建 | ✅ 已实现 |
| `file.edited` | 文件编辑（追踪规范） | ✅ 已实现 |
| `session.idle` | 会话空闲（阶段完成通知） | ⏳ Phase 2 |
| `experimental.session.compacting` | 会话压缩上下文注入 | ⏳ Phase 2 |
| `tui.command.execute` | TUI 命令执行 | ⏳ Phase 2 |
| `tui.toast.show` | 显示 Toast 通知 | ⏳ Phase 2 |

### 自定义工具

| 工具 | 说明 | 参数 |
|------|------|------|
| `sdd_init` | 初始化 SDD 工作流 | - |
| `sdd_specify` | 创建规范 | `feature: string` |
| `sdd_status` | 查看状态 | - |

## 🔄 开发计划

### Phase 1: 基础功能 ✅ 完成 (v1.1.0)

- [x] 6 阶段 SDD 工作流 (spec → plan → tasks → build → review → validate)
- [x] 14 个 Agent (6 阶段 ×2 命名 + 入口 + 帮助)
- [x] 状态机防跳过机制
- [x] `/sdd` 命令系统 (智能路由 + 流程守护)
- [x] 跨平台安装脚本 (Windows + Linux/macOS)
- [x] 插件框架 + 命令定义
- [x] 状态机 + Agent 调用集成
- [x] 权限配置统一 + 模板格式统一

### Phase 2: 增强功能 ⏳ 计划中 (v1.2.0)

基于 [OpenCode 官方文档](https://opencode.ai/docs) 最新能力：

- [ ] **Agent Skills 系统** - `.opencode/skills/*/SKILL.md` 定义可重用行为
- [ ] **TUI 集成** - Plugin Events 支持，阶段完成通知
- [ ] **会话压缩增强** - `experimental.session.compacting` 注入 SDD 上下文
- [ ] **MCP 集成** - [context7](https://mcp.context7.com/mcp) 文档搜索, [gh_grep](https://mcp.grep.app) 代码示例
- [ ] **Structured Output** - JSON Schema 确保规范文件结构一致

### Phase 3: 高级功能 ⏳ 规划中

- [ ] 前后端对齐检查 Agent
- [ ] 多 Feature 并发支持
- [ ] 一键回滚 + 批量操作
- [ ] 导出报告 + Git 集成

## 🔗 参考链接

- [OpenCode 官方文档](https://opencode.ai/docs)
- [OpenCode Plugin 开发](https://opencode.ai/docs/plugins)
- [OpenCode Agent 系统](https://opencode.ai/docs/agents)
- [OpenCode Agent Skills](https://opencode.ai/docs/skills)
- [OpenCode MCP 集成](https://opencode.ai/docs/mcp-servers)
- [OpenCode SDK](https://opencode.ai/docs/sdk)

## 📋 版本历史

详见 [CHANGELOG.md](./CHANGELOG.md)

| 版本 | 日期 | 说明 |
|------|------|------|
| v1.2.0 | 规划中 | Phase 2: Skills + TUI + MCP + Structured Output |
| v1.1.0 | 2026-03-25 | 权限配置统一 + 模板格式统一 |
| v1.0.0 | 2026-03-20 | 首发版本：6 阶段工作流 + 14 个 Agent |

## 📄 许可证

MIT License