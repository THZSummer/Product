# 🚀 OpenCode SDD Plugin 安装指南

## 构建状态

✅ 构建成功！

## 安装步骤

### 方法 1: 在 permission-app 项目中测试

1. **复制插件到项目目录**

```powershell
# 在 permission-app 目录执行
cd F:\Product\permission-app
mkdir -p .opencode/plugins/sdd
Copy-Item -Recurse -Force F:\Product\opencode-sdd-plugin\dist\* .opencode\plugins\sdd\
Copy-Item F:\Product\opencode-sdd-plugin\package.json .opencode\plugins\sdd\
```

2. **修改 opencode.json**

在 `F:\Product\permission-app\opencode.json` 中添加：

```json
{
  "$schema": "https://opencode.ai/config.json",
  "plugins": [
    {
      "name": "opencode-sdd-plugin",
      "path": "./.opencode/plugins/sdd"
    }
  ],
  "agent": {
    "sdd-spec": {
      "description": "SDD Specification Expert",
      "mode": "subagent",
      "prompt": "{file:.opencode/agents/sdd-spec.md}"
    },
    "sdd-plan": {
      "description": "SDD Technical Planning Expert",
      "mode": "subagent",
      "prompt": "{file:.opencode/agents/sdd-plan.md}"
    },
    "sdd-tasks": {
      "description": "SDD Task Decomposition Expert",
      "mode": "subagent",
      "prompt": "{file:.opencode/agents/sdd-tasks.md}"
    },
    "sdd-validate": {
      "description": "SDD Validation Expert",
      "mode": "subagent",
      "prompt": "{file:.opencode/agents/sdd-validate.md}"
    }
  },
  "permission": {
    "edit": "ask",
    "bash": "ask"
  }
}
```

3. **重启 OpenCode**

```bash
cd F:\Product\permission-app
opencode
```

4. **测试插件**

```
/sdd --help
/sdd status
```

---

### 方法 2: 全局安装测试

1. **创建全局插件目录**

```powershell
mkdir -p $env:LOCALAPPDATA\opencode\plugins\sdd
Copy-Item -Recurse -Force F:\Product\opencode-sdd-plugin\dist\* $env:LOCALAPPDATA\opencode\plugins\sdd\
```

2. **修改全局配置**

编辑 `%LOCALAPPDATA%\opencode\config.json` 或 `~/.opencode/config.json`：

```json
{
  "plugins": [
    {
      "name": "opencode-sdd-plugin",
      "path": "C:\\Users\\Summe\\AppData\\Local\\opencode\\plugins\\sdd"
    }
  ]
}
```

---

## 验证安装

在 OpenCode 中运行：

```
/sdd --help
```

应该看到：

```
📖 SDD 工作流帮助

用法：/sdd <command> [arguments]

命令:
| 命令 | 说明 |
|------|------|
| init | 初始化 SDD 工作流 |
| specify | 创建 Feature Specification |
...
```

---

## 快速开始

```bash
# 初始化
/sdd init

# 创建规范
/sdd specify 用户登录功能

# 查看状态
/sdd status
```

---

## 故障排除

### 插件未加载

检查 OpenCode 启动日志，应该看到：
```
✅ SDD Plugin 已安装
```

### 命令不识别

确保 `opencode.json` 配置正确，并且重启了 OpenCode。

### 构建错误

```bash
cd F:\Product\opencode-sdd-plugin
npm run clean
npm run build
```

---

## 下一步

1. 测试基本功能
2. 完善命令实现
3. 添加状态机集成
4. 优化用户体验
