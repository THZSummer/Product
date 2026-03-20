# ✅ OpenCode SDD Plugin 重构完成

**日期**: 2026-03-20 17:50  
**依据**: [OpenCode 官方插件文档](https://opencode.ai/docs/plugins/)

---

## 📊 重构状态

| 项目 | 状态 | 说明 |
|------|------|------|
| **插件结构** | ✅ 符合官方规范 | 使用标准插件函数签名 |
| **Hooks 系统** | ✅ 已实现 | session.created, file.edited 等 |
| **自定义工具** | ✅ 已实现 | sdd_init, sdd_specify, sdd_status |
| **日志 API** | ✅ 使用官方 API | `client.app.log()` |
| **Shell API** | ✅ 使用官方 API | `$` (Bun shell) |
| **安装方式** | ✅ 项目级 | `.opencode/plugins/` |
| **配置方式** | ✅ 符合规范 | `opencode.json` 的 `plugin` 字段 |

---

## 🏗️ 插件结构（官方规范）

### 基本结构

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

根据官方文档实现的 Hooks：

| Hook | 用途 | 状态 |
|------|------|------|
| `session.created` | 会话创建 | ✅ 已实现 |
| `file.edited` | 文件编辑（追踪规范） | ✅ 已实现 |
| `tool.execute.before` | 工具执行前 | ⏳ 预留 |
| `tool.execute.after` | 工具执行后 | ⏳ 预留 |
| `tui.command.execute` | TUI 命令执行 | ⏳ 预留 |

### 自定义工具

| 工具 | 说明 | 参数 |
|------|------|------|
| `sdd_init` | 初始化 SDD 工作流 | - |
| `sdd_specify` | 创建规范 | `feature: string` |
| `sdd_status` | 查看状态 | - |

---

## 📦 安装方式

### 项目级安装（当前）

插件位置：
```
F:\Product\permission-app\.opencode\plugins\sdd\
```

配置文件 `opencode.json`：
```json
{
  "$schema": "https://opencode.ai/config.json",
  "plugin": ["opencode-sdd-plugin"]
}
```

### 依赖配置

`.opencode/package.json`：
```json
{
  "dependencies": {
    "zod": "^3.0.0"
  }
}
```

OpenCode 会在启动时自动运行 `bun install` 安装依赖。

---

## 🎯 使用方式对比

### 方式 1: SDD CLI（独立工具）

```bash
sdd init
sdd specify "用户认证"
sdd status
```

**优点**: 跨平台，独立运行  
**缺点**: 需要额外安装

### 方式 2: 插件自定义工具

在 OpenCode TUI 中：
```
/tool sdd_init
/tool sdd_specify {"feature": "用户认证"}
/tool sdd_status
```

**优点**: 集成在 OpenCode 中  
**缺点**: 需要记住工具语法

### 方式 3: 直接使用 Agents

```bash
opencode @sdd-spec "创建规范"
```

**优点**: 简单直接  
**缺点**: 没有工作流管理

---

## 📁 最终目录结构

```
F:\Product\
├── permission-app/              # 目标项目
│   ├── .opencode/
│   │   ├── plugins/
│   │   │   └── sdd/            # ✅ 插件文件
│   │   ├── agents/             # SDD Agents
│   │   └── package.json        # 依赖配置
│   ├── opencode.json           # ✅ 插件配置
│   └── .specs/                 # 规范目录
│
└── opencode-sdd-plugin/         # 插件源码
    ├── src/
    │   └── index.ts            # ✅ 符合官方规范
    ├── dist/
    │   └── index.js            # 编译输出
    ├── package.json
    └── README.md
```

---

## 🔄 与官方规范对齐

### ✅ 完全符合

- [x] 插件函数签名
- [x] Context 参数（project, client, $, directory, worktree）
- [x] Hooks 返回值结构
- [x] 自定义工具定义
- [x] 日志 API (`client.app.log`)
- [x] Shell API (`$`)
- [x] 项目级插件目录
- [x] npm 包配置方式

### ⏳ 后续优化

- [ ] 发布到 npm（可选）
- [ ] 更多 Hooks 集成
- [ ] 状态机持久化
- [ ] 交互式 UI
- [ ] 错误处理完善

---

## 🚀 测试验证

### 1. 重启 OpenCode

```bash
cd F:\Product\permission-app
opencode
```

### 2. 检查插件加载

查看启动日志，应该看到：
```
SDD Plugin loaded
```

### 3. 测试自定义工具

在 OpenCode 中输入：
```
/tool sdd_init
```

### 4. 测试 CLI

```bash
cd F:\Product\permission-app
sdd status
```

---

## 📚 参考文档

- [OpenCode 插件文档](https://opencode.ai/docs/plugins/)
- [OpenCode SDK](https://opencode.ai/docs/sdk)
- [社区插件示例](https://opencode.ai/docs/ecosystem#plugins)

---

## ✅ 结论

**SDD Plugin 已按照官方规范重构完成！**

- ✅ 插件结构符合官方标准
- ✅ 使用官方 API（client.app.log, $）
- ✅ 实现 Hooks 系统
- ✅ 提供自定义工具
- ✅ 项目级安装（无需全局）
- ✅ 配置方式正确

**可以开始测试了！** 🎉
