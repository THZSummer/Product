# opencode-sdd-plugin

OpenCode Specification-Driven Development (SDD) 插件

基于 OpenCode 官方插件规范实现的 SDD 工作流插件，帮助团队通过规范驱动的方式开发高质量软件。

## 📖 什么是 SDD

SDD (Specification-Driven Development) 是一种开发方法论，强调在编写代码之前先创建完整的规范文档。本插件实现了完整的 SDD 工作流：

```
1.spec → 2.plan → 3.tasks → 4.build → 5.review → 6.validate
```

## 🎯 功能特性

- ✅ **智能工作流** - 6 个阶段自动引导
- ✅ **Agent 系统** - 7 个专用 AI agents
- ✅ **状态机管理** - 自动追踪项目进度
- ✅ **规范模板** - 内置标准规范模板
- ✅ **代码审查** - 自动化质量检查
- ✅ **验证报告** - 规范符合性验证

## 📦 安装

### 方法 1: 项目级安装（推荐）

```bash
# 1. 克隆或下载插件源码
git clone <repository-url> opencode-sdd-plugin

# 2. 安装依赖并构建
cd opencode-sdd-plugin
npm install
npm run build

# 3. 复制到目标项目
cp -r dist/* <your-project>/.opencode/plugins/sdd/
```

### 方法 2: 从 npm 安装（即将发布）

```bash
npm install opencode-sdd-plugin
```

## 🚀 使用

### 1. 配置插件

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

### 2. 配置 Agents

在 `.opencode/agents/` 目录创建 agent 定义文件：

```bash
.opencode/agents/
├── sdd.md           # 智能入口
├── sdd-help.md      # 帮助
├── sdd-1-spec.md    # 规范编写 (阶段 1/6)
├── sdd-2-plan.md    # 技术规划 (阶段 2/6)
├── sdd-3-tasks.md   # 任务分解 (阶段 3/6)
├── sdd-4-build.md   # 任务实现 (阶段 4/6)
├── sdd-5-review.md  # 代码审查 (阶段 5/6)
└── sdd-6-validate.md # 最终验证 (阶段 6/6)
```

### 3. 开始使用

```bash
# 方式 1: 智能入口（推荐）
opencode @sdd "开始 用户登录"

# 方式 2: 使用序号 agents
opencode @sdd-1-spec "用户登录规范"
opencode @sdd-2-plan "用户登录计划"
opencode @sdd-3-tasks "用户登录任务"
opencode @sdd-4-build "实现 TASK-001"
opencode @sdd-5-review "审查用户登录"
opencode @sdd-6-validate "验证用户登录"

# 方式 3: 使用短名
opencode @sdd-spec "用户登录规范"
opencode @sdd-plan "用户登录计划"
...
```

## 📁 目录结构

```
opencode-sdd-plugin/
├── src/                      # 源码目录
│   ├── index.ts              # 插件入口
│   ├── agents/
│   │   └── sdd-agents.ts     # Agent 注册
│   ├── commands/
│   │   └── sdd.ts            # 命令定义
│   └── state/
│       └── machine.ts        # 状态机
│
├── dist/                     # 编译输出
├── templates/                # 模板文件
│   ├── agents/               # Agent 模板
│   └── spec.md.hbs           # 规范模板
│
├── package.json
├── tsconfig.json
└── README.md
```

## 🔧 开发

```bash
# 安装依赖
npm install

# 构建
npm run build

# 监听模式
npm run dev

# 清理
npm run clean
```

## 📚 文档

- [官方插件文档](https://opencode.ai/docs/plugins/)
- [SDD_QUICKSTART.md](../../permission-app/SDD_QUICKSTART.md) - 快速开始
- [SDD_README.md](../../permission-app/SDD_README.md) - 完整说明

## 🎯 完整工作流

| 阶段 | Agent | 输入 | 输出 |
|------|-------|------|------|
| 1/6 | @sdd-1-spec | 需求描述 | spec.md |
| 2/6 | @sdd-2-plan | spec.md | plan.md |
| 3/6 | @sdd-3-tasks | plan.md | tasks.md |
| 4/6 | @sdd-4-build | tasks.md | 代码实现 |
| 5/6 | @sdd-5-review | 代码 + 文档 | 审查报告 |
| 6/6 | @sdd-6-validate | 完整 feature | 验证报告 |

## 💡 最佳实践

1. **按顺序执行** - 不要跳过阶段
2. **规范先行** - 先写规范，再写代码
3. **任务原子化** - 每个任务独立可验证
4. **持续验证** - 实现后及时验证

## 📝 示例规范结构

```markdown
# Feature: 用户登录

## 用户故事
作为用户，我想要登录系统，以便访问我的个人数据

## 功能需求
FR-001: 用户可以通过手机号和验证码登录
FR-002: 系统应该验证验证码的正确性

## 非功能需求
NFR-001: 登录响应时间 < 500ms
NFR-002: 支持并发 1000+ 用户登录

## 边界情况
EC-001: 验证码错误时返回明确错误信息
EC-002: 验证码过期后自动失效
```

## ⚠️ 注意事项

- 编译后的 `dist/` 目录不要提交到 Git
- `node_modules/` 目录不要提交到 Git
- 修改源码后需要重新构建

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📄 许可证

MIT License

---

**版本**: 1.0.0  
**构建状态**: ✅ 成功
