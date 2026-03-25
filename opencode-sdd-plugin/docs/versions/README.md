# 📚 SDD Plugin 版本文档

本目录包含所有版本的详细变更文档。

---

## 📁 目录结构

```
docs/versions/
├── README.md              # 本文件 - 版本文档索引
├── v1.0.0-initial.md      # v1.0.0 首发版本说明
├── v1.1.0-PHASE1.md       # v1.1.0 Phase 1 优化完成报告
└── ...                    # 未来版本
```

---

## 📋 版本列表

| 版本 | 日期 | 类型 | 说明 | 文档 |
|------|------|------|------|------|
| **v1.1.0** | 2026-03-25 | 次版本 | Phase 1 优化 | [v1.1.0-PHASE1.md](./v1.1.0-PHASE1.md) |
| **v1.0.0** | 2026-03-20 | 主版本 | 首发版本 | [v1.0.0-initial.md](./v1.0.0-initial.md) |

---

## 🎯 版本详情

### v1.1.0 - Phase 1 优化 (2026-03-25)

**主题**: 权限配置统一 + 模板格式统一

**核心变更**:
- ✅ 所有 8 个 Agent 模板统一 frontmatter 配置
- ✅ 新增角色定位、输出格式、异常处理、示例对话
- ✅ 每个 Agent 完成后提示状态更新

**修改文件**:
- `templates/agents/sdd-spec.md.hbs`
- `templates/agents/sdd-plan.md.hbs`
- `templates/agents/sdd-tasks.md.hbs`
- `templates/agents/sdd-build.md.hbs` (修复)
- `templates/agents/sdd-review.md.hbs` (修复)
- `templates/agents/sdd-validate.md.hbs`
- `templates/agents/sdd-help.md.hbs` (修复)

**详细文档**: [v1.1.0-PHASE1.md](./v1.1.0-PHASE1.md)

---

### v1.0.0 - 首发版本 (2026-03-20)

**主题**: SDD 工作流基础框架

**核心功能**:
- ✅ 6 阶段 SDD 工作流
- ✅ 8 个专用 Agent
- ✅ 状态机管理
- ✅ 插件工具系统

**详细文档**: [v1.0.0-initial.md](./v1.0.0-initial.md)

---

## 📊 变更统计

| 版本 | 修改文件数 | 新增文件数 | 删除文件数 | 代码行数变更 |
|------|-----------|-----------|-----------|-------------|
| v1.1.0 | 7 | 3 | 0 | ~800 行 (模板) |
| v1.0.0 | 0 | 15 | 0 | ~2000 行 (初始) |

---

## 🔗 相关链接

- [主变更日志](../../CHANGELOG.md)
- [安装指南](../../INSTALL.md)
- [用户手册](../../README.md)
- [OpenCode 插件文档](https://opencode.ai/docs/plugins/)

---

## 📝 文档维护

### 添加新版本文档

1. 在 `docs/versions/` 创建新文件：`v{主版本}.{次版本}.{修订号}-{说明}.md`
2. 更新本索引文件的版本列表
3. 更新 `../../CHANGELOG.md`

### 文档模板

```markdown
# v{版本号} - {版本主题}

**发布日期**: YYYY-MM-DD  
**版本类型**: [主版本 | 次版本 | 修订号]

## 变更摘要
- 变更 1
- 变更 2

## 修改文件
- 文件 1
- 文件 2

## 详细说明
...
```
