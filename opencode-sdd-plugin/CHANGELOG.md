# 📋 SDD Plugin 版本变更日志

所有重要变更将记录在此文件中。

---

## 📚 版本文档索引

| 版本 | 日期 | 类型 | 说明 | 详细文档 |
|------|------|------|------|----------|
| **v1.1.0** | 2026-03-25 | 次版本 | Phase 1 优化 | [v1.1.0-PHASE1.md](./docs/archive/v1.1.0-PHASE1.md) |
| **v1.0.0** | 2026-03-20 | 主版本 | 首发版本 | [v1.0.0-initial.md](./docs/archive/v1.0.0-initial.md) |

### 变更统计

| 版本 | 修改文件数 | 新增文件数 | 删除文件数 | 代码行数变更 |
|------|-----------|-----------|-----------|-------------|
| v1.1.0 | 7 | 3 | 0 | ~800 行 (模板) |
| v1.0.0 | 0 | 15 | 0 | ~2000 行 (初始) |

---

## [未发布]

### 计划中 (Phase 2-3)
- 状态机集成增强
- 前后端对齐检查 Agent
- 多 Feature 并发支持
- 命令系统统一

---

## [1.1.0] - 2026-03-25

### ✨ 新增
- **统一权限配置**: 所有 Agent 模板现在都有标准的 frontmatter 权限配置
- **状态更新提示**: 每个 Agent 完成后提示用户运行 `/tool sdd_update_state`
- **异常处理指导**: 所有 Agent 都包含异常处理表格
- **示例对话**: 所有 Agent 都包含完整的示例对话

### 🔧 优化
- **模板格式统一**: 所有 8 个 Agent 模板遵循统一的结构
- **角色定位清晰**: 每个 Agent 都有明确的角色定位说明
- **输出格式规范**: 每个 Agent 都有标准的输出格式

### 📝 文档
- 新增 `CHANGELOG.md` 版本变更日志
- 新增版本文档索引（集成到 CHANGELOG.md 顶部）

### 🐛 修复
- `sdd-build.md.hbs`: 缺失 frontmatter 配置
- `sdd-review.md.hbs`: 缺失 frontmatter 配置
- `sdd-help.md.hbs`: 缺失 frontmatter 配置

### 📊 修改统计
- 修改文件：7 个 Agent 模板
- 新增文档：4 个
- 代码变更：0 行（仅模板优化）

### 📖 详细文档
- [Phase 1 完成报告](./docs/archive/v1.1.0-PHASE1.md)

---

## [1.0.0] - 2026-03-20

### ✨ 首发版本

#### 核心功能
- **6 阶段 SDD 工作流**: spec → plan → tasks → build → review → validate
- **智能入口 Agent**: `@sdd` 自动路由到正确阶段
- **状态机管理**: 自动追踪 Feature 进度
- **规范模板系统**: Handlebars 模板生成标准规范

#### Agent 系统
- `@sdd` - 智能路由助手
- `@sdd-help` - 帮助助手
- `@sdd-spec` - 规范编写专家 (阶段 1/6)
- `@sdd-plan` - 技术规划专家 (阶段 2/6)
- `@sdd-tasks` - 任务分解专家 (阶段 3/6)
- `@sdd-build` - 任务实现专家 (阶段 4/6)
- `@sdd-review` - 代码审查专家 (阶段 5/6)
- `@sdd-validate` - 验证专家 (阶段 6/6)

#### 插件工具
- `sdd_init` - 初始化 SDD 工作流
- `sdd_specify` - 创建 Feature Specification
- `sdd_status` - 查看 SDD 状态

#### 技术栈
- TypeScript 5.x
- OpenCode Plugin SDK
- Handlebars 模板引擎

---

## 版本说明

### 版本号规则
遵循语义化版本控制 (Semantic Versioning)：`主版本。次版本.修订号`

- **主版本**: 不兼容的 API 变更
- **次版本**: 向后兼容的功能新增
- **修订号**: 向后兼容的问题修复

### Phase 计划

| Phase | 版本 | 状态 | 内容 |
|-------|------|------|------|
| Phase 1 | v1.1.0 | ✅ 完成 | 权限配置统一 + 模板格式统一 |
| Phase 2 | v1.2.0 | ⏳ 计划中 | 状态机集成 + 错误处理 |
| Phase 3 | v1.3.0 | ⏳ 计划中 | 前后端对齐检查 + 命令统一 |
| Phase 4 | v2.0.0 | ⏳ 规划中 | 多 Feature 支持 + 重大重构 |

---

## 升级指南

### v1.0.0 → v1.1.0

**影响**: 无破坏性变更

**操作**:
1. 重新构建插件：`npm run build`
2. 复制到项目：`Copy-Item -Recurse -Force dist\* <project>\.opencode\plugins\sdd\`
3. 重启 OpenCode

**新特性使用**:
- 每个 Agent 完成后会提示运行状态更新命令
- 遇到异常时会显示处理建议
- 示例对话帮助理解如何使用

---

## 参考链接

- [OpenCode 插件文档](https://opencode.ai/docs/plugins/)
- [历史档案](./docs/archive/) - 版本文档详情
- [安装指南](./INSTALL.md)
- [用户手册](./README.md)
