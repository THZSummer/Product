# 🚀 OpenCode SDD Plugin 安装指南

## 📝 配置说明

### 模型配置（`opencode.json`）

所有 Agent 的**模型**配置在 `opencode.json` 中，便于用户根据需求调整：

```json
{
  "agent": {
    "sdd-1-spec": {
      "model": "bailian/qwen3.5-plus"  // 修改这里
    }
  }
}
```

**默认模型分配**：
- **规范/规划/任务** (阶段 1-3): `qwen3.5-plus` - 适合文本分析
- **实现/审查/验证** (阶段 4-6): `qwen3-coder-plus` - 适合代码任务

### Temperature 配置（agent md 文件）

**temperature** 保留在各 agent 的 md 文件 frontmatter 中，用户一般无需修改：

| Agent | Temperature | 说明 |
|-------|-------------|------|
| spec/plan/tasks | 0.1-0.3 | 较低温度，输出更稳定 |
| build/review | 0.2-0.3 | 平衡创造性与准确性 |
| validate/help | 0.1-0.3 | 较低温度，确保准确性 |

---

## ✅ 快速开始

### 一键安装（推荐）

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

---

## 详细安装说明

### 步骤 1: 构建插件

```bash
cd opencode-sdd-plugin
npm install
npm run build
```

### 步骤 2: 复制到项目

```bash
# Linux/macOS
cp -r dist/* <your-project>/.opencode/plugins/sdd/

# Windows PowerShell
Copy-Item -Recurse -Force dist\* <your-project>\.opencode\plugins\sdd\
```

### 步骤 3: 配置项目

在项目的 `opencode.json` 中添加：

```json
{
  "$schema": "https://opencode.ai/config.json",
  "plugins": [
    {
      "name": "opencode-sdd-plugin",
      "path": "./.opencode/plugins/sdd"
    }
  ]
}
```

### 步骤 4: 重启 OpenCode

```bash
cd <your-project>
opencode
```

---

## 详细安装说明

### 方法 1: 项目级安装（推荐）

适合在单个项目中使用 SDD 工作流。

#### 1. 创建插件目录

```bash
cd <your-project>
mkdir -p .opencode/plugins/sdd
```

#### 2. 复制插件文件

```bash
# 从插件源码目录复制
cp -r opencode-sdd-plugin/dist/* .opencode/plugins/sdd/
cp opencode-sdd-plugin/package.json .opencode/plugins/sdd/
```

#### 3. 配置 opencode.json

```json
{
  "$schema": "https://opencode.ai/config.json",
  "plugins": [
    {
      "name": "opencode-sdd-plugin",
      "path": "./.opencode/plugins/sdd"
    }
  ]
}
```

#### 4. 创建 Agents

在 `.opencode/agents/` 目录创建以下文件：

- `sdd.md` - 智能入口
- `sdd-help.md` - 帮助
- `sdd-1-spec.md` 到 `sdd-6-validate.md` - 6 个阶段 agents

> 💡 提示：可以从 `opencode-sdd-plugin/templates/agents/` 复制模板文件

#### 5. 验证安装

在 OpenCode 中输入：

```
@sdd 帮助
```

应该看到完整的命令列表。

---

### 方法 2: 全局安装（可选）

适合在多个项目中使用 SDD 工作流。

#### 1. 创建全局插件目录

```bash
# Linux/macOS
mkdir -p ~/.opencode/plugins/sdd

# Windows
mkdir %APPDATA%\opencode\plugins\sdd
```

#### 2. 复制插件文件

```bash
# Linux/macOS
cp -r opencode-sdd-plugin/dist/* ~/.opencode/plugins/sdd/

# Windows PowerShell
Copy-Item -Recurse -Force opencode-sdd-plugin\dist\* $env:APPDATA\opencode\plugins\sdd\
```

#### 3. 配置全局 config.json

编辑 `~/.opencode/config.json` 或 `%APPDATA%\opencode\config.json`：

```json
{
  "plugins": [
    {
      "name": "opencode-sdd-plugin",
      "path": "~/.opencode/plugins/sdd"
    }
  ]
}
```

---

## 验证安装

### 检查插件加载

启动 OpenCode 后，应该看到：

```
✅ SDD Plugin 已安装
```

### 测试命令

```bash
# 方式 1: 智能入口
@sdd 帮助

# 方式 2: 直接使用 agents
@sdd-1-spec "测试规范"

# 方式 3: 使用短名
@sdd-spec "测试规范"
```

### 预期输出

```
📚 SDD 工作流 - 完整命令

常用命令
| 命令 | 说明 | 示例 |
|------|------|------|
| @sdd 开始 | 开始新 feature | @sdd 开始 用户登录 |
| @sdd 继续 | 继续当前工作 | @sdd 继续 |
| @sdd 状态 | 查看进度 | @sdd 状态 |
| @sdd 帮助 | 查看帮助 | @sdd 帮助 |
...
```

---

## 快速开始

### 1. 初始化 SDD 工作流

```bash
cd <your-project>

# 创建必要目录
mkdir -p .specs
mkdir -p .opencode/agents
mkdir -p .opencode/sdd
```

### 2. 创建第一个 Feature

```bash
# 使用智能入口
opencode @sdd "开始 用户登录"

# 或直接使用 agents
opencode @sdd-1-spec "用户登录规范"
```

### 3. 跟随工作流

```bash
# 1. 规范编写
opencode @sdd-1-spec "用户登录规范"

# 2. 技术规划
opencode @sdd-2-plan "用户登录计划"

# 3. 任务分解
opencode @sdd-3-tasks "用户登录任务"

# 4. 任务实现
opencode @sdd-4-build "实现 TASK-001"

# 5. 代码审查
opencode @sdd-5-review "审查用户登录"

# 6. 最终验证
opencode @sdd-6-validate "验证用户登录"
```

---

## 故障排除

### 插件未加载

**问题**: OpenCode 启动时没有显示 "✅ SDD Plugin 已安装"

**解决方案**:
1. 检查 `opencode.json` 配置路径是否正确
2. 确认 `dist/` 目录已复制到正确位置
3. 重启 OpenCode

### 命令不识别

**问题**: 输入 `@sdd` 没有反应

**解决方案**:
1. 检查 `.opencode/agents/sdd.md` 是否存在
2. 确认 agent 文件格式正确
3. 重启 OpenCode

### 构建错误

**问题**: `npm run build` 失败

**解决方案**:
```bash
# 清理并重新构建
npm run clean
npm install
npm run build
```

### TypeScript 错误

**问题**: 编译时出现 TypeScript 错误

**解决方案**:
```bash
# 检查 TypeScript 版本
npm list typescript

# 应该是 ^5.0.0
# 如需要，更新版本
npm install typescript@^5.0.0 --save-dev
```

---

## 更新插件

当插件源码更新时：

```bash
# 1. 重新构建
cd opencode-sdd-plugin
npm run build

# 2. 复制到项目
cp -r dist/* <your-project>/.opencode/plugins/sdd/

# 3. 重启 OpenCode
```

---

## 下一步

1. ✅ 完成安装
2. 📖 阅读项目中的 SDD 文档（如果有的话）
3. 🚀 开始第一个 feature：`@sdd 开始 [feature 名称]`
4. 🎯 熟悉完整工作流

---

**需要帮助？** 

- 在 OpenCode 中输入：`@sdd 帮助`
- 或查看 [README.md](./README.md)
- 或提交 Issue
